package org.example.ui;

import org.example.dao.MealLogDAO;
import org.example.model.MealLog;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;


public class MealViewer extends JFrame {
    private JPanel cardContainer;

    public MealViewer(int profileId) {
        setTitle("🍽️ Meal Log Viewer");
        setSize(750, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Fetch meals
        List<MealLog> allMeals = MealLogDAO.getMealsByProfile(profileId);

        // Extract unique dates
        Set<String> uniqueDates = allMeals.stream()
                .map(meal -> meal.getMealDate().toString())
                .collect(Collectors.toCollection(TreeSet::new)); // ordered set

        JComboBox<String> dateSelector = new JComboBox<>(uniqueDates.toArray(new String[0]));
        dateSelector.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dateSelector.setPreferredSize(new Dimension(200, 30));
        dateSelector.addActionListener(e -> {
            String selectedDate = (String) dateSelector.getSelectedItem();
            displayMealsForDate(selectedDate, allMeals);
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        topBar.add(new JLabel("Select Date:"));
        topBar.add(dateSelector);

        cardContainer = new JPanel();
        cardContainer.setLayout(new BoxLayout(cardContainer, BoxLayout.Y_AXIS));
        cardContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(cardContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(topBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Show meals for the first date by default
        if (!uniqueDates.isEmpty()) {
            displayMealsForDate(uniqueDates.iterator().next(), allMeals);
        } else {
            JLabel emptyLabel = new JLabel("No meals logged yet.");
            emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            cardContainer.add(emptyLabel);
        }
    }

    private void displayMealsForDate(String date, List<MealLog> allMeals) {
        cardContainer.removeAll();

        List<MealLog> filtered = allMeals.stream()
                .filter(m -> m.getMealDate().toString().equals(date))
                .toList();

        for (MealLog meal : filtered) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 10, 10, 10),
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY)
            ));
            card.setBackground(new Color(250, 250, 250));

            JLabel title = new JLabel(String.format("🍽 %s | 🍎 %s (ID: %d)",
                    meal.getMealType(), meal.getFoodName(), meal.getFoodId()), SwingConstants.CENTER);
            title.setFont(new Font("SansSerif", Font.BOLD, 14));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            JTextArea details = new JTextArea(String.format("""
            Qty: %.2fg
            Calories: %.2f kcal | Protein: %.2f g | Carbs: %.2f g | Fat: %.2f g
            Sat Fat: %.2f g | Trans Fat: %.2f g | Sugars: %.2f g | Fiber: %.2f g
            Cholesterol: %.2f mg | Sodium: %.2f mg | Potassium: %.2f mg
            Calcium: %.2f mg | Iron: %.2f mg
            """,
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
            details.setFont(new Font("Monospaced", Font.PLAIN, 12));
            details.setEditable(false);
            details.setOpaque(false);
            details.setBorder(null);

            JButton viewChartBtn = new JButton("📊 View Nutrient Breakdown");
            viewChartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Nutrient chart panel (initially hidden)
            ChartPanel chartPanel = new ChartPanel(createNutrientPieChart(meal));
            chartPanel.setPreferredSize(new Dimension(400, 300));
            chartPanel.setVisible(false);

            viewChartBtn.addActionListener(e -> {
                boolean currentlyVisible = chartPanel.isVisible();
                chartPanel.setVisible(!currentlyVisible);
                viewChartBtn.setText(currentlyVisible ? "📊 View Nutrient Breakdown" : "❌ Close Chart");
            });

            card.add(title);
            card.add(Box.createVerticalStrut(5));
            card.add(details);
            card.add(viewChartBtn);
            card.add(chartPanel);
            card.add(Box.createVerticalStrut(10));
            cardContainer.add(card);
            cardContainer.add(Box.createVerticalStrut(10));
        }

        cardContainer.revalidate();
        cardContainer.repaint();
    }

    private JFreeChart createNutrientPieChart(MealLog meal) {
        double protein = meal.getProtein();
        double carbs = meal.getCarbohydrates();
        double fat = meal.getFat();

        DefaultPieDataset dataset = new DefaultPieDataset();
        if (protein + carbs + fat > 0) {
            dataset.setValue("Protein", protein);
            dataset.setValue("Carbohydrates", carbs);
            dataset.setValue("Fat", fat);
        } else {
            dataset.setValue("No Data", 1); // show empty fallback for now idk
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Macronutrient Composition (g)", dataset, true, true, false
        );

        // Set white background
        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(Color.WHITE);

        return chart;
    }


}
