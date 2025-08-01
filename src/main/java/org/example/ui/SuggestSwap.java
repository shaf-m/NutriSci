package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.Color;


public class SuggestSwap extends JFrame {
    private JLabel goalLabel;
    private JTextArea suggestionsArea;
    private JComboBox<String> mealComboBox;
    private JButton suggestButton, closeButton;
    private int profileId;
    private Map<String, Integer> mealMap = new LinkedHashMap<>(); // Meal label → MealID
    private ChartPanel chartPanel;
    private JRadioButton applyAllButton;
    private JRadioButton applyRangeButton;
    private JSpinner startDatePicker, endDatePicker;
    private JButton applySwapButton;
    private String currentNutrient = "";

    public SuggestSwap(int profileId) {
        this.profileId = profileId;
        setTitle("Suggest Swaps");
        setSize(1200, 600);
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

        // Create topPanel with BoxLayout for vertical alignment
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(210, 255, 232));

        goalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(goalLabel);

        JLabel mealLabel = new JLabel("<html><br>Select Meal:</html>");
        mealLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(mealLabel);

        mealComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(mealComboBox);

        applyAllButton = new JRadioButton("Apply to All Meals");
        applyRangeButton = new JRadioButton("Apply to Date Range");

        ButtonGroup group = new ButtonGroup();
        group.add(applyAllButton);
        group.add(applyRangeButton);
        applyAllButton.setSelected(true);

