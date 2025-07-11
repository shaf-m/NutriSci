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

    public CanadaGuideChart(int profileId) {
        this.profileId = profileId;

        setTitle("Canada Food Guide Alignment");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
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

        add(panel);

        if (dateComboBox.getItemCount() > 0) {
            dateComboBox.setSelectedIndex(0); // Auto-load the most recent date
        }
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

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Vegetables/Fruits", new Color(102, 204, 102));
        plot.setSectionPaint("Protein", new Color(188, 39, 44));
        plot.setSectionPaint("Whole Grains", new Color(255, 204, 102));

        chartPanel.setChart(chart);
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
