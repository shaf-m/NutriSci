// Final CNFImporter.java - cleaned up with import counter

package org.example;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.SimpleDateFormat;


public class CNFImporter {

    private final Connection connection;

    public CNFImporter(Connection connection) {
        this.connection = connection;
    }

    public void importNutrientAmount(String filePath) {
        int importedCount = 0;
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            reader.readNext(); // Skip header

            String sql = "INSERT IGNORE INTO nutrient_amount " +
                    "(FoodID, NutrientNameID, NutrientSourceID, NutrientValue, StandardError, NumberOfObservations, NutrientDateEntry) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);

            while ((nextLine = reader.readNext()) != null) {
                try {
                    int foodId = Integer.parseInt(nextLine[0].trim());
                    int nutrientId = Integer.parseInt(nextLine[1].trim());
                    BigDecimal nutrientValue = parseDecimal(nextLine[2], 5);
                    int sourceId = parseIntSafe(nextLine[3]);

                    if (!nutrientSourceExists(sourceId)) {
                        continue;
                    }

                    BigDecimal stdError = parseDecimal(nextLine[4], 4);
                    int observations = parseIntSafe(nextLine[5]);
                    Date entryDate = parseDate(nextLine[6]);

                    stmt.setInt(1, foodId);
                    stmt.setInt(2, nutrientId);
                    stmt.setInt(3, sourceId);
                    stmt.setBigDecimal(4, nutrientValue);
                    stmt.setBigDecimal(5, stdError);
                    stmt.setInt(6, observations);
                    stmt.setDate(7, entryDate);

                    stmt.executeUpdate();
                    importedCount++;
                } catch (Exception e) {
                    // Skipping bad line silently
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("✅ Nutrient amount import completed. Rows inserted: " + importedCount);
    }

    private boolean nutrientSourceExists(int id) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT 1 FROM nutrient_source WHERE NutrientSourceID = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    private BigDecimal parseDecimal(String val, int scale) {
        if (val == null || val.trim().isEmpty()) return BigDecimal.ZERO;
        try {
            val = val.replace("\uFEFF", "")
                    .replace("\u00A0", "")
                    .replace(",", ".")
                    .replaceAll("[^\\d.\\-+eE]", "")
                    .trim();

            return new BigDecimal(val).setScale(scale, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid decimal: [" + val + "]");
        }
    }

    private int parseIntSafe(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private Date parseDate(String val) {
        try {
            return Date.valueOf(val.trim());
        } catch (Exception e) {
            return null;
        }
    }


}