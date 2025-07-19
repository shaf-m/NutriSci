package org.example.ui;

import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

public class CanadaGuideChart extends JFrame {
    private int profileId;
    private JComboBox<String> dateComboBox;
    private ChartPanel chartPanel;
    private JLabel guideFeedbackLabel;

    public CanadaGuideChart(int profileId) {
        this.profileId = profileId;

        setTitle("Canada Food Guide Alignment");
        setSize(600, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(210, 255, 232));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dateComboBox = new JComboBox<>();
        loadMealDates();

        dateComboBox.addActionListener(e -> {
            String selectedDate = (String) dateComboBox.getSelectedItem();
            if (selectedDate != null) {
                updateChart(LocalDate.parse(selectedDate));
            }
        });

        panel.add(dateComboBox, BorderLayout.NORTH);

        chartPanel = new ChartPanel(null);
        chartPanel.setPreferredSize(new Dimension(550, 400));
        panel.add(chartPanel, BorderLayout.CENTER);

        // Initialize feedback label before using it
        guideFeedbackLabel = new JLabel("Loading Canada Food Guide comparison...", SwingConstants.CENTER);
        guideFeedbackLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(guideFeedbackLabel, BorderLayout.SOUTH);

        add(panel);

        // Trigger chart and feedback for the first available date
        if (dateComboBox.getItemCount() > 0) {
            dateComboBox.setSelectedIndex(0);
            String selectedDate = (String) dateComboBox.getSelectedItem();
            if (selectedDate != null) {
                updateChart(LocalDate.parse(selectedDate));
            }
        }
    }

    private void updateFeedbackLabel(DefaultPieDataset dataset) {
        double veg = dataset.getValue("Vegetables/Fruits").doubleValue();
        double protein = dataset.getValue("Protein").doubleValue();
        double grains = dataset.getValue("Whole Grains").doubleValue();

        double sum = veg + protein + grains;
        if (sum == 0) sum = 1; // prevent division by zero

        double vegPercent = (veg / sum) * 100;
        double proteinPercent = (protein / sum) * 100;
        double grainsPercent = (grains / sum) * 100;

        StringBuilder blurb = new StringBuilder();

        // Veg/Fruits comparison
        if (vegPercent < 45) {
            blurb.append("🥦 Try to increase vegetables and fruits to fill half your plate.<br>");
        } else if (vegPercent >= 55) {
            blurb.append("✅ Great job meeting or exceeding the veggie/fruit target!<br>");
        } else {
            blurb.append("👍 You're close to the recommended veggies/fruits amount.<br>");
        }

        // Protein comparison
        if (proteinPercent < 20) {
            blurb.append("🍗 Consider adding more protein foods to cover a quarter of your plate.<br>");
        } else if (proteinPercent > 30) {
            blurb.append("🍗 Your protein intake is a bit high compared to recommendations.<br>");
        } else {
            blurb.append("👍 Your protein intake is well balanced.<br>");
        }

        // Whole grains comparison
        if (grainsPercent < 20) {
            blurb.append("🍞 Add more whole grains to reach a quarter of your plate.<br>");
        } else if (grainsPercent > 30) {
            blurb.append("🍞 You might want to reduce whole grains slightly for better balance.<br>");
        } else {
            blurb.append("👍 Your whole grain intake looks good.<br>");
        }

        String instructions = String.format("""
        <html><div style='text-align: center;'>
        <b>Your Meal Breakdown (normalized):</b><br>
        🥦 Vegetables & Fruits: %.1f%%<br>
        🍗 Protein Foods: %.1f%%<br>
        🍞 Whole Grains: %.1f%%<br><br>
        <b>Canada Food Guide recommends:</b><br>
        - Half your plate should be vegetables and fruits.<br>
        - A quarter should be protein foods.<br>
        - A quarter should be whole grains.<br><br>
        %s
        </div></html>
        """, vegPercent, proteinPercent, grainsPercent, blurb.toString());

        guideFeedbackLabel.setText(instructions);
    }


    private void loadMealDates() {
        Set<String> dates = new LinkedHashSet<>();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT DISTINCT MealDate FROM meal_log WHERE ProfileID = ? ORDER BY MealDate DESC")) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dates.add(rs.getDate("MealDate").toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (String date : dates) {
            dateComboBox.addItem(date);
        }
    }

    private void updateChart(LocalDate date) {
        DefaultPieDataset dataset = getFoodGroupBreakdown(date);
        JFreeChart chart = ChartFactory.createPieChart(
                "Canada Food Guide - " + date,
                dataset,
                true, true, false
        );

        chart.setBackgroundPaint(new Color(210, 255, 232));

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Vegetables/Fruits", new Color(102, 204, 102));
        plot.setSectionPaint("Protein", new Color(188, 39, 44));
        plot.setSectionPaint("Whole Grains", new Color(255, 204, 102));
        plot.setBackgroundPaint(new Color(210, 255, 232));

        chartPanel.setChart(chart);

        updateFeedbackLabel(dataset);
    }

    private DefaultPieDataset getFoodGroupBreakdown(LocalDate date) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        double vegFruits = 0, protein = 0, grains = 0, total = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT fn.FoodGroupID, ml.Quantity " +
                             "FROM meal_log ml " +
                             "JOIN food_name fn ON ml.FoodID = fn.FoodID " +
                             "WHERE ml.ProfileID = ? AND ml.MealDate = ?")) {
            stmt.setInt(1, profileId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int groupId = rs.getInt("FoodGroupID");
                double quantity = rs.getDouble("Quantity");
                total += quantity;

                if (groupId == 9 || groupId == 11) {
                    vegFruits += quantity;
                } else if (groupId == 8 || groupId == 18 || groupId == 20) {
                    grains += quantity;
                } else if (groupId == 1 || groupId == 5 || groupId == 10 ||
                        groupId == 12 || groupId == 13 || groupId == 15 ||
                        groupId == 16 || groupId == 17) {
                    protein += quantity;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (total == 0) total = 1; // Prevent division by 0

        dataset.setValue("Vegetables/Fruits", vegFruits / total * 100);
        dataset.setValue("Protein", protein / total * 100);
        dataset.setValue("Whole Grains", grains / total * 100);

        return dataset;
    }
}
