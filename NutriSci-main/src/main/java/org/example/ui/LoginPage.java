package org.example.ui;

import javax.swing.*;
import java.awt.*;

import org.example.dao.CentralDAO;
import org.example.dao.UserProfileDAO;

public class LoginPage extends JFrame {
    UserProfileDAO userProfileDAO;

    public LoginPage() {
        setTitle("NutriSci Login");
        setSize(480, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(210, 255, 232));
        setLayout(new BorderLayout());

        // Logo placeholder, if you guys want we can add an actual logo here later maybe
        JLabel logoLabel = new JLabel("<html><div>🥦NutriSci</div></html>", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(new Color(34, 139, 87));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(logoLabel, BorderLayout.NORTH);
        CentralDAO centralDAO = CentralDAO.getInstance();
        userProfileDAO = centralDAO.getUserProfileDAO();

        // Tabbed pane for login/signup, I honestly think tabs seemed the cleanest but up to you
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.add("Login", createLoginPanel());
        tabs.add("Sign Up", createSignupPanel());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(210, 255, 232));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 40));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 40));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        CentralDAO centralDAO = CentralDAO.getInstance();

        // Login button
        JButton loginBtn = new JButton("Log In");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBackground(new Color(39, 174, 96));
        loginBtn.setForeground(new Color(3, 62, 25));
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(250, 40));

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (userProfileDAO.authenticate(username, password)) {
                int profileId = userProfileDAO.getProfileId(username);
                new Dashboard(profileId).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridy++;
        panel.add(usernameField, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridy++;
        panel.add(passwordField, gbc);
        gbc.gridy++;
        panel.add(loginBtn, gbc);

        return panel;
    }

    private JPanel createSignupPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(210, 255, 232));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 40));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 40));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Signup button
        JButton signupBtn = new JButton("Create Account");
        signupBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signupBtn.setBackground(new Color(39, 174, 96));
        signupBtn.setForeground(new Color(3, 62, 25));
        signupBtn.setFocusPainted(false);
        signupBtn.setPreferredSize(new Dimension(250, 40));

        signupBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isBlank() || password.isBlank()) {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty.");
                return;
            }

            if (userProfileDAO.createAccount(username, password)) {
                JOptionPane.showMessageDialog(this, "Account created! Please complete your profile.");
                new ProfileForm(true, username).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username taken.");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("New Username:"), gbc);
        gbc.gridy++;
        panel.add(usernameField, gbc);
        gbc.gridy++;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridy++;
        panel.add(passwordField, gbc);
        gbc.gridy++;
        panel.add(signupBtn, gbc);

        return panel;
    }
}