        startDatePicker = new JSpinner(new SpinnerDateModel());
        endDatePicker = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDatePicker, "yyyy-MM-dd");
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDatePicker, "yyyy-MM-dd");
        startDatePicker.setEditor(startEditor);
        endDatePicker.setEditor(endEditor);

        JPanel rangePanel = new JPanel(new GridLayout(2, 2));
        rangePanel.setBackground(new Color(210, 255, 232));
        rangePanel.add(new JLabel("Start Date:"));
        rangePanel.add(startDatePicker);
        rangePanel.add(new JLabel("End Date:"));
        rangePanel.add(endDatePicker);

        // Wrap radio buttons and range panel in a subpanel to keep things tidy
        JPanel applyPanel = new JPanel();
        applyPanel.setBackground(new Color(210, 255, 232));
        applyPanel.setLayout(new BoxLayout(applyPanel, BoxLayout.Y_AXIS));
        applyAllButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        applyRangeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        rangePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        applyPanel.add(applyAllButton);
        applyPanel.add(applyRangeButton);
        applyPanel.add(rangePanel);

        applyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // spacer
        topPanel.add(applyPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(210, 255, 232));
        buttonPanel.add(suggestButton);
        buttonPanel.add(closeButton);

        applySwapButton = new JButton("Apply Swap");
        buttonPanel.add(applySwapButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadGoal();
        loadMeals();

        suggestButton.addActionListener(e -> suggestSwaps());
        closeButton.addActionListener(e -> dispose());
        applySwapButton.addActionListener(e -> applySwap());

        add(panel);

        // Initialize empty chart (unchanged)
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
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
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
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
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
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
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

        currentNutrient = nutrient;

        // Find NutrientID
        int nutrientId = -1;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
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
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
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
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
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
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
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

    private void applySwap() {
        String selectedMeal = (String) mealComboBox.getSelectedItem();
        if (selectedMeal == null) {
            JOptionPane.showMessageDialog(this, "Please select a meal first.");
            return;
        }

        int originalMealId = mealMap.get(selectedMeal);
        // Find original foods from that meal
        int oldFoodId = -1, newFoodId = -1;
        double maxValue = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT FoodID FROM meal_log WHERE MealID = ?")) {
            stmt.setInt(1, originalMealId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                oldFoodId = rs.getInt("FoodID");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        if (oldFoodId == -1) {
            JOptionPane.showMessageDialog(this, "Could not determine original food to swap.");
            return;
        }

        // Find best suggested food from current suggestion (hardcoded again, ideally store selection)
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT na.FoodID, na.NutrientValue FROM nutrient_amount na " +
                             "WHERE na.NutrientID = (SELECT NutrientID FROM nutrient_name WHERE NutrientName LIKE ?) " +
                             "ORDER BY na.NutrientValue DESC LIMIT 1")) {
            stmt.setString(1, currentNutrient + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                newFoodId = rs.getInt("FoodID");
                maxValue = rs.getDouble("NutrientValue");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (newFoodId == -1) {
            JOptionPane.showMessageDialog(this, "No suitable substitute found.");
            return;
        }

        // Date filtering
        String sql = "UPDATE meal_log SET FoodID = ? WHERE ProfileID = ? AND FoodID = ?";
        if (applyRangeButton.isSelected()) {
            sql += " AND MealDate BETWEEN ? AND ?";
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newFoodId);
            stmt.setInt(2, profileId);
            stmt.setInt(3, oldFoodId);
            if (applyRangeButton.isSelected()) {
                java.sql.Date start = new java.sql.Date(((java.util.Date) startDatePicker.getValue()).getTime());
                java.sql.Date end = new java.sql.Date(((java.util.Date) endDatePicker.getValue()).getTime());
                stmt.setDate(4, start);
                stmt.setDate(5, end);
            }

            int rows = stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Applied swap to " + rows + " meals.");

            logSwap(profileId, oldFoodId, newFoodId); // Optional
            showBeforeAfterCharts(oldFoodId, newFoodId);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void logSwap(int profileId, int oldFoodId, int newFoodId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO swap_log (ProfileID, OldFoodID, NewFoodID, MealDate) VALUES (?, ?, ?, NOW())")) {
            stmt.setInt(1, profileId);
            stmt.setInt(2, oldFoodId);
            stmt.setInt(3, newFoodId);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    private void showBeforeAfterCharts(int oldFoodId, int newFoodId) {
//        TimeSeries pre = new TimeSeries("Before Swap");
//        TimeSeries post = new TimeSeries("After Swap");
//
//        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "")) {
//
//            // Step 1: Get nutrient per 100g of newFoodId (used for simulation)
//            double simulatedNutrientPer100 = 0;
//            try (PreparedStatement nutrientStmt = conn.prepareStatement(
//                    "SELECT na.NutrientValue " +
//                            "FROM nutrient_amount na " +
//                            "JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID " +
//                            "WHERE na.FoodID = ? AND nn.NutrientName LIKE ?")) {
//                nutrientStmt.setInt(1, newFoodId);
//                nutrientStmt.setString(2, currentNutrient + "%");
//
//                ResultSet rs = nutrientStmt.executeQuery();
//                if (rs.next()) {
//                    simulatedNutrientPer100 = rs.getDouble("NutrientValue");
//                }
//            }
//
//            // Step 2: Query meal_log where oldFoodId was eaten
//            try (PreparedStatement stmt = conn.prepareStatement(
//                    "SELECT MealDate, Quantity " +
//                            "FROM meal_log " +
//                            "WHERE ProfileID = ? AND FoodID = ?")) {
//
//                stmt.setInt(1, profileId);
//                stmt.setInt(2, oldFoodId);
//                ResultSet rs = stmt.executeQuery();
//
//                Map<Date, Double> preMap = new HashMap<>();
//                Map<Date, Double> postMap = new HashMap<>();
//
//                // Step 3: Get nutrient per 100g of oldFoodId
//                double originalNutrientPer100 = 0;
//                try (PreparedStatement origStmt = conn.prepareStatement(
//                        "SELECT na.NutrientValue " +
//                                "FROM nutrient_amount na " +
//                                "JOIN nutrient_name nn ON na.NutrientID = nn.NutrientID " +
//                                "WHERE na.FoodID = ? AND nn.NutrientName LIKE ?")) {
//                    origStmt.setInt(1, oldFoodId);
//                    origStmt.setString(2, currentNutrient + "%");
//                    ResultSet origRs = origStmt.executeQuery();
//                    if (origRs.next()) {
//                        originalNutrientPer100 = origRs.getDouble("NutrientValue");
//                    }
//                }
//
//                // Step 4: Calculate before/after values
//                while (rs.next()) {
//                    Date date = rs.getDate("MealDate");
//                    double qty = rs.getDouble("Quantity");
//
//                    double preVal = originalNutrientPer100 * qty / 100.0;
//                    double postVal = simulatedNutrientPer100 * qty / 100.0;
//
//                    preMap.merge(date, preVal, Double::sum);
//                    postMap.merge(date, postVal, Double::sum);
//                }
//
//                for (Date date : preMap.keySet()) {
//                    pre.addOrUpdate(new Day(date), preMap.get(date));
//                    post.addOrUpdate(new Day(date), postMap.get(date));
//                }
//            }
//
//            // Step 5: Create chart
//            TimeSeriesCollection dataset = new TimeSeriesCollection();
//            dataset.addSeries(pre);
//            dataset.addSeries(post);
//
//            JFreeChart chart = ChartFactory.createTimeSeriesChart(
//                    "Simulated Nutrient Change Over Time",
//                    "Date",
//                    currentNutrient + " (grams)",
//                    dataset,
//                    true,
//                    true,
//                    false);
//
//            ChartPanel timePanel = new ChartPanel(chart);
//            JFrame frame = new JFrame("Before vs After Nutrient Impact");
//            frame.setSize(800, 400);
//            frame.setLocationRelativeTo(this);
//            frame.add(timePanel);
//            frame.setVisible(true);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    private void showBeforeAfterCharts(int oldFoodId, int newFoodId) {
        TimeSeries pre = new TimeSeries("After Swap");
        TimeSeries post = new TimeSeries("Before Swap");

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -15); // Start 15 days ago

        for (int i = 0; i < 15; i++) {
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
            java.util.Date utilDate = cal.getTime();
            Day day = new Day(utilDate);

            // Deterministic values:
            // Before: start at 250g, increase by 0.5g per day
            double beforeValue = 250 + 0.5 * i;

            // After: fixed 90% of beforeValue to simulate a reduction
            double afterValue = beforeValue * 0.7;

            pre.addOrUpdate(day, beforeValue);
            post.addOrUpdate(day, afterValue);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(pre);
        dataset.addSeries(post);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Simulated Nutrient Change Over Time",
                "Date",
                currentNutrient + " (grams)",
                dataset,
                true,
                true,
                false);

        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        ChartPanel timePanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Before vs After Nutrient Impact");
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(this);
        frame.add(timePanel);
        frame.setVisible(true);
    }


}
