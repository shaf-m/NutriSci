package org.example.ui;

import org.example.model.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class ProfileForm extends JFrame {
    private JTextField nameField;
    private JComboBox<String> sexBox;
    private JTextField dobField;
    private JTextField heightField;
    private JTextField weightField;
    private JComboBox<String> unitBox;
    private JLabel statusLabel;
    private boolean launchedFromSplash = false;

    public ProfileForm() {
        this(false); // default constructor
    }

    public ProfileForm(boolean fromSplash) {
        this.launchedFromSplash = fromSplash;

        setTitle("Create Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        nameField = new JTextField();
        sexBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        dobField = new JTextField("YYYY-MM-DD");
        heightField = new JTextField();
        weightField = new JTextField();
        unitBox = new JComboBox<>(new String[]{"Metric", "Imperial"});
        statusLabel = new JLabel("");

        JButton saveButton = new JButton("Save Profile");
        JButton backButton = new JButton("Back");

        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Sex:")); panel.add(sexBox);
        panel.add(new JLabel("Date of Birth:")); panel.add(dobField);
        panel.add(new JLabel("Height (cm):")); panel.add(heightField);
        panel.add(new JLabel("Weight (kg):")); panel.add(weightField);
        panel.add(new JLabel("Units:")); panel.add(unitBox);
        panel.add(saveButton); panel.add(backButton);
        panel.add(statusLabel); panel.add(new JLabel(""));

        saveButton.addActionListener(this::handleSave);
        backButton.addActionListener(e -> {
            if (launchedFromSplash) {
                new ProfileSelector().setVisible(true);
            }
            dispose();
        });

        add(panel);
    }

    private void handleSave(ActionEvent e) {
        try {
            String name = nameField.getText();
            String sex = (String) sexBox.getSelectedItem();
            Date dob = Date.valueOf(dobField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            String units = (String) unitBox.getSelectedItem();

            UserProfile profile = new UserProfile(name, sex, dob, height, weight, units);

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/nutriscidb", "root", "");
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO user_profile (Name, Sex, DateOfBirth, Height_cm, Weight_kg, Units) " +
                            "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, profile.getName());
            stmt.setString(2, profile.getSex());
            stmt.setDate(3, profile.getDateOfBirth());
            stmt.setDouble(4, profile.getHeightCm());
            stmt.setDouble(5, profile.getWeightKg());
            stmt.setString(6, profile.getUnits());

            int rows = stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int newProfileId = rs.getInt(1);
                statusLabel.setText("✅ Saved. Redirecting...");
                stmt.close(); conn.close();
                if (launchedFromSplash) {
                    new Dashboard(newProfileId).setVisible(true);
                }
                dispose();
            } else {
                statusLabel.setText("❌ Saved but no ID returned.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("❌ Failed to save");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProfileForm(true).setVisible(true));
    }
}
