package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.example.dao.UserProfileDAO;

public class LoginPage extends JFrame {
    public LoginPage() {
        setTitle("NutriSci Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Login", createLoginPanel());
        tabs.add("Sign Up", createSignupPanel());
        add(tabs);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginBtn = new JButton("Log In");
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (UserProfileDAO.authenticate(username, password)) {
                int profileId = UserProfileDAO.getProfileId(username);
                new Dashboard(profileId).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        });

        panel.add(new JLabel("Username"));
        panel.add(usernameField);
        panel.add(new JLabel("Password"));
        panel.add(passwordField);
        panel.add(loginBtn);
        return panel;
    }

    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton signupBtn = new JButton("Create Account");

        signupBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isBlank() || password.isBlank()) {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty.");
                return;
            }

            if (UserProfileDAO.createAccount(username, password)) {
                JOptionPane.showMessageDialog(this, "Account created! Please complete your profile.");
                new ProfileForm(true, username).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username taken.");
            }
        });

        panel.add(new JLabel("New Username"));
        panel.add(usernameField);
        panel.add(new JLabel("New Password"));
        panel.add(passwordField);
        panel.add(signupBtn);
        return panel;
    }
}
