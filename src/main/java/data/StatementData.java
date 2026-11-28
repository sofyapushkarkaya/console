package data;

import model.Statement;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Collections;

public class StatementData {

    private static final Logger LOG = Logger.getLogger(StatementData.class.getName());

    private static final Map<Integer, Statement> statementsById = new HashMap<>();
    private static final Map<String, List<Integer>> idsByCategory = new HashMap<>();
    private static final String FILE_NAME = "facts.txt";
    private static final String CATEGORY_ALL = "Все";
    private static final String CATEGORY_RANDOM = "Случайный факт";

    public record LoadResult(int loaded, int skipped, int errors, boolean fileFound) {
        public boolean isOk() {
            return fileFound && errors == 0 && skipped == 0;
        }
    }

    static {
        LoadResult result = loadFromFile();

        if (!result.fileFound()) {
            LOG.info("facts.txt не найден — стартуем с пустым хранилищем");
        } else if (result.errors() > 0 || result.skipped() > 0) {
            LOG.warning("Загрузка facts.txt: успешно " + result.loaded()
                    + ", пропущено " + result.skipped()
                    + ", ошибок " + result.errors());
        } else {
            LOG.info("Загружено фактов: " + result.loaded());
        }
    }

    private static String normalizeCategory(String raw) {
        if (raw == null) return "";
        String t = raw.trim();
        if (t.isEmpty()) return t;
        return t.substring(0, 1).toUpperCase() + t.substring(1).toLowerCase();
    }

    public static void addNewStatement(Statement statement) {
        int newId = statementsById.keySet().stream().max(Integer::compare).orElse(0) + 1;

        String category = normalizeCategory(statement.getCategory());
        Statement normalizedStatement = new Statement(
                statement.getText(), statement.isTrue(),
                statement.getExplanation(), category);

        statementsById.put(newId, normalizedStatement);
        idsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(newId);

        saveToFile();
    }

    private static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(FILE_NAME, StandardCharsets.UTF_8))) {
            for (Map.Entry<Integer, Statement> entry : statementsById.entrySet()) {
                Statement s = entry.getValue();
                String line = entry.getKey() + " | " + s.getCategory() + " | "
                        + s.getText() + " | " + s.isTrue() + " | " + s.getExplanation();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            LOG.severe("Ошибка сохранения в файл: " + e.getMessage());
        }
    }

    private static LoadResult loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new LoadResult(0, 0, 0, false);
        }

        int loaded = 0;
        int skipped = 0;
        int errors = 0;

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split("\\s*\\|\\s*");
                    if (parts.length != 5) {
                        skipped++;
                        LOG.warning("Строка " + lineNumber + " пропущена (ожидалось 5 полей, найдено "
                                + parts.length + "): " + line);
                        continue;
                    }

                    int id = Integer.parseInt(parts[0].trim());
                    String category = normalizeCategory(parts[1]);
                    String text = parts[2].trim();
                    boolean isTrue = Boolean.parseBoolean(parts[3].trim());
                    String explanation = parts[4].trim();

                    Statement s = new Statement(text, isTrue, explanation, category);
                    statementsById.put(id, s);
                    idsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(id);
                    loaded++;

                } catch (Exception e) {
                    // Одна битая строка не обрывает загрузку остальных
                    errors++;
                    LOG.warning("Ошибка в строке " + lineNumber + ": " + e.getMessage()
                            + " → строка пропущена: " + line);
                }
            }
        } catch (IOException e) {
            LOG.severe("Ошибка чтения из файла: " + e.getMessage());
            return new LoadResult(loaded, skipped, errors + 1, true);
        }

        return new LoadResult(loaded, skipped, errors, true);
    }

    public static Statement getStatementById(int id) {
        return statementsById.get(id);
    }

    public static List<Integer> getStatementIdsByCategory(String category) {
        if (CATEGORY_ALL.equalsIgnoreCase(category)) {
            return new ArrayList<>(statementsById.keySet());
        }
        if (CATEGORY_RANDOM.equalsIgnoreCase(category)) {
            List<Integer> ids = new ArrayList<>(statementsById.keySet());
            Collections.shuffle(ids);
            return ids;
        }
        return idsByCategory.getOrDefault(normalizeCategory(category), List.of());
    }

    public static List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        for (String cat : idsByCategory.keySet()) {
            if (!idsByCategory.get(cat).isEmpty()) {
                categories.add(cat);
            }
        }
        categories.sort(String.CASE_INSENSITIVE_ORDER);
        categories.add(CATEGORY_RANDOM);
        categories.add(CATEGORY_ALL);
        return categories;
    }
}