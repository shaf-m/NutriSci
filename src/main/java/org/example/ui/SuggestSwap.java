package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;
import org.jfree.chart.renderer.category.BarRenderer;
import java.awt.Color;


public class SuggestSwap extends JFrame {
    private JLabel goalLabel;
    private JTextArea suggestionsArea;
    private JComboBox<String> mealComboBox;
    private JButton suggestButton, closeButton;
    private int profileId;
    private Map<String, Integer> mealMap = new LinkedHashMap<>(); // Meal label → MealID
    private ChartPanel chartPanel;

    public SuggestSwap(int profileId) {
        this.profileId = profileId;
        setTitle("Suggest Swaps");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(210, 255, 232));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        goalLabel = new JLabel("Your Goal: ");
        mealComboBox = new JComboBox<>();
        suggestionsArea = new JTextArea(10, 40);
        suggestionsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(suggestionsArea);

        suggestButton = new JButton("Suggest Swaps");
        closeButton = new JButton("Close");

        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        topPanel.setBackground(new Color(210, 255, 232));
        topPanel.add(goalLabel);
        topPanel.add(new JLabel("Select Meal:"));
        topPanel.add(mealComboBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(210, 255, 232));
        buttonPanel.add(suggestButton);
        buttonPanel.add(closeButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadGoal();
        loadMeals();

        suggestButton.addActionListener(e -> suggestSwaps());
        closeButton.addActionListener(e -> dispose());

        add(panel);

        // Initialize empty chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Nutrient Comparison",
                "Source",
                "Amount",
                new DefaultCategoryDataset(),
                PlotOrientation.VERTICAL,
                false, true, false);

        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 250));
        panel.add(chartPanel, BorderLayout.EAST);
    }

    private void loadGoal() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT goal_type, nutrient, goal_amount FROM user_profile WHERE ProfileID = ?")) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String goal = rs.getString("goal_type") + " " + rs.getString("nutrient") + " to " + rs.getString("goal_amount");
                goalLabel.setText("Your Goal: " + goal + " grams");
            } else {
                goalLabel.setText("No Goal Found.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            goalLabel.setText("Error loading goal.");
        }
    }

    private void loadMeals() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ml.MealID, ml.MealDate, ml.MealType, GROUP_CONCAT(fn.FoodDescription SEPARATOR ', ') AS Foods " +
                             "FROM meal_log ml " +
                             "JOIN food_name fn ON ml.FoodID = fn.FoodID " +
                             "WHERE ml.ProfileID = ? " +
                             "GROUP BY ml.MealID, ml.MealDate, ml.MealType " +
                             "ORDER BY ml.MealDate DESC, ml.MealID DESC")) {

            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int mealId = rs.getInt("MealID");
                String date = rs.getDate("MealDate").toString();
                String type = rs.getString("MealType");
                String foods = rs.getString("Foods");
                String label = date + " - " + type + " | Food: " + foods + " (Meal ID: " + mealId + ")";
                mealMap.put(label, mealId);
                mealComboBox.addItem(label);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void suggestSwaps() {
        String selectedMeal = (String) mealComboBox.getSelectedItem();
        if (selectedMeal == null) {
            suggestionsArea.setText("Please select a meal first.");
            return;
        }
        int mealId = mealMap.get(selectedMeal);

        // Load user goal from DB
        String goalType = "", nutrient = "";
        double targetAmount = 0;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT goal_type, nutrient, goal_amount FROM user_profile WHERE ProfileID = ?")) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                goalType = rs.getString("goal_type");
                nutrient = rs.getString("nutrient");
                targetAmount = Double.parseDouble(rs.getString("goal_amount"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            suggestionsArea.setText("Error reading user goal.");
            return;
        }

        // Find NutrientID
        int nutrientId = -1;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT NutrientID FROM nutrient_name WHERE NutrientName LIKE ?")) {
            stmt.setString(1, nutrient + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nutrientId = rs.getInt("NutrientID");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            suggestionsArea.setText("Error finding nutrient.");
            return;
        }

        if (nutrientId == -1) {
            suggestionsArea.setText("Nutrient not found.");
            return;
        }

        // Calculate total amount of the nutrient in the current meal
        double oldMealNutrientTotal = 0;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT SUM(na.NutrientValue * ml.Quantity / 100) AS totalNutrient " +
                             "FROM meal_log ml " +
                             "JOIN nutrient_amount na ON ml.FoodID = na.FoodID " +
                             "WHERE ml.MealID = ? AND na.NutrientID = ?")) {
            stmt.setInt(1, mealId);
            stmt.setInt(2, nutrientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                oldMealNutrientTotal = rs.getDouble("totalNutrient");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            suggestionsArea.setText("Error calculating meal's nutrient.");
            return;
        }

        StringBuilder suggestion = new StringBuilder();
        suggestion.append("Suggesting foods to ").append(goalType.toLowerCase())
                .append(" ").append(nutrient.toLowerCase())
                .append(" to ").append(targetAmount).append(" grams.\n\n");
        suggestion.append("Current meal provides: ")
                .append(String.format("%.2f", oldMealNutrientTotal))
                .append(" grams of ").append(nutrient).append(".\n");

        double gap = 0;
        if (goalType.equalsIgnoreCase("increase")) {
            gap = targetAmount - oldMealNutrientTotal;
            if (gap <= 0) {
                suggestion.append("✅ Your current meal already meets or exceeds the goal.\n\n");
                suggestionsArea.setText(suggestion.toString());
                updateChart(oldMealNutrientTotal, 0, nutrient);
                return;
            } else {
                suggestion.append("⬆ You need to increase by at least ")
                        .append(String.format("%.2f", gap))
                        .append(" grams.\n\n");
            }
        } else if (goalType.equalsIgnoreCase("decrease")) {
            gap = oldMealNutrientTotal - targetAmount;
            if (gap <= 0) {
                suggestion.append("✅ Your current meal already meets the goal.\n\n");
                suggestionsArea.setText(suggestion.toString());
                updateChart(oldMealNutrientTotal, 0, nutrient);
                return;
            } else {
                suggestion.append("⬇ You need to reduce by at least ")
                        .append(String.format("%.2f", gap))
                        .append(" grams.\n\n");
            }
        } else {
            suggestion.append("Goal type not recognized.\n");
            suggestionsArea.setText(suggestion.toString());
            return;
        }

        // Only handle "increase" goals for combined suggestions here
        if (goalType.equalsIgnoreCase("increase")) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT f.FoodDescription, n.NutrientValue " +
                                 "FROM food_name f " +
                                 "JOIN nutrient_amount n ON f.FoodID = n.FoodID " +
                                 "WHERE n.NutrientID = ? " +
                                 "AND f.FoodDescription NOT LIKE '%sweet%'" +
                                 "ORDER BY n.NutrientValue DESC " +
                                 "LIMIT 100")) {
                stmt.setInt(1, nutrientId);
                ResultSet rs = stmt.executeQuery();

                double sumProtein = 0;
                while (rs.next() && sumProtein < gap) {
                    String foodDesc = rs.getString("FoodDescription");
                    double val = rs.getDouble("NutrientValue");
                    suggestion.append("- ").append(foodDesc)
                            .append(" (").append(val).append(" grams)\n");
                    sumProtein += val;
                }
                suggestion.append("\nTotal protein in suggested foods: ")
                        .append(String.format("%.2f", sumProtein))
                        .append(" grams.\n");

                updateChart(oldMealNutrientTotal, sumProtein, nutrient);
            } catch (Exception ex) {
                ex.printStackTrace();
                suggestion.append("\n⚠ Error finding food suggestions.");
                suggestionsArea.setText(suggestion.toString());
                return;
            }
        } else {
            // Handle decrease or other goal types - fallback to original 5 food suggestions
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT f.FoodDescription, n.NutrientValue " +
                                 "FROM food_name f " +
                                 "JOIN nutrient_amount n ON f.FoodID = n.FoodID " +
                                 "WHERE n.NutrientID = ? " +
                                 (goalType.equalsIgnoreCase("increase") ?
                                         "AND n.NutrientValue >= ?" :
                                         "AND n.NutrientValue <= ?") +
                                 "AND f.FoodDescription NOT LIKE '%sweet%'" +
                                 " ORDER BY n.NutrientValue " +
                                 (goalType.equalsIgnoreCase("increase") ? "DESC" : "ASC") +
                                 " LIMIT 5")) {
                stmt.setInt(1, nutrientId);
                stmt.setDouble(2, targetAmount);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    suggestion.append("- ")
                            .append(rs.getString("FoodDescription"))
                            .append(" (").append(rs.getDouble("NutrientValue")).append(" grams)\n");
                }
                updateChart(oldMealNutrientTotal, -1, nutrient);
            } catch (Exception ex) {
                ex.printStackTrace();
                suggestion.append("\n⚠ Error finding food suggestions.");
            }
        }

        suggestion.append("\n\nAssuming 100g of each suggested food.\n");
        suggestionsArea.setText(suggestion.toString());
    }

    private void updateChart(double currentMealValue, double suggestedValue, String nutrient) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(currentMealValue, "Nutrient", "Current Meal");
        if (suggestedValue >= 0) {
            dataset.addValue(suggestedValue, "Nutrient", "Suggested Foods Combined");
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Nutrient Comparison",
                "Source",
                "Amount (" + nutrient + ")",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Set custom colors for bars
        renderer.setSeriesPaint(0, new Color(92, 189, 121));
        if (suggestedValue >= 0) {
            renderer.setSeriesPaint(1, new Color(92, 189, 121));
        }

        // Optional: make bars thicker or change item margin
        renderer.setMaximumBarWidth(0.1); // thinner bars
        renderer.setItemMargin(0.1f);     // spacing between bars

        // Set background colors
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        // Make domain axis labels bigger and bold
        plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));

        // Make range axis labels bigger and bold
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 14));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));

        chartPanel.setChart(chart);
    }



}
