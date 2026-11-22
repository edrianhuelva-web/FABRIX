/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fabrix_Database;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class FileHandling {

    private static final String DATA_FOLDER = "data";
    private static final String FILE_NAME = "products.csv";

    private static final String[] HEADER = {
        "StockUnit", "ProductName", "Category",
        "Size", "Color", "Price", "Quantity"
    };

    // Initialize database
    public static void initialize() throws IOException {

        Files.createDirectories(Paths.get(DATA_FOLDER));

        Path file = Paths.get(DATA_FOLDER, FILE_NAME);

        if (!Files.exists(file)) {
            List<String[]> records = new ArrayList<>();
            records.add(HEADER);
            writeCSV(records);
        }
    }

    // Read CSV file
    public static List<String[]> readCSV() throws IOException {

        initialize();

        List<String[]> records = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(
                Paths.get(DATA_FOLDER, FILE_NAME),
                StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                records.add(parseCSVLine(line));
            }
        }

        return records;
    }

    // Write CSV file
    public static void writeCSV(List<String[]> records)
            throws IOException {

        Files.createDirectories(Paths.get(DATA_FOLDER));

        Path file = Paths.get(DATA_FOLDER, FILE_NAME);

        Path tempFile = Paths.get(DATA_FOLDER, "products.tmp");

        try (BufferedWriter writer = Files.newBufferedWriter(
                tempFile, StandardCharsets.UTF_8)) {

            for (String[] record : records) {

                StringJoiner joiner = new StringJoiner(",");

                for (String value : record) {
                    joiner.add(escapeCSV(value));
                }

                writer.write(joiner.toString());
                writer.newLine();
            }
        }

        try {
            Files.move(tempFile, file,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tempFile, file,
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // Escape CSV values
    private static String escapeCSV(String value) {

        if (value == null) {
            value = "";
        }

        if (value.contains(",") ||
            value.contains("\"") ||
            value.contains("\n") ||
            value.contains("\r")) {

            value = "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    // Parse CSV values
    private static String[] parseCSVLine(String line) {

        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {

            char c = line.charAt(i);

            if (c == '"') {

                if (insideQuotes && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {

                    current.append('"');
                    i++;

                } else {
                    insideQuotes = !insideQuotes;
                }

            } else if (c == ',' && !insideQuotes) {

                values.add(current.toString());
                current.setLength(0);

            } else {
                current.append(c);
            }
        }

        values.add(current.toString());

        return values.toArray(new String[0]);
    }
}
