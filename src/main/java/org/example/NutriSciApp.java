package org.example;

import org.example.ui.LoginPage;

import javax.swing.*;

public class NutriSciApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
