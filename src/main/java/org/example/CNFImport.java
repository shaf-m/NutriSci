package org.example;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.opencsv.CSVReader;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.nio.file.*;


public class CNFImport {
    public static void main(String[] args) throws Exception {
        String folderPath = "data";
        String jdbcURL = "jdbc:mysql://localhost:3306/nutriscidb";
        String username = "root";
        String password = "";
        Map<String, String> fileToTableMap = new HashMap<>();
        fileToTableMap.put("FOOD GROUP.csv", "food_group");
        fileToTableMap.put("FOOD NAME.csv", "food_name");
        fileToTableMap.put("FOOD SOURCE.csv", "food_source");
        fileToTableMap.put("MEASURE NAME.csv", "measure_name");
        fileToTableMap.put("NUTRIENT AMOUNT.csv", "nutrient_amount");
        fileToTableMap.put("NUTRIENT NAME.csv", "nutrient_name");
        fileToTableMap.put("NUTRIENT SOURCE.csv", "nutrient_source");
        fileToTableMap.put("REFUSE AMOUNT.csv", "refuse_amount");
        fileToTableMap.put("REFUSE NAME.csv", "refuse_name");
        fileToTableMap.put("YIELD AMOUNT.csv", "yield_amount");
        fileToTableMap.put("YIELD NAME.csv", "yield_name");


        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            Files.list(Paths.get(folderPath))
                    .filter(path -> path.toString().toLowerCase().endsWith(".csv"))
                    .forEach(path -> {
                        String tableName = fileToTableMap.get(path.getFileName().toString());
                        if (tableName == null) {
                            System.err.println("⚠️ No table mapping found for: " + path.getFileName());
                            return;
                        }

                        try {
                            importCsvToTable(connection, path.toFile(), tableName);
                        } catch (Exception e) {
                            System.err.println("❌ Failed to import: " + path.getFileName());
                            e.printStackTrace();
                        }
                    });
        }
    }
    public static void importCsvToTable(Connection connection, File csvFile, String tableName) throws Exception {
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> allRows = reader.readAll();

            if (allRows.isEmpty()) {
                throw new RuntimeException("CSV is empty: " + csvFile.getName());
            }

            String[] columns = allRows.get(0);
            String placeholders = String.join(", ", Collections.nCopies(columns.length, "?"));
            String sql = "INSERT INTO " + tableName + " (" + String.join(", ", columns) + ") VALUES (" + placeholders + ")";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int insertCount = 0;

                for (int i = 1; i < allRows.size(); i++) {  // Skip header row
                    String[] data = allRows.get(i);
                    for (int j = 0; j < data.length; j++) {
                        String value = data[j].trim();
                        if (value.isEmpty()) {
                            statement.setNull(j + 1, Types.NULL);
                        } else {
                            statement.setString(j + 1, value);
                        }
                    }
                    statement.addBatch();
                    insertCount++;
                }
                statement.executeBatch();

                System.out.println("✅ Imported: " + csvFile.getName() + " → " + tableName + " (Rows inserted: " + insertCount + ")");
            }
        }
    }

}
