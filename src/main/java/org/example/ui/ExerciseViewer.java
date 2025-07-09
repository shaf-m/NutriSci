package org.example.ui;

import org.example.dao.ExerciseLogDAO;
import org.example.model.ExerciseLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ExerciseViewer extends JFrame {
    public ExerciseViewer(int profileId) {
        setTitle("Exercise History");
        setSize(650, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        List<ExerciseLog> logs;
        try {
            logs = ExerciseLogDAO.getLogsByProfile(profileId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load exercise logs.");
            e.printStackTrace();
            return;
        }

        String[] columns = {"Date", "Type", "Duration (min)", "Calories Burned"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (ExerciseLog log : logs) {
            model.addRow(new Object[]{
                    log.getLogDate().toString(),
                    log.getExerciseType(),
                    log.getDurationMinutes(),
                    String.format("%.1f", log.getCaloriesBurned())
            });
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(table);

        JLabel title = new JLabel("🏃 Exercise History", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExerciseViewer(1).setVisible(true));
    }
}
