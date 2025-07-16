package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class ProfileForm extends JFrame {
    private JTextField nameField, dobField, heightField, weightField;
    private JComboBox<String> sexBox, unitBox;
    private JLabel statusLabel;
    private boolean launchedFromLogin = false;
    private String username;

    public ProfileForm() {
        this(false, null);
    }

    public ProfileForm(boolean fromLogin, String username) {
        this.launchedFromLogin = fromLogin;
        this.username = username;

        setTitle("Create Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 420);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(10, 2, 10, 10)); // updated layout
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        nameField = new JTextField(username != null ? username : "");
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
            if (launchedFromLogin) {
                new LoginPage().setVisible(true);
            } else {
                new ProfileSelector().setVisible(true);
            }
            dispose();
        });

        add(panel);
    }

    private void handleSave(ActionEvent e) {
        try {
            String name = nameField.getText().trim();
            String sex = (String) sexBox.getSelectedItem();
            String dobText = dobField.getText().trim();
            String heightText = heightField.getText().trim();
            String weightText = weightField.getText().trim();
            String units = (String) unitBox.getSelectedItem();

            if (name.isEmpty() || dobText.isEmpty() || heightText.isEmpty() || weightText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields before saving.");
                return;
            }

            Date dob = Date.valueOf(dobText);
            double height = Double.parseDouble(heightText);
            double weight = Double.parseDouble(weightText);

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE user_profile SET Name=?, Sex=?, DateOfBirth=?, Height_cm=?, Weight_kg=?, Units=? " +
                            "WHERE Username=?", Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, name);
            stmt.setString(2, sex);
            stmt.setDate(3, dob);
            stmt.setDouble(4, height);
            stmt.setDouble(5, weight);
            stmt.setString(6, units);
            stmt.setString(7, username);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                PreparedStatement fetch = conn.prepareStatement(
                        "SELECT ProfileID FROM user_profile WHERE Username = ?");
                fetch.setString(1, username);
                ResultSet rs = fetch.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("ProfileID");
                    statusLabel.setText("✅ Profile saved.");
                    conn.close();
                    new Dashboard(id).setVisible(true);
                    dispose();
                }
            } else {
                statusLabel.setText("❌ Failed to update profile.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Invalid input or database error.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProfileForm(true, "testuser").setVisible(true));
    }
}
