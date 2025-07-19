package org.example.ui;

import org.example.dao.CentralDAO;
import org.example.dao.UserProfileDAO;
import org.example.model.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class BMRWindow extends JFrame {
    public BMRWindow(int profileId) {
        setTitle("📈 BMR Estimate");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(210, 255, 232));

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setBackground(new Color(210, 255, 232));

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        add(new JScrollPane(textPane), BorderLayout.CENTER);
        add(closeButton, BorderLayout.SOUTH);

        CentralDAO centralDAO = CentralDAO.getInstance();
        UserProfile profile = UserProfileDAO.getById(profileId);

        if (profile != null) {
            String name = profile.getName();
            String sex = profile.getSex();
            double height = profile.getHeightCm();
            double weight = profile.getWeightKg();

            int age = LocalDate.now().getYear() - profile.getDateOfBirth().toLocalDate().getYear();

            double bmr;

            // I think this is the right formula based on what I found online (compared and seemed ok)
            if (sex.equalsIgnoreCase("Male")) {
                bmr = 10 * weight + 6.25 * height - 5 * age + 5;
            } else if (sex.equalsIgnoreCase("Female")) {
                bmr = 10 * weight + 6.25 * height - 5 * age - 161;
            } else {
                bmr = 10 * weight + 6.25 * height - 5 * age;
            }

            // If you guys wanna add formatting, you can just modify the html below.
            String html = String.format(
                    "<html><div style='font-family:sans-serif; text-align:center;'>"
                            + "<br><br>"
                            + "<p style='font-size:10px; color:gray; margin-bottom:10px;'>"
                            + "Basal Metabolic Rate (BMR) is the number of calories your body burns at rest to maintain vital functions like breathing and circulation."
                            + "</p>"
                            + "<h3>BMR for %s</h3>"
                            + "<div style='display:inline-block; text-align:left; margin-left:20px;'>"
                            + "<ul style='padding-left:20px;'>"
                            + "<li>Sex: %s</li>"
                            + "<li>Age: %d</li>"
                            + "<li>Height: %.1f cm</li>"
                            + "<li>Weight: %.1f kg</li>"
                            + "</ul></div><br>"
                            + "<b>Estimated BMR: %.0f calories/day</b>"
                            + "</div></html>",
                    name, sex, age, height, weight, bmr
            );

            textPane.setText(html);
        } else {
            textPane.setText("Failed to load user profile.");
        }
    }
}
