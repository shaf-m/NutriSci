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
            textPane.setText(profile.getHTML());
        } else {
            textPane.setText("Failed to load user profile.");
        }
    }
}
