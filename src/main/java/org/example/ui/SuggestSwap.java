package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;


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
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        goalLabel = new JLabel("Your Goal: ");
        mealComboBox = new JComboBox<>();
        suggestionsArea = new JTextArea(10, 40);
        suggestionsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(suggestionsArea);

        suggestButton = new JButton("Suggest Swaps");
        closeButton = new JButton("Close");

        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        topPanel.add(goalLabel);
        topPanel.add(new JLabel("Select Meal:"));
        topPanel.add(mealComboBox);

        JPanel buttonPanel = new JPanel();
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
        chartPanel.setPreferredSize(new Dimension(450, 250));
        panel.add(chartPanel, BorderLayout.EAST);
    }

    private void loadGoal() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT goal_type, nutrient, goal_amount FROM user_profile WHERE ProfileID = ?")) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String goal = rs.getString("goal_type") + " " + rs.getString("nutrient") + " by " + rs.getString("goal_amount");
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

        // Find NutrientNameID
        int nutrientId = -1;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT NutrientNameID FROM nutrient_name WHERE NutrientName LIKE ?")) {
            stmt.setString(1, nutrient + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nutrientId = rs.getInt("NutrientNameID");
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
                     "SELECT SUM(na.NutrientValue * ml.Quantity) AS totalNutrient " +
                             "FROM meal_log ml " +
                             "JOIN nutrient_amount na ON ml.FoodID = na.FoodID " +
                             "WHERE ml.MealID = ? AND na.NutrientNameID = ?")) {
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

        // Build suggestion text
        StringBuilder suggestion = new StringBuilder();
        suggestion.append("Suggesting foods to ").append(goalType.toLowerCase())
                .append(" ").append(nutrient.toLowerCase())
                .append(" by ").append(targetAmount).append(" grams.\n\n");

        suggestion.append("Current meal provides: ")
                .append(String.format("%.2f", oldMealNutrientTotal))
                .append(" grams of ").append(nutrient).append(".\n");

        if (goalType.equalsIgnoreCase("increase")) {
            double gap = targetAmount - oldMealNutrientTotal;
            if (gap <= 0) {
                suggestion.append("✅ Your current meal already meets or exceeds the goal.\n\n");
            } else {
                suggestion.append("⬆ You need to increase by at least ")
                        .append(String.format("%.2f", gap))
                        .append(" grams.\n\n");
            }
        } else if (goalType.equalsIgnoreCase("decrease")) {
            double gap = oldMealNutrientTotal - targetAmount;
            if (gap <= 0) {
                suggestion.append("✅ Your current meal already meets the goal.\n\n");
            } else {
                suggestion.append("⬇ You need to reduce by at least ")
                        .append(String.format("%.2f", gap))
                        .append(" grams.\n\n");
            }
        }

        suggestion.append("Suggested foods:\n");

        // Suggest foods based on goal
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT f.FoodDescription, n.NutrientValue " +
                             "FROM food_name f " +
                             "JOIN nutrient_amount n ON f.FoodID = n.FoodID " +
                             "WHERE n.NutrientNameID = ? " +
                             (goalType.equalsIgnoreCase("increase") ?
                                     "AND n.NutrientValue >= ?" :
                                     "AND n.NutrientValue <= ?") +
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
        } catch (Exception ex) {
            ex.printStackTrace();
            suggestion.append("\n⚠ Error finding food suggestions.");
        }

        suggestionsArea.setText(suggestion.toString());
        // Update chart with nutrient comparison
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(oldMealNutrientTotal, "Nutrient", "Current Meal");

        // Use the first suggested food nutrient value (if any)
        double suggestedValue = -1;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT n.NutrientValue FROM food_name f " +
                             "JOIN nutrient_amount n ON f.FoodID = n.FoodID " +
                             "WHERE n.NutrientNameID = ? " +
                             (goalType.equalsIgnoreCase("increase") ?
                                     "AND n.NutrientValue >= ?" :
                                     "AND n.NutrientValue <= ?") +
                             " ORDER BY n.NutrientValue " +
                             (goalType.equalsIgnoreCase("increase") ? "DESC" : "ASC") +
                             " LIMIT 1")) {
            stmt.setInt(1, nutrientId);
            stmt.setDouble(2, targetAmount);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                suggestedValue = rs.getDouble("NutrientValue");
                dataset.addValue(suggestedValue, "Nutrient", "Suggested Food");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Replace old chart
        JFreeChart newChart = ChartFactory.createBarChart(
                "Nutrient Comparison",
                "Source",
                "Amount (" + nutrient + ")",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);
        chartPanel.setChart(newChart);

    }


}
