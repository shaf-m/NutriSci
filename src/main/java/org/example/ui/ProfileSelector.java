package org.example.ui;

import org.example.dao.UserProfileDAO;
import org.example.model.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Dashboard extends JFrame {
    private final int profileId;

    public Dashboard(int profileId) {
        this.profileId = profileId;
        setTitle("NutriSci Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        UserProfile profile = UserProfileDAO.getById(profileId);
        String userName = profile != null ? profile.getName() : "User";

        // Top welcome label
        JLabel welcomeLabel = new JLabel("👋 Welcome back, " + userName + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        // Card grid container
        JPanel container = new JPanel(new GridLayout(3, 3, 20, 20));
        container.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        container.setBackground(new Color(243, 250, 243));

        // Add cards
        container.add(makeCard("👤 Edit Profile", e -> new ProfileEditor(profileId).setVisible(true)));
        container.add(makeCard("📈 View My BMR", e -> new BMRWindow(profileId).setVisible(true)));
        container.add(makeCard("📊 Nutrient Chart", e -> new NutrientChartWindow().setVisible(true)));
        container.add(makeCard("📖 Log a Meal", e -> new MealLogger(profileId).setVisible(true)));
        container.add(makeCard("🍽️ View Meals", e -> new MealViewer(profileId).setVisible(true)));
        container.add(makeCard("🔁 Suggest Swaps", null));
        container.add(makeCard("📉 Compare Intake", null));
        container.add(makeCard("🥗 Canada Food Guide", null));

        // Setup main layout
        getContentPane().setBackground(new Color(243, 250, 243));
        setLayout(new BorderLayout());
        add(welcomeLabel, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
    }

    private JButton makeCard(String title, ActionListener action) {
        JButton card = new JButton("<html><center>" + title + "</center></html>");
        card.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.setFocusPainted(false);
        card.setBackground(Color.WHITE);
        card.setOpaque(true);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Rounded border + shadow
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(25, 15, 25, 15)
        ));

        // Optional hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(230, 255, 230));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });

        if (action != null) card.addActionListener(action);
        return card;
    }
}
