package org.example.ui;

import org.example.dao.UserProfileDAO;
import org.example.model.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class Dashboard extends JFrame {
    private final int profileId;

    public Dashboard(int profileId) {
        this.profileId = profileId;
        setTitle("NutriSci Dashboard");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Load user profile
        UserProfile profile = UserProfileDAO.getById(profileId);
        String userName = profile != null ? profile.getName() : "User";

        // Top label
        JLabel welcomeLabel = new JLabel("Welcome back, " + userName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        // Card grid
        JPanel container = new JPanel(new GridLayout(3, 2, 20, 20));
        container.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        container.setBackground(new Color(193, 240, 193));
        container.setOpaque(true);

        container.add(makeCard("👤 Edit Profile", e -> new ProfileEditor(profileId).setVisible(true)));
        container.add(makeCard("📈 View My BMR", e -> new BMRWindow(profileId).setVisible(true)));
        container.add(makeCard("📊 Visualize Nutrients", null));
        container.add(makeCard("📖 Log a Meal", e -> new MealLogger(profileId).setVisible(true)));
        container.add(makeCard("🍽️ View My Meals", e -> new MealViewer(profileId).setVisible(true)));
        container.add(makeCard("🔁 Suggest Swaps", null));
        container.add(makeCard("📉 Compare Intake (Before/After)", null));
        container.add(makeCard("🥗 Align with Canada Food Guide", null));

        getContentPane().setBackground(new Color(193, 240, 193)); // I set this to light green for now
        // Layout wrapper
        setLayout(new BorderLayout());
        add(welcomeLabel, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
    }

    private JButton makeCard(String title, ActionListener action) {
        JButton card = new JButton(title);
        card.setFont(new Font("Arial", Font.BOLD, 14));
        card.setFocusPainted(false);
        card.setBackground(new Color(255, 255, 255)); // I set the cards to white for now
        card.setOpaque(true);
        // Layout wrapper
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        if (action != null) card.addActionListener(action);
        return card;
    }
}
