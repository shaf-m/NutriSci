package org.example.dao;

import org.example.model.UserProfile;

import java.sql.*;

public class UserProfileDAO {
    public static UserProfile getById(int id) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "")) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_profile WHERE ProfileID = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UserProfile(
                        rs.getInt("ProfileID"),
                        rs.getString("Name"),
                        rs.getString("Sex"),
                        rs.getDate("DateOfBirth"),
                        rs.getDouble("Height_cm"),
                        rs.getDouble("Weight_kg"),
                        rs.getString("Units")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
