package org.example.ui;

import org.example.dao.ExerciseLogDAO;
import org.example.model.ExerciseLog;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExerciseLogger extends JFrame {
    private final int profileId;

    public ExerciseLogger(int profileId) {
        this.profileId = profileId;
        setTitle("Log Exercise");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        JTextField typeField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField caloriesField = new JTextField();
        JLabel status = new JLabel();

        JButton submitBtn = new JButton("Log Exercise");
        submitBtn.addActionListener(e -> {
            try {
                ExerciseLog log = new ExerciseLog();
                log.setProfileId(profileId);
                log.setExerciseDate(java.sql.Date.valueOf(dateField.getText()));
                log.setExerciseType(typeField.getText());
                log.setDurationMinutes(Integer.parseInt(durationField.getText()));
                log.setCaloriesBurned(Double.parseDouble(caloriesField.getText()));

                ExerciseLogDAO.logExercise(log);
                status.setText("✅ Exercise logged!");
            } catch (Exception ex) {
                ex.printStackTrace();
                status.setText("❌ Error logging exercise.");
            }
        });

        panel.add(new JLabel("Date (yyyy-mm-dd):")); panel.add(dateField);
        panel.add(new JLabel("Exercise Type:")); panel.add(typeField);
        panel.add(new JLabel("Duration (min):")); panel.add(durationField);
        panel.add(new JLabel("Calories Burned:")); panel.add(caloriesField);
        panel.add(submitBtn); panel.add(status);

        add(panel);
    }
}
