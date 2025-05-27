package org.example.ui;

import org.example.dao.MealLogDAO;
import org.example.model.MealLog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MealViewer extends JFrame {
    public MealViewer(int profileId) {
        setTitle("Meal Log");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(area);

        List<MealLog> meals = MealLogDAO.getMealsByProfile(profileId);
        if (meals.isEmpty()) {
            area.setText("No meals logged.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (MealLog meal : meals) {
                sb.append(String.format("""
                    📅 %s | 🍽 %s | 🍎 %s (ID: %d) | Qty: %.2fg
                    - Calories:     %.2f kcal
                    - Protein:      %.2f g
                    - Carbs:        %.2f g
                    - Fat:          %.2f g
                    - Saturated Fat:%.2f g
                    - Trans Fat:    %.2f g
                    - Sugars:       %.2f g
                    - Fiber:        %.2f g
                    - Cholesterol:  %.2f mg
                    - Sodium:       %.2f mg
                    - Potassium:    %.2f mg
                    - Calcium:      %.2f mg
                    - Iron:         %.2f mg
                    
                    """,
                        meal.getMealDate(),
                        meal.getMealType(),
                        meal.getFoodName(), meal.getFoodId(),
                        meal.getQuantity(),
                        meal.getCalories(),
                        meal.getProtein(),
                        meal.getCarbohydrates(),
                        meal.getFat(),
                        meal.getSaturatedFat(),
                        meal.getTransFat(),
                        meal.getSugars(),
                        meal.getFiber(),
                        meal.getCholesterol(),
                        meal.getSodium(),
                        meal.getPotassium(),
                        meal.getCalcium(),
                        meal.getIron()
                ));
            }
            area.setText(sb.toString());
        }

        add(scrollPane);
    }
}
