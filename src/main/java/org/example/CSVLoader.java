package org.example;

import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Arrays;
import com.opencsv.CSVReader;

public class CSVLoader {

    public void loadFoodName(Connection conn, String csvPath) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        String[] parts;
        String sql = "INSERT INTO food_name (FoodID, FoodCode, FoodGroupID, FoodSourceID, FoodDescription, FoodDescriptionF, CountryCode, FoodDateOfEntry, FoodDateOfPublication, ScientificName) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        reader.readNext(); // Skip header
        while ((parts = reader.readNext()) != null) {
            if (parts.length < 10) {
                System.err.println("❌ Skipped malformed row: " + Arrays.toString(parts));
                continue;
            }
            try {
                stmt.setInt(1, tryParseInt(parts[0]));
                stmt.setInt(2, tryParseInt(parts[1]));
                stmt.setInt(3, tryParseInt(parts[2]));
                stmt.setInt(4, tryParseInt(parts[3]));
                stmt.setString(5, parts[4].replaceAll("\"", "").trim());
                stmt.setString(6, parts[5].replaceAll("\"", "").trim());
                stmt.setInt(7, tryParseInt(parts[6]));
                stmt.setDate(8, tryParseDate(parts[7]));
                stmt.setDate(9, tryParseDate(parts[8]));
                stmt.setString(10, parts[9].replaceAll("\"", "").trim());
                stmt.addBatch();
            } catch (Exception ex) {
                System.err.println("❌ Failed to parse line: " + Arrays.toString(parts));
                ex.printStackTrace();
            }
        }
        try {
            stmt.executeBatch();
        } catch (BatchUpdateException bue) {
            System.err.println("⚠️ Some duplicates or bad rows skipped for food_name.");
        }
        stmt.close();
        reader.close();
    }

    public void loadNutrientAmount(Connection conn, String csvPath) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        String[] parts;
        String sql = "INSERT INTO nutrient_amount (FoodID, NutrientNameID, NutrientSourceID, NutrientValue, StandardError, NumberOfObservations, NutrientDateEntry) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        reader.readNext(); // Skip header
        while ((parts = reader.readNext()) != null) {
            if (parts.length < 7) {
                System.err.println("❌ Skipped malformed row: " + Arrays.toString(parts));
                continue;
            }
            try {
                stmt.setInt(1, tryParseInt(parts[0]));
                stmt.setInt(2, tryParseInt(parts[1]));
                stmt.setInt(3, tryParseInt(parts[2]));
                stmt.setBigDecimal(4, tryParseDecimal(parts[3]));
                stmt.setBigDecimal(5, tryParseDecimal(parts[4]));
                stmt.setInt(6, tryParseInt(parts[5]));
                stmt.setDate(7, tryParseDate(parts[6]));
                stmt.addBatch();
            } catch (Exception ex) {
                System.err.println("❌ Failed to parse line: " + Arrays.toString(parts));
                ex.printStackTrace();
            }
        }
        try {
            stmt.executeBatch();
        } catch (BatchUpdateException bue) {
            System.err.println("⚠️ Some duplicates or bad rows skipped for nutrient_amount.");
        }
        stmt.close();
        reader.close();
    }

    public void loadConversionFactor(Connection conn, String csvPath) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        String[] parts;
        String sql = "INSERT INTO conversion_factor (FoodID, MeasureID, ConversionFactorValue, ConvFactorDateOfEntry) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        reader.readNext(); // Skip header
        while ((parts = reader.readNext()) != null) {
            if (parts.length < 4) {
                System.err.println("❌ Skipped malformed row: " + Arrays.toString(parts));
                continue;
            }
            try {
                stmt.setInt(1, tryParseInt(parts[0]));
                stmt.setInt(2, tryParseInt(parts[1]));
                stmt.setBigDecimal(3, tryParseDecimal(parts[2]));
                stmt.setDate(4, tryParseDate(parts[3]));
                stmt.addBatch();
            } catch (Exception ex) {
                System.err.println("❌ Failed to parse line: " + Arrays.toString(parts));
                ex.printStackTrace();
            }
        }
        try {
            stmt.executeBatch();
        } catch (BatchUpdateException bue) {
            System.err.println("⚠️ Some duplicates or bad rows skipped for conversion_factor.");
        }
        stmt.close();
        reader.close();
    }

    public void loadRefuseAmount(Connection conn, String csvPath) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        String[] parts;
        String sql = "INSERT INTO refuse_amount (FoodID, RefuseID, RefuseAmount, RefuseDateOfEntry) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        reader.readNext(); // Skip header
        while ((parts = reader.readNext()) != null) {
            if (parts.length < 4) {
                System.err.println("❌ Skipped malformed row: " + Arrays.toString(parts));
                continue;
            }
            try {
                stmt.setInt(1, tryParseInt(parts[0]));
                stmt.setInt(2, tryParseInt(parts[1]));
                stmt.setBigDecimal(3, tryParseDecimal(parts[2]));
                stmt.setDate(4, tryParseDate(parts[3]));
                stmt.addBatch();
            } catch (Exception ex) {
                System.err.println("❌ Failed to parse line: " + Arrays.toString(parts));
                ex.printStackTrace();
            }
        }
        try {
            stmt.executeBatch();
        } catch (BatchUpdateException bue) {
            System.err.println("⚠️ Some duplicates or bad rows skipped for refuse_amount.");
        }
        stmt.close();
        reader.close();
    }

    public void loadYieldAmount(Connection conn, String csvPath) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        String[] parts;
        String sql = "INSERT INTO yield_amount (FoodID, YieldID, YieldAmount, YieldDateOfEntry) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        reader.readNext(); // Skip header
        while ((parts = reader.readNext()) != null) {
            if (parts.length < 4) {
                System.err.println("❌ Skipped malformed row: " + Arrays.toString(parts));
                continue;
            }
            try {
                stmt.setInt(1, tryParseInt(parts[0]));
                stmt.setInt(2, tryParseInt(parts[1]));
                stmt.setBigDecimal(3, tryParseDecimal(parts[2]));
                stmt.setDate(4, tryParseDate(parts[3]));
                stmt.addBatch();
            } catch (Exception ex) {
                System.err.println("❌ Failed to parse line: " + Arrays.toString(parts));
                ex.printStackTrace();
            }
        }
        try {
            stmt.executeBatch();
        } catch (BatchUpdateException bue) {
            System.err.println("⚠️ Some duplicates or bad rows skipped for yield_amount.");
        }
        stmt.close();
        reader.close();
    }

    public void loadFoodGroup(Connection conn, String csvPath) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        String[] parts;
        String sql = "INSERT INTO food_group (FoodGroupID, FoodGroupCode, FoodGroupName, FoodGroupNameF) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        reader.readNext(); // Skip header
        while ((parts = reader.readNext()) != null) {
            if (parts.length < 4) {
                System.err.println("❌ Skipped malformed row: " + Arrays.toString(parts));
                continue;
            }
            try {
                stmt.setInt(1, tryParseInt(parts[0]));
                stmt.setInt(2, tryParseInt(parts[1]));
                stmt.setString(3, parts[2].replaceAll("\"", "").trim());
                stmt.setString(4, parts[3].replaceAll("\"", "").trim());
                stmt.addBatch();
            } catch (Exception ex) {
                System.err.println("❌ Failed to parse line: " + Arrays.toString(parts));
                ex.printStackTrace();
            }
        }
        try {
            stmt.executeBatch();
        } catch (BatchUpdateException bue) {
            System.err.println("⚠️ Some duplicates or bad rows skipped for food_group.");
        }
        stmt.close();
        reader.close();
    }

    public void loadFoodSource(Connection conn, String csvPath) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        String[] parts;
        String sql = "INSERT INTO food_source (FoodSourceID, FoodSourceCode, FoodSourceDescription, FoodSourceDescriptionF) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        reader.readNext(); // Skip header
        while ((parts = reader.readNext()) != null) {
            if (parts.length < 4) {
                System.err.println("❌ Skipped malformed row: " + Arrays.toString(parts));
                continue;
            }
            try {
                stmt.setInt(1, tryParseInt(parts[0]));
                stmt.setInt(2, tryParseInt(parts[1]));
                stmt.setString(3, parts[2].replaceAll("\"", "").trim());
                stmt.setString(4, parts[3].replaceAll("\"", "").trim());
                stmt.addBatch();
            } catch (Exception ex) {
                System.err.println("❌ Failed to parse line: " + Arrays.toString(parts));
                ex.printStackTrace();
            }
        }
        try {
            stmt.executeBatch();
        } catch (BatchUpdateException bue) {
            System.err.println("⚠️ Some duplicates or bad rows skipped for food_source.");
        }
        stmt.close();
        reader.close();
    }

    public void loadNutrientName(Connection conn, String csvPath) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        String[] parts;
        String sql = "INSERT INTO nutrient_name (NutrientNameID, NutrientCode, NutrientSymbol, Unit, NutrientName, NutrientNameF, Tagname, NutrientDecimals) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        reader.readNext(); // Skip header
        while ((parts = reader.readNext()) != null) {
            if (parts.length < 8) {
                System.err.println("❌ Skipped malformed row: " + Arrays.toString(parts));
                continue;
            }
            try {
                stmt.setInt(1, tryParseInt(parts[0]));
                stmt.setInt(2, tryParseInt(parts[1]));
                stmt.setString(3, parts[2].replaceAll("\"", "").trim());
                stmt.setString(4, parts[3].replaceAll("\"", "").trim());
                stmt.setString(5, parts[4].replaceAll("\"", "").trim());
                stmt.setString(6, parts[5].replaceAll("\"", "").trim());
                stmt.setString(7, parts[6].replaceAll("\"", "").trim());
                stmt.setBigDecimal(8, tryParseDecimal(parts[7]));
                stmt.addBatch();
            } catch (Exception ex) {
                System.err.println("❌ Failed to parse line: " + Arrays.toString(parts));
                ex.printStackTrace();
            }
        }
        try {
            stmt.executeBatch();
        } catch (BatchUpdateException bue) {
            System.err.println("⚠️ Some duplicates or bad rows skipped for nutrient_name.");
        }
        stmt.close();
        reader.close();
    }

    public void loadNutrientSource(Connection conn, String csvPath) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        String[] parts;
        String sql = "INSERT INTO nutrient_source (NutrientSourceID, NutrientSourceCode, NutrientSourceDescription, NutrientSourceDescriptionF) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        reader.readNext(); // Skip header
        while ((parts = reader.readNext()) != null) {
            if (parts.length < 4) {
                System.err.println("❌ Skipped malformed row: " + Arrays.toString(parts));
                continue;
            }
            try {
                stmt.setInt(1, tryParseInt(parts[0]));
                stmt.setInt(2, tryParseInt(parts[1]));
                stmt.setString(3, parts[2].replaceAll("\"", "").trim());
                stmt.setString(4, parts[3].replaceAll("\"", "").trim());
                stmt.addBatch();
            } catch (Exception ex) {
                System.err.println("❌ Failed to parse line: " + Arrays.toString(parts));
                ex.printStackTrace();
            }
        }
        try {
            stmt.executeBatch();
        } catch (BatchUpdateException bue) {
            System.err.println("⚠️ Some duplicates or bad rows skipped for nutrient_source.");
        }
        stmt.close();
        reader.close();
    }

    public void loadMeasureName(Connection conn, String csvPath) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        String[] parts;
        String sql = "INSERT INTO measure_name (MeasureID, MeasureName, MeasureNameF) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        reader.readNext(); // Skip header
        while ((parts = reader.readNext()) != null) {
            if (parts.length < 3) {
                System.err.println("❌ Skipped malformed row: " + Arrays.toString(parts));
                continue;
            }
            try {
                stmt.setInt(1, tryParseInt(parts[0]));
                stmt.setString(2, parts[1].replaceAll("\"", "").trim());
                stmt.setString(3, parts[2].replaceAll("\"", "").trim());
                stmt.addBatch();
            } catch (Exception ex) {
                System.err.println("❌ Failed to parse line: " + Arrays.toString(parts));
                ex.printStackTrace();
            }
        }
        try {
            stmt.executeBatch();
        } catch (BatchUpdateException bue) {
            System.err.println("⚠️ Some duplicates or bad rows skipped for measure_name.");
        }
        stmt.close();
        reader.close();
    }

    public void loadRefuseName(Connection conn, String csvPath) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        String[] parts;
        String sql = "INSERT INTO refuse_name (RefuseID, RefuseName, RefuseNameF) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        reader.readNext(); // Skip header
        while ((parts = reader.readNext()) != null) {
            if (parts.length < 3) {
                System.err.println("❌ Skipped malformed row: " + Arrays.toString(parts));
                continue;
            }
            try {
                stmt.setInt(1, tryParseInt(parts[0]));
                stmt.setString(2, parts[1].replaceAll("\"", "").trim());
                stmt.setString(3, parts[2].replaceAll("\"", "").trim());
                stmt.addBatch();
            } catch (Exception ex) {
                System.err.println("❌ Failed to parse line: " + Arrays.toString(parts));
                ex.printStackTrace();
            }
        }
        try {
            stmt.executeBatch();
        } catch (BatchUpdateException bue) {
            System.err.println("⚠️ Some duplicates or bad rows skipped for refuse_name.");
        }
        stmt.close();
        reader.close();
    }

    public void loadYieldName(Connection conn, String csvPath) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(csvPath));
        String[] parts;
        String sql = "INSERT INTO yield_name (YieldID, YieldName, YieldNameF) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        reader.readNext(); // Skip header
        while ((parts = reader.readNext()) != null) {
            if (parts.length < 3) {
                System.err.println("❌ Skipped malformed row: " + Arrays.toString(parts));
                continue;
            }
            try {
                stmt.setInt(1, tryParseInt(parts[0]));
                stmt.setString(2, parts[1].replaceAll("\"", "").trim());
                stmt.setString(3, parts[2].replaceAll("\"", "").trim());
                stmt.addBatch();
            } catch (Exception ex) {
                System.err.println("❌ Failed to parse line: " + Arrays.toString(parts));
                ex.printStackTrace();
            }
        }
        try {
            stmt.executeBatch();
        } catch (BatchUpdateException bue) {
            System.err.println("⚠️ Some duplicates or bad rows skipped for yield_name.");
        }
        stmt.close();
        reader.close();
    }




    private int tryParseInt(String value) {
        try {
            return Integer.parseInt(value.replaceAll("\"", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private BigDecimal tryParseDecimal(String value) {
        try {
            return new BigDecimal(value.replaceAll("\"", "").trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private Date tryParseDate(String value) {
        try {
            return Date.valueOf(value.replaceAll("\"", "").trim());
        } catch (Exception e) {
            return null;
        }
    }
}
