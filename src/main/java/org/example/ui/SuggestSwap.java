package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class SuggestSwap extends JFrame {
    private JLabel goalLabel;
    private JTextArea suggestionsArea;
    private JComboBox<String> mealComboBox;
    private JButton suggestButton, closeButton;
    private int profileId;
    private Map<String, Integer> mealMap = new LinkedHashMap<>(); // Meal label → MealID

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
    }

    private void loadGoal() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT goal_type, nutrient, goal_amount FROM user_profile WHERE ProfileID = ?")) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String goal = rs.getString("goal_type") + " " + rs.getString("nutrient") + " by " + rs.getString("goal_amount");
                goalLabel.setText("Your Goal: " + goal);
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
                     "SELECT DISTINCT MealID, MealDate, MealType FROM meal_log WHERE ProfileID = ? ORDER BY MealDate DESC, MealID DESC")) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int mealId = rs.getInt("MealID");
                String label = rs.getDate("MealDate") + " - " + rs.getString("MealType") + " (Meal ID: " + mealId + ")";
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

        //Load user goal from DB
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

        //Find NutrientNameID
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

        //Find food suggestions to meet goal
        StringBuilder suggestion = new StringBuilder();
        suggestion.append("Suggesting foods to ").append(goalType.toLowerCase())
                .append(" ").append(nutrient.toLowerCase())
                .append(" by ").append(targetAmount).append(" units.\n\n");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT f.FoodDescription, n.NutrientValue " +
                             "FROM food_name f " +
                             "JOIN nutrient_amount n ON f.FoodID = n.FoodID " +
                             "WHERE n.NutrientNameID = ? " +
                             "AND n.NutrientValue >= ? " +
                             "ORDER BY n.NutrientValue DESC LIMIT 5")) {
            stmt.setInt(1, nutrientId);
            stmt.setDouble(2, targetAmount);  // Minimum value to satisfy goal
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                suggestion.append("- ")
                        .append(rs.getString("FoodDescription"))
                        .append(" (").append(rs.getDouble("NutrientValue")).append(" units)\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            suggestion.append("\n⚠ Error finding food suggestions.");
        }

        suggestionsArea.setText(suggestion.toString());
    }

}
