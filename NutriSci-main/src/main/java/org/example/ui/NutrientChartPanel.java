package org.example.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class NutrientChartPanel extends JPanel {

    public NutrientChartPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Mock nutrient data
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Protein", 500);
        dataset.setValue("Carbohydrates", 900);
        dataset.setValue("Fat", 300);
        dataset.setValue("Fiber", 350);
        dataset.setValue("Sugars", 200);

        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Macronutrient Breakdown", dataset, true, true, false
        );

        // Customize plot
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 180));
        plot.setLabelShadowPaint(null);
        plot.setLabelOutlinePaint(null);
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1}g ({2})", new DecimalFormat("0"), new DecimalFormat("0%")));

        // Custom colors
        plot.setSectionPaint("Protein", new Color(102, 204, 255));
        plot.setSectionPaint("Carbohydrates", new Color(255, 153, 102));
        plot.setSectionPaint("Fat", new Color(255, 102, 153));
        plot.setSectionPaint("Fiber", new Color(153, 255, 153));
        plot.setSectionPaint("Sugars", new Color(255, 255, 102));

        // Chart title font
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));

        // Add chart panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        add(chartPanel, BorderLayout.CENTER);

        // Add description
        JLabel description = new JLabel(
                "<html>" +
                        "<center>" +
                        "This chart displays the macronutrient composition of your weekly meal log." +
                        "</center>" +
                        "</html>",
                SwingConstants.CENTER);
        description.setFont(new Font("Arial", Font.ITALIC, 12));
        description.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(description, BorderLayout.SOUTH);
    }
}
