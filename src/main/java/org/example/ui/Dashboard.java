package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

class ProfileSelector extends JFrame {
    private JComboBox<String> profileBox;
    private JButton createButton, continueButton;
    private ArrayList<Integer> profileIDs = new ArrayList<>();

    public ProfileSelector() {
        setTitle("Select Profile");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        profileBox = new JComboBox<>();
        loadProfiles();

        createButton = new JButton("Create New Profile");
        continueButton = new JButton("Continue");

        panel.add(profileBox);
        panel.add(continueButton);
        panel.add(createButton);
        add(panel);

        createButton.addActionListener(e -> {
            new ProfileForm(true).setVisible(true); // ← tells ProfileForm to return to dashboard
            dispose();
        });

        continueButton.addActionListener(e -> {
            int index = profileBox.getSelectedIndex();
            if (index >= 0) {
                int profileId = profileIDs.get(index);
                new Dashboard(profileId).setVisible(true);
                dispose();
            }
        });
    }

    private void loadProfiles() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT ProfileID, Name FROM user_profile");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                profileIDs.add(rs.getInt("ProfileID"));
                profileBox.addItem(rs.getString("Name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProfileSelector().setVisible(true));
    }
}
