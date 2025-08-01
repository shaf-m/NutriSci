package org.example.ui;

import org.example.dao.CentralDAO;
import org.example.dao.FoodSearchDAO;
import org.example.dao.MealLogDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MealLogger extends JFrame {
    private final int profileId;
    private CentralDAO centralDAO;

    public MealLogger(int profileId) {
        centralDAO = centralDAO.getInstance();
        this.profileId = profileId;

        setGeneralSettings();

        JPanel panel = makePanel();

        add(panel);
    }

    private void setGeneralSettings() {
        setTitle("Log a Meal");
        setSize(500, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private JPanel makePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(210, 255, 232));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField dateField = new JTextField(LocalDate.now().toString());
        JComboBox<String> mealBox = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("🔍");
        JComboBox<String> resultsBox = new JComboBox<>();
        JTextField qtyField = new JTextField();
        JLabel status = new JLabel();
        JButton saveButton = new JButton("Log Meal");
        Map<String, Integer> foodMap = new HashMap<>();


        // Search logic
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            Map<String, Integer> matches = centralDAO.getFoodSearchDAO().searchFoods(query);
            resultsBox.removeAllItems();
            foodMap.clear();
            for (String name : matches.keySet()) {
                resultsBox.addItem(name);
                foodMap.put(name, matches.get(name));
            }
        });


        addDateToPanel(panel, gbc, dateField);

        addMealTypeToPanel(panel, gbc, mealBox);

        addSearchFoodToPanel(panel, gbc, searchField, searchButton);

        addSearchResultToPanel(panel, gbc, resultsBox);

        addQuantityToPanel(panel, gbc, qtyField);

        addSaveButtonToPanel(panel, gbc, saveButton);

        addStatusToPanel(panel, gbc, status);

        saveButton.addActionListener(e -> {
            try {
                Date date = Date.valueOf(LocalDate.now().toString().trim());
                String mealType = (String) mealBox.getSelectedItem();
                String selectedFood = (String) resultsBox.getSelectedItem();
                if (!foodMap.containsKey(selectedFood)) {
                    status.setText("❌ Invalid food.");
                    return;
                }

                int foodId = foodMap.get(selectedFood);
                double qty = Double.parseDouble(qtyField.getText().trim());

                centralDAO.getMealLogDAO().insertMeal(profileId, date, mealType, foodId, qty);
                status.setText("✅ Meal logged!");
            } catch (Exception ex) {
                ex.printStackTrace();
                status.setText("❌ Error.");
            }
        });

        return panel;
    }

    private void addDateToPanel(JPanel panel, GridBagConstraints gbc, JTextField dateField) {
        panel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);
    }

    private void addMealTypeToPanel(JPanel panel, GridBagConstraints gbc, JComboBox<String> mealBox) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Meal Type:"), gbc);
        gbc.gridx = 1;
        panel.add(mealBox, gbc);
    }

    private void addSearchFoodToPanel(JPanel panel, GridBagConstraints gbc, JTextField searchField, JButton searchButton) {
        searchField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Search Food:"), gbc);
        gbc.gridx = 1;
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(210, 255, 232));
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        panel.add(searchPanel, gbc);
    }

    private void addSearchResultToPanel(JPanel panel, GridBagConstraints gbc, JComboBox<String> resultsBox) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Select Result:"), gbc);
        gbc.gridx = 1;
        panel.add(resultsBox, gbc);
    }

    private void addQuantityToPanel(JPanel panel, GridBagConstraints gbc, JTextField qtyField) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Quantity (g/ml):"), gbc);
        gbc.gridx = 1;
        panel.add(qtyField, gbc);
    }

    private void addSaveButtonToPanel(JPanel panel, GridBagConstraints gbc, JButton saveButton) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.black);
        saveButton.setFocusPainted(false);
        panel.add(saveButton, gbc);
    }

    private void addStatusToPanel(JPanel panel, GridBagConstraints gbc, JLabel status) {
        gbc.gridy++;
        panel.add(status, gbc);
    }
}
