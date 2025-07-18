package org.example;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

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
        fileToTableMap.put("CONVERSION FACTOR.csv", "conversion_factor");
        fileToTableMap.put("NUTRIENT NAME.csv", "nutrient_name");
        fileToTableMap.put("NUTRIENT SOURCE.csv", "nutrient_source");
        fileToTableMap.put("NUTRIENT AMOUNT.csv", "nutrient_amount");
        fileToTableMap.put("REFUSE NAME.csv", "refuse_name");
        fileToTableMap.put("REFUSE AMOUNT.csv", "refuse_amount");
        fileToTableMap.put("YIELD NAME.csv", "yield_name");
        fileToTableMap.put("YIELD AMOUNT.csv", "yield_amount");

        List<String> orderedFiles = List.of(
                "FOOD GROUP.csv",
                "FOOD NAME.csv",
                "FOOD SOURCE.csv",
                "MEASURE NAME.csv",
                "CONVERSION FACTOR.csv",
                "NUTRIENT NAME.csv",
                "NUTRIENT SOURCE.csv",
                "NUTRIENT AMOUNT.csv",
                "REFUSE NAME.csv",
                "REFUSE AMOUNT.csv",
                "YIELD NAME.csv",
                "YIELD AMOUNT.csv"
        );

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            for (String fileName : orderedFiles) {
                Path path = Paths.get(folderPath, fileName);
                String tableName = fileToTableMap.get(fileName);
                if (tableName == null) {
                    System.err.println("⚠️ No table mapping found for: " + fileName);
                    continue;
                }
                try {
                    importCsvToTable(connection, path.toFile(), tableName);
                } catch (Exception e) {
                    System.err.println("❌ Failed to import: " + fileName);
                    e.printStackTrace();
                }
            }
        }
    }

    public static void importCsvToTable(Connection conn, File csvFile, String tableName) throws Exception {
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] header = reader.readNext(); // Read header

            if (header == null) {
                throw new RuntimeException("Empty CSV: " + csvFile.getName());
            }

            // Clean CSV headers
            String[] csvColumns = Arrays.stream(header)
                    .map(s -> s.replace("\uFEFF", "").trim())
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);

            // Fetch DB columns
            Set<String> dbColumns = getTableColumns(conn, tableName);

            // Validate headers
            List<String> missing = Arrays.stream(csvColumns)
                    .filter(c -> !dbColumns.contains(c))
                    .collect(Collectors.toList());
            if (!missing.isEmpty()) {
                System.err.println("❌ Mismatched columns in file " + csvFile.getName() + ": " + missing);
                return;
            }

            String placeholders = String.join(", ", Collections.nCopies(csvColumns.length, "?"));
            String columnList = Arrays.stream(csvColumns)
                    .map(c -> "`" + c + "`")
                    .collect(Collectors.joining(", "));

            String sql = "INSERT IGNORE INTO `" + tableName + "` (" + columnList + ") VALUES (" + placeholders + ")";
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                int inserted = 0;
                String[] row;

                while ((row = reader.readNext()) != null) {
                    // Trim trailing empty columns
                    int actualLength = row.length;
                    while (actualLength > 0 && (row[actualLength - 1] == null || row[actualLength - 1].trim().isEmpty())) {
                        actualLength--;
                    }

                    if (actualLength != csvColumns.length) {
                        System.err.println("⚠️ Skipping row due to column count mismatch: header=" + csvColumns.length + " row=" + actualLength);
                        continue;
                    }

                    for (int j = 0; j < actualLength; j++) {
                        String value = row[j].trim();
                        if (value.isEmpty()) {
                            stmt.setNull(j + 1, Types.NULL);
                        } else {
                            stmt.setString(j + 1, value);
                        }
                    }
                    stmt.addBatch();
                    inserted++;

                    if (inserted % 1000 == 0) {
                        stmt.executeBatch();
                        conn.commit();
                        stmt.clearBatch();
                        System.out.println("⏱ Inserted and committed " + inserted + " rows...");
                    }
                }

                // Final flush & commit
                stmt.executeBatch();
                conn.commit();
                stmt.clearBatch();

                System.out.println("✅ Imported: " + csvFile.getName() + " → " + tableName + " (Rows inserted: " + inserted + ")");
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static Set<String> getTableColumns(Connection conn, String tableName) throws SQLException {
        Set<String> columns = new HashSet<>();
        String sql = "SHOW COLUMNS FROM `" + tableName + "`";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                columns.add(rs.getString("Field").trim());
            }
        }
        return columns;
    }
}