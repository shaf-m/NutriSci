package org.example.ui;

import org.example.dao.FoodSearchDAO;
import org.example.dao.MealLogDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class MealLogger extends JFrame {
    private final int profileId;

    public MealLogger(int profileId) {
        this.profileId = profileId;
        setTitle("Log a Meal");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField dateField = new JTextField("YYYY-MM-DD");
        JComboBox<String> mealBox = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});

        JTextField searchField = new JTextField();
        JComboBox<String> resultsBox = new JComboBox<>();
        JTextField qtyField = new JTextField();

        JLabel status = new JLabel();
        JButton searchButton = new JButton("Search Food");
        JButton saveButton = new JButton("Log Meal");

        Map<String, Integer> foodMap = new HashMap<>();

        // Search logic
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            Map<String, Integer> matches = FoodSearchDAO.searchFoods(query);
            resultsBox.removeAllItems();
            foodMap.clear();
            for (String name : matches.keySet()) {
                resultsBox.addItem(name);
                foodMap.put(name, matches.get(name));
            }
        });

        // Form layout
        panel.add(new JLabel("Date:")); panel.add(dateField);
        panel.add(new JLabel("Meal Type:")); panel.add(mealBox);
        panel.add(new JLabel("Search Food:")); panel.add(searchField);
        panel.add(new JLabel("Select Result:")); panel.add(resultsBox);
        panel.add(new JLabel("Quantity (g/ml):")); panel.add(qtyField);
        panel.add(searchButton); panel.add(saveButton);
        panel.add(new JLabel()); panel.add(status);

        saveButton.addActionListener(e -> {
            try {
                Date date = Date.valueOf(dateField.getText().trim());
                String mealType = (String) mealBox.getSelectedItem();
                String selectedFood = (String) resultsBox.getSelectedItem();
                if (!foodMap.containsKey(selectedFood)) {
                    status.setText("❌ Invalid food.");
                    return;
                }

                int foodId = foodMap.get(selectedFood);
                double qty = Double.parseDouble(qtyField.getText().trim());

                MealLogDAO.insertMeal(profileId, date, mealType, foodId, qty);
                status.setText("✅ Meal logged!");
            } catch (Exception ex) {
                ex.printStackTrace();
                status.setText("❌ Error.");
            }
        });

        add(panel);
    }
}
