package data;

import model.Statement;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatementData {

    private static final Map<Integer, Statement> statementsById = new HashMap<>();
    private static final Map<String, List<Integer>> idsByCategory = new HashMap<>();
    private static final String FILE_NAME = "facts.txt";

    static {
        loadFromFile();

        //проверка на пустоту
        if (statementsById.isEmpty()) {
            System.out.println("Факты не найдены. Как только админы выспятся, сразу же решат вашу проблему. А пока попробуйте просто выйти и зайти нормально, вдруг прокатит");
            // System.exit(0);
        }
    }

    public static void addNewStatement(Statement statement) {
        int newId = statementsById.keySet().stream().max(Integer::compare).orElse(0) + 1;
        statementsById.put(newId, statement);

        String category = statement.getCategory();
        idsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(newId);
        idsByCategory.computeIfAbsent("Все", k -> new ArrayList<>()).add(newId);

        saveToFile(); // Сохраняем после каждого добавления
    }

    private static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, StandardCharsets.UTF_8))) {
            for (Map.Entry<Integer, Statement> entry : statementsById.entrySet()) {
                Statement s = entry.getValue();
                String line = entry.getKey() + " | " + s.getCategory() + " | " + s.getText() + " | " + s.isTrue() + " | " + s.getExplanation();
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
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s*\\|\\s*");
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    String category = parts[1].trim();
                    String text = parts[2].trim();
                    boolean isTrue = Boolean.parseBoolean(parts[3].trim());
                    String explanation = parts[4].trim();

                    Statement s = new Statement(text, isTrue, explanation, category);
                    statementsById.put(id, s);

                    idsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(id);
                    idsByCategory.computeIfAbsent("Все", k -> new ArrayList<>()).add(id);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка чтения из файла: " + e.getMessage());
        }
    }

    public static Statement getStatementById(int id) {
        return statementsById.get(id);
    }

    public static List<Integer> getStatementIdsByCategory(String category) {
        return idsByCategory.getOrDefault(category, List.of());
    }

    public static List<String> getAllCategories() {
        return List.of("Животные", "Наука", "Техника", "Для тестов");
    }
}