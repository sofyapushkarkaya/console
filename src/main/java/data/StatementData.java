package data;

import model.Category;
import model.Statement;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StatementData {

    private static final Map<Integer, Statement> statementsById = new HashMap<>();
    private static final Map<Category, List<Integer>> idsByCategory = new HashMap<>();
    private static final String FILE_NAME = "facts.txt";
    private static final String ALL_DISPLAY = "Все";

    static {
        loadFromFile();
        if (statementsById.isEmpty()) {
            System.out.println("Факты не найдены. Как только админы выспятся, сразу же решат вашу проблему...");
        }
    }

    public static void addNewStatement(Statement statement) {
        int newId = statementsById.keySet().stream().max(Integer::compare).orElse(0) + 1;
        statementsById.put(newId, statement);
        idsByCategory.computeIfAbsent(statement.getCategory(), k -> new ArrayList<>()).add(newId);
        saveToFile();
    }

    private static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, StandardCharsets.UTF_8))) {
            for (Map.Entry<Integer, Statement> entry : statementsById.entrySet()) {
                Statement s = entry.getValue();
                String line = entry.getKey() + " | " + s.getCategory().displayName() + " | "
                        + s.getText() + " | " + s.isTrue() + " | " + s.getExplanation();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка сохранения в файл: " + e.getMessage());
        }
    }

    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                String[] parts = line.split("\\s*\\|\\s*");
                if (parts.length != 5) {
                    System.err.println("Строка " + lineNo + " пропущена: ожидалось 5 полей");
                    continue;
                }
                Category category = Category.fromDisplayName(parts[1]);
                if (category == null) {
                    System.err.println("Строка " + lineNo + " пропущена: неизвестная категория '" + parts[1] + "'");
                    continue;
                }
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String text = parts[2].trim();
                    boolean isTrue = Boolean.parseBoolean(parts[3].trim());
                    String explanation = parts[4].trim();

                    Statement s = new Statement(text, isTrue, explanation, category);
                    statementsById.put(id, s);
                    idsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(id);
                } catch (NumberFormatException e) {
                    System.err.println("Строка " + lineNo + " пропущена: некорректный id '" + parts[0] + "'");
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка чтения из файла: " + e.getMessage());
        }
    }

    public static Statement getStatementById(int id) {
        return statementsById.get(id);
    }

    public static List<Integer> getStatementIdsByCategory(String categoryDisplayName) {
        if (categoryDisplayName != null && ALL_DISPLAY.equalsIgnoreCase(categoryDisplayName.trim())) {
            return new ArrayList<>(statementsById.keySet());
        }
        Category category = Category.fromDisplayName(categoryDisplayName);
        if (category == null) return List.of();
        return idsByCategory.getOrDefault(category, List.of());
    }


    public static List<String> getAllCategories() {
        List<String> result = new ArrayList<>();
        for (Category c : Category.values()) {
            if (c == Category.TEST) continue;
            if (idsByCategory.getOrDefault(c, List.of()).isEmpty()) continue;
            result.add(c.displayName());
        }
        result.add(ALL_DISPLAY);
        return result;
    }

    public static List<Category> getAddableCategories() {
        List<Category> result = new ArrayList<>();
        for (Category c : Category.values()) {
            if (c == Category.TEST) continue;
            result.add(c);
        }
        return result;
    }
}