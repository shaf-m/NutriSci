package org.example.dao;

import org.example.model.ExerciseLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExerciseLogDAO {

    public static void logExercise(ExerciseLog log) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
        String sql = "INSERT INTO exercise_log (ProfileID, ExerciseDate, ExerciseType, DurationMinutes, CaloriesBurned) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, log.getProfileId());
        stmt.setDate(2, new java.sql.Date(log.getExerciseDate().getTime()));
        stmt.setString(3, log.getExerciseType());
        stmt.setInt(4, log.getDurationMinutes());
        stmt.setDouble(5, log.getCaloriesBurned());
        stmt.executeUpdate();
        conn.close();
    }

    public static List<ExerciseLog> getLogsByProfile(int profileId) throws Exception {
        List<ExerciseLog> logs = new ArrayList<>();
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
        String sql = "SELECT * FROM exercise_log WHERE ProfileID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, profileId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            ExerciseLog log = new ExerciseLog();
            log.setExerciseId(rs.getInt("ExerciseID"));
            log.setProfileId(rs.getInt("ProfileID"));
            log.setExerciseDate(rs.getDate("ExerciseDate"));
            log.setExerciseType(rs.getString("ExerciseType"));
            log.setDurationMinutes(rs.getInt("DurationMinutes"));
            log.setCaloriesBurned(rs.getDouble("CaloriesBurned"));
            logs.add(log);
        }
        conn.close();
        return logs;
    }
}
