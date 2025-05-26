package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class Dashboard extends JFrame {
    private final int profileId;

    public Dashboard(int profileId) {
        this.profileId = profileId;
        setTitle("NutriSci Dashboard");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel container = new JPanel(new GridLayout(3, 2, 20, 20));
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        container.add(makeCard("👤 Edit Profile", e -> new ProfileEditor(profileId).setVisible(true)));
        container.add(makeCard("📊 Visualize Nutrients", null));
        container.add(makeCard("📖 Log a Meal", null));
        container.add(makeCard("🔁 Suggest Swaps", null));
        container.add(makeCard("📉 Compare Intake (Before/After)", null));
        container.add(makeCard("🥗 Align with Canada Food Guide", null));

        add(container);
    }

    private JButton makeCard(String title, ActionListener action) {
        JButton card = new JButton(title);
        card.setFont(new Font("Arial", Font.BOLD, 14));
        card.setFocusPainted(false);
        card.setBackground(new Color(220, 235, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        if (action != null) card.addActionListener(action);
        return card;
    }
}
