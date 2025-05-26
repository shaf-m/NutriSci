package org.example;
import java.sql.Connection;
import java.sql.DriverManager;

public class CNFImporter {
    public static void main(String[] args) {
        try {
            // Update with your actual credentials
            String url = "jdbc:mysql://localhost:3306/nutriscidb";
            String user = "root";
            String password = ""; // or "your_password"

            Connection conn = DriverManager.getConnection(url, user, password);
            CSVLoader loader = new CSVLoader();

            System.out.println("🔁 Loading support tables...");
            loader.loadFoodGroup(conn, "data/FOOD GROUP.csv");
            loader.loadFoodSource(conn, "data/FOOD SOURCE.csv");
            loader.loadNutrientName(conn, "data/NUTRIENT NAME.csv");
            loader.loadNutrientSource(conn, "data/NUTRIENT SOURCE.csv");
            loader.loadMeasureName(conn, "data/MEASURE NAME.csv");
            loader.loadRefuseName(conn, "data/REFUSE NAME.csv");
            loader.loadYieldName(conn, "data/YIELD NAME.csv");

            System.out.println("🔁 Loading main tables...");
            loader.loadFoodName(conn, "data/FOOD NAME.csv");
            loader.loadConversionFactor(conn, "data/CONVERSION FACTOR.csv");
            loader.loadRefuseAmount(conn, "data/REFUSE AMOUNT.csv");
            loader.loadYieldAmount(conn, "data/YIELD AMOUNT.csv");
            loader.loadNutrientAmount(conn, "data/NUTRIENT AMOUNT.csv");

            System.out.println("✅ All data loaded successfully.");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
