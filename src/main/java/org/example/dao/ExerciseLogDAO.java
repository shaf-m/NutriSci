package org.example.dao;

import org.example.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExerciseLogDAO {
    ExerciseLogFactory logFactory;

    ExerciseLogDAO() {
        this.logFactory = new ExerciseLogFactory();
    }

    public void logExercise(ExerciseLog log) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
        String sql = "INSERT INTO exercise_log (ProfileID, ExerciseDate, ExerciseType, DurationMinutes, CaloriesBurned) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, log.getProfileId());
        stmt.setDate(2, new java.sql.Date(log.getLogDate().getTime()));
        stmt.setString(3, log.getLogType());
        stmt.setInt(4, log.getDurationMinutes());
        stmt.setDouble(5, log.getCalories());
        stmt.executeUpdate();
        conn.close();
    }

    public List<ExerciseLog> getLogsByProfile(int profileId) throws Exception {
        List<ExerciseLog> logs = new ArrayList<>();
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
        String sql = "SELECT * FROM exercise_log WHERE ProfileID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, profileId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            ExerciseLog log = (ExerciseLog)logFactory.createLog("exercise");
            log.setLogId(rs.getInt("ExerciseID"));
            log.setProfileId(rs.getInt("ProfileID"));
            log.setLogDate(rs.getDate("ExerciseDate"));
            log.setLogType(rs.getString("ExerciseType"));
            log.setDurationMinutes(rs.getInt("DurationMinutes"));
            log.setCalories(rs.getDouble("CaloriesBurned"));
            logs.add(log);
        }
        conn.close();
        return logs;
    }
}
