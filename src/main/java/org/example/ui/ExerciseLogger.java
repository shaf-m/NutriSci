package org.example.ui;

import org.example.dao.CentralDAO;
import org.example.dao.ExerciseLogDAO;
import org.example.model.*;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExerciseLogger extends JFrame {
    private final int profileId;
    CentralDAO centralDAO = CentralDAO.getInstance();
    ExerciseLogFactory logFactory;

    public ExerciseLogger(int profileId) {
        logFactory = new ExerciseLogFactory();
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

        centralDAO = CentralDAO.getInstance();

        submitBtn.addActionListener(e -> {
            try {
                ExerciseLog log = logFactory.createLog(profileId, java.sql.Date.valueOf(dateField.getText()), typeField.getText(), Integer.parseInt(durationField.getText()));
                log.setCalories(Double.parseDouble(caloriesField.getText()));

                centralDAO.getExerciseLogDAO().logExercise(log);
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
