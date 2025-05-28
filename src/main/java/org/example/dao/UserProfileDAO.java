package org.example.dao;

import org.example.model.UserProfile;

import java.sql.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

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
    public static boolean createAccount(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "")) {
            String hash = hashPassword(password);
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO user_profile (Name, Username, PasswordHash, Sex, DateOfBirth, Height_cm, Weight_kg, Units) " +
                            "VALUES (?, ?, ?, 'Other', CURDATE(), 0, 0, 'Metric')"
            );
            stmt.setString(1, username); // Placeholder name
            stmt.setString(2, username);
            stmt.setString(3, hash);
            stmt.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false; // username taken
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean authenticate(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "")) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT PasswordHash FROM user_profile WHERE Username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String stored = rs.getString("PasswordHash");
                return stored.equals(hashPassword(password));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String hashPassword(String password) throws Exception {
        // skipping the hashing for now... or else it'll be hard to debug if we forget passwords
        // u1 would look like n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=
        // so just uncomment the code below when were done testing cuz there's no way of decoding it cuz its SHA-256
//        MessageDigest md = MessageDigest.getInstance("SHA-256");
//        byte[] hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
//        return Base64.getEncoder().encodeToString(hashed);
        return password;
    }

    public static int getProfileId(String username) {
        int profileId = -1;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ProfileID FROM user_profile WHERE Username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                profileId = rs.getInt("ProfileID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profileId;
    }


    public static String getUsernameById(int profileId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT Username FROM user_profile WHERE ProfileID = ?")) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Username");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "User";
    }


}
