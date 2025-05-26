package org.example.ui;

import org.example.model.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProfileEditor extends JFrame {
    private JTextField heightField, weightField, dobField;
    private JLabel status;
    private int profileId;

    public ProfileEditor(int profileId) {
        this.profileId = profileId;
        setTitle("Edit Profile");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        heightField = new JTextField();
        weightField = new JTextField();
        dobField = new JTextField();

        JButton save = new JButton("Save Changes");
        JButton close = new JButton("Close");
        status = new JLabel("");

        panel.add(new JLabel("DOB (yyyy-mm-dd):")); panel.add(dobField);
        panel.add(new JLabel("Height (cm):")); panel.add(heightField);
        panel.add(new JLabel("Weight (kg):")); panel.add(weightField);
        panel.add(save); panel.add(status); panel.add(close);

        loadProfile();

        close.addActionListener(e -> dispose());

        save.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE user_profile SET DateOfBirth=?, Height_cm=?, Weight_kg=? WHERE ProfileID=?")) {
                stmt.setDate(1, Date.valueOf(dobField.getText().trim()));
                stmt.setDouble(2, Double.parseDouble(heightField.getText().trim()));
                stmt.setDouble(3, Double.parseDouble(weightField.getText().trim()));
                stmt.setInt(4, profileId);
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
                     "SELECT DateOfBirth, Height_cm, Weight_kg FROM user_profile WHERE ProfileID = ?")) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dobField.setText(rs.getDate("DateOfBirth").toString());
                heightField.setText(rs.getString("Height_cm"));
                weightField.setText(rs.getString("Weight_kg"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
