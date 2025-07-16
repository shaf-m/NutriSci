package org.example.ui;

import javax.swing.*;

public class NutrientChartWindow extends JFrame {

    public NutrientChartWindow() {
        setTitle("Nutrient Chart");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(new NutrientChartPanel());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NutrientChartWindow window = new NutrientChartWindow();
            window.setVisible(true);
        });
    }
}
