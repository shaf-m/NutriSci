package org.example.ui;

import org.example.model.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProfileEditor extends JFrame {
    private JTextField heightField, weightField, dobField, amountField;
    private JComboBox<String> goalTypeBox, nutrientBox;
    private JLabel status;
    private int profileId;

    //ALTER TABLE user_profile
    //ADD COLUMN goal_type VARCHAR(20),
    //ADD COLUMN nutrient VARCHAR(50),
    //ADD COLUMN goal_amount VARCHAR(50);

    public ProfileEditor(int profileId) {
        this.profileId = profileId;
        setTitle("Edit Profile");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBackground(new Color(210, 255, 232));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        heightField = new JTextField();
        weightField = new JTextField();
        dobField = new JTextField();
        amountField = new JTextField();

        goalTypeBox = new JComboBox<>(new String[]{"Increase", "Decrease"});
        nutrientBox = new JComboBox<>(new String[]{"Calories", "Fiber", "Protein", "Fat"});


        JButton save = new JButton("Save Changes");
        JButton close = new JButton("Close");
        status = new JLabel("");

        panel.add(new JLabel("DOB (yyyy-mm-dd):")); panel.add(dobField);
        panel.add(new JLabel("Height (cm):")); panel.add(heightField);
        panel.add(new JLabel("Weight (kg):")); panel.add(weightField);
        panel.add(new JLabel("Goal Type:")); panel.add(goalTypeBox);
        panel.add(new JLabel("Nutrient:")); panel.add(nutrientBox);
        panel.add(new JLabel("Amount (g):")); panel.add(amountField);
        panel.add(save); panel.add(status); panel.add(close);

        loadProfile();

        close.addActionListener(e -> dispose());

        save.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE user_profile SET DateOfBirth=?, Height_cm=?, Weight_kg=?, goal_type=?, nutrient=?, goal_amount=? WHERE ProfileID=?")) {
                stmt.setDate(1, Date.valueOf(dobField.getText().trim()));
                stmt.setDouble(2, Double.parseDouble(heightField.getText().trim()));
                stmt.setDouble(3, Double.parseDouble(weightField.getText().trim()));
                stmt.setString(4, goalTypeBox.getSelectedItem().toString());
                stmt.setString(5, nutrientBox.getSelectedItem().toString());
                stmt.setString(6, amountField.getText().trim());
                stmt.setInt(7, profileId);
                stmt.executeUpdate();
                status.setText("✅ Saved.");
            } catch (Exception ex) {
                ex.printStackTrace();
                status.setText("❌ Failed.");
            }
        });

        add(panel);
    }

    private void loadProfile() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT DateOfBirth, Height_cm, Weight_kg, goal_type, nutrient, goal_amount FROM user_profile WHERE ProfileID = ?")) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dobField.setText(rs.getDate("DateOfBirth").toString());
                heightField.setText(rs.getString("Height_cm"));
                weightField.setText(rs.getString("Weight_kg"));
                goalTypeBox.setSelectedItem(rs.getString("goal_type"));
                nutrientBox.setSelectedItem(rs.getString("nutrient"));
                amountField.setText(rs.getString("goal_amount"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}