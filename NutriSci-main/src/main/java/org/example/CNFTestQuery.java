package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CNFTestQuery {
    public static void main(String[] args) {
        try {
            System.out.println("Import complete. 1");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nutriscidb", "root", "password");
            System.out.println("Import complete. 2");
            CNFImporter importer = new CNFImporter(conn);
            System.out.println("Import complete. 3");
            importer.importNutrientAmount("data/NUTRIENT AMOUNT.csv");
            System.out.println("Import complete. 4");
            System.out.println("Import complete. 5");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public static void main(String[] args) {
//        String url = "jdbc:mysql://localhost:3306/nutriscidb";
//        String user = "root";
//        String password = ""; // or your MySQL password
//
//        try {
//            Connection conn = DriverManager.getConnection(url, user, password);
//            Statement stmt = conn.createStatement();
//
//            // Sample food_name entries
//            ResultSet rs = stmt.executeQuery("SELECT FoodID, FoodDescription FROM food_name LIMIT 5");
//            System.out.println("✅ Sample food_name rows:");
//            while (rs.next()) {
//                int id = rs.getInt("FoodID");
//                String desc = rs.getString("FoodDescription");
//                System.out.println(" - " + id + ": " + desc);
//            }
//
//            // Nutrients with non-zero values for 'apple'
//            System.out.println("\n🔍 Non-zero nutrients for 'apple':");
//            rs = stmt.executeQuery(
//                    "SELECT fn.FoodDescription, nn.NutrientName, na.NutrientValue, nn.Unit " +
//                            "FROM nutrient_amount na " +
//                            "JOIN nutrient_name nn ON na.NutrientNameID = nn.NutrientNameID " +
//                            "JOIN food_name fn ON na.FoodID = fn.FoodID " +
//                            "WHERE fn.FoodDescription LIKE '%apple%' " +
//                            "AND na.NutrientValue > 0 " +
//                            "ORDER BY na.NutrientValue DESC");
//            while (rs.next()) {
//                System.out.printf(" - %s: %.2f %s%n", rs.getString("NutrientName"), rs.getDouble("NutrientValue"), rs.getString("Unit"));
//            }
//
//            stmt.close();
//            conn.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
