package org.example.ui;

import org.example.dao.UserProfileDAO;
import org.example.model.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Dashboard extends JFrame {
    private final int profileId;

    public Dashboard(int profileId) {
        this.profileId = profileId;
        setTitle("NutriSci Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        UserProfile profile = UserProfileDAO.getById(profileId);
//        System.out.println("Fetched UserProfile: " + (profile != null ? profile.getName() : "null"));

        String userName;
        if (profile != null && profile.getName() != null && !profile.getName().trim().isEmpty()) {
            userName = profile.getName();
//            System.out.println("Using Name from profile: " + userName);
        } else {
            userName = UserProfileDAO.getUsernameById(profileId);
//            System.out.println("Fallback to Username: " + userName);
        }

//      Top welcome label
        JLabel welcomeLabel = new JLabel(
                "<html><div style='text-align:center;'><br>👋<br>Welcome back,<br>" + userName + "!</div></html>",
                SwingConstants.CENTER
        );
//        System.out.println("Final Welcome Label Text: 👋 Welcome back, " + userName + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        welcomeLabel.setPreferredSize(new Dimension(700, 200));

        // Card grid container
        JPanel container = new JPanel(new GridLayout(3, 3, 20, 20));
        container.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        container.setBackground(new Color(210, 255, 232));

        // Add cards
        container.add(makeCard("<html><div>👤<br>Edit Profile and Goal</div></html>", e -> new ProfileEditor(profileId).setVisible(true)));
        container.add(makeCard("<html><div>📈<br>View My BMR</div></html>", e -> new BMRWindow(profileId).setVisible(true)));
//        container.add(makeCard("<html><div>📊<br>Nutrient Chart</div></html>", e -> new NutrientChartWindow().setVisible(true)));
//        container.add(makeCard("<html><div>📉<br>Compare Intake</div></html>", null));
        container.add(makeCard("<html><div>📖<br>Log a Meal</div></html>", e -> new MealLogger(profileId).setVisible(true)));
        container.add(makeCard("<html><div>🍽<br>View Meal Log</div></html>", e -> new MealViewer(profileId).setVisible(true)));
        container.add(makeCard("<html><div>🏃<br>Log Exercise</div></html>", e -> new ExerciseLogger(profileId).setVisible(true)));
        container.add(makeCard("<html><div>🏋🏽<br>View Exercise Log</div></html>", e -> new ExerciseViewer(profileId).setVisible(true)));
        container.add(makeCard("<html><div>🔁<br>Suggest Swaps</div></html>", e -> new SuggestSwap(profileId).setVisible(true)));
        container.add(makeCard("<html><div>🥗<br>Canada Food Guide</div></html>", e -> new CanadaGuideChart(profileId).setVisible(true)));

        // Setup main layout
        getContentPane().setBackground(new Color(210, 255, 232));
        setLayout(new BorderLayout());
        add(welcomeLabel, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
    }

    private JButton makeCard(String title, ActionListener action) {
        JButton card = new JButton("<html><center>" + title + "</center></html>");
        card.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.setFocusPainted(false);
        card.setBackground(Color.WHITE);
        card.setOpaque(true);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Rounded border + shadow
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(25, 15, 25, 15)
        ));

        // Optional hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(210, 255, 232));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });

        if (action != null) card.addActionListener(action);
        return card;
    }
}
