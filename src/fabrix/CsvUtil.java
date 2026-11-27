package fabrix;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvUtil {
    private CsvUtil() {}

    public static String toLine(String... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            String f = fields[i] == null ? "" : fields[i];
            if (f.contains(",") || f.contains("\"") || f.contains("\n")) {
                f = "\"" + f.replace("\"", "\"\"") + "\"";
            }
            sb.append(f);
        }
        return sb.toString();
    }

    public static String[] parseLine(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { cur.append('"'); i++; }
                    else quoted = false;
                } else cur.append(c);
            } else if (c == '"') quoted = true;
            else if (c == ',') { out.add(cur.toString()); cur.setLength(0); }
            else cur.append(c);
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }
    public static String header(Path file) throws IOException {
        if (!Files.exists(file)) return "";
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        return lines.isEmpty() ? "" : lines.get(0).replace("\uFEFF", "").trim();
    }
    public static List<String[]> read(Path file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        if (!Files.exists(file)) return rows;
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) rows.add(parseLine(lines.get(i)));
        }
        return rows;
    }

    public static void write(Path file, String header, List<String> lines) throws IOException {
        if (file.getParent() != null) Files.createDirectories(file.getParent());
        List<String> all = new ArrayList<>();
        all.add(header);
        all.addAll(lines);
        Files.write(file, all, StandardCharsets.UTF_8);
    }
}
