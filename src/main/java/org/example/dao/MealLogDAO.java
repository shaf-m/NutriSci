package org.example.dao;

import org.apache.commons.logging.impl.LogFactoryImpl;
import org.example.model.HealthLogFactory;
import org.example.model.LogFactory;
import org.example.model.MealLog;
import org.example.model.MealLogFactory;

import java.sql.*;
import java.util.*;

public class MealLogDAO {
    MealLogFactory logFactory;

    public MealLogDAO() {
        logFactory = new MealLogFactory();
    }

    public void insertMeal(int profileId, java.util.Date mealDate, String type, int foodId, double quantity) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO meal_log (ProfileID, MealDate, MealType, FoodID, Quantity) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setInt(1, profileId);
            stmt.setDate(2, new java.sql.Date(mealDate.getTime()));
            stmt.setString(3, type);
            stmt.setInt(4, foodId);
            stmt.setDouble(5, quantity);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MealLog> getMealsByProfile(int profileId) {
        List<MealLog> meals = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ml.*, fn.FoodDescription FROM meal_log ml " +
                             "JOIN food_name fn ON ml.FoodID = fn.FoodID " +
                             "WHERE ml.ProfileID = ? " +
                             "ORDER BY ml.MealDate DESC, ml.MealType")) {

            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int mealId = rs.getInt("MealID");
                int foodId = rs.getInt("FoodID");
                double qty = rs.getDouble("Quantity");

                MealLog meal = (MealLog)logFactory.createLog(
                        rs.getInt("ProfileID"),
                        rs.getDate("MealDate"),
                        rs.getString("MealType"),
                        foodId,
                        qty
                );
                meal.setLogId(mealId);
                meal.setFoodName(rs.getString("FoodDescription")); // Set readable food name

                loadNutrientSummary(conn, meal);
                meals.add(meal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return meals;
    }

    private void loadNutrientSummary(Connection conn, MealLog meal) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT NutrientID, NutrientValue " +
                        "FROM nutrient_amount " +
                        "WHERE FoodID = ?")) {
            stmt.setInt(1, meal.getFoodId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("NutrientID");
                double value = rs.getDouble("NutrientValue") * (meal.getQuantity() / 100);

                switch (id) {
                    case 208: meal.setCalories(value); break;        // ENERGY (kcal)
                    case 203: meal.setProtein(value); break;         // PROTEIN
                    case 204: meal.setFat(value); break;             // FAT
                    case 606: meal.setSaturatedFat(value); break;    // Saturated Fat
                    case 605: meal.setTransFat(value); break;        // Trans Fat
                    case 205: meal.setCarbohydrates(value); break;   // Carbs
                    case 291: meal.setFiber(value); break;           // Fiber
                    case 269: meal.setSugars(value); break;          // Sugars
                    case 601: meal.setCholesterol(value); break;     // Cholesterol
                    case 307: meal.setSodium(value); break;          // Sodium
                    case 306: meal.setPotassium(value); break;       // Potassium
                    case 301: meal.setCalcium(value); break;         // Calcium
                    case 303: meal.setIron(value); break;            // Iron
                    // Add more mappings as needed
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
