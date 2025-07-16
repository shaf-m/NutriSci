package org.example.dao;

import java.sql.*;
import java.util.*;

public class FoodSearchDAO {
    public static Map<String, Integer> searchFoods(String query) {
        Map<String, Integer> results = new LinkedHashMap<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT FoodID, FoodDescription FROM food_name WHERE FoodDescription LIKE ? LIMIT 20")) {
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.put(rs.getString("FoodDescription"), rs.getInt("FoodID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
