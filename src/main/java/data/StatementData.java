package data;

import model.Statement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatementData {
    //возможно надо написать statementsById капсом, потому что это константа, но я написала маленькими
    private static final Map<Integer, Statement> statementsById = new HashMap<>();

    static {
        statementsById.put(1, new Statement("Осьминог чувствует вкус щупальцами", true, "На каждой присоске — рецепторы вкуса.", "Животные"));
        statementsById.put(2, new Statement("Если акула перестанет двигаться, она задохнётся.", true, "Многие виды акул должны постоянно плыть, чтобы вода проходила через жабры.", "Животные"));
        statementsById.put(3, new Statement("В Австралии есть официальный «день пауков» — когда их нельзя убивать.", false, "Такого дня нет. Но есть законы о защите некоторых видов.", "Животные"));

        statementsById.put(4, new Statement("Бананы радиоактивны", true, "Из-за калия-40. Доза микроскопическая, но физически — да.", "Наука"));
        statementsById.put(5, new Statement("Первый жёсткий диск весил больше тонны", true, "IBM 350 (1956) весил ~1 тонну и хранил всего 5 МБ.", "Наука"));

        statementsById.put(6, new Statement("Первый iPhone не умел копировать и вставлять текст.", true, "Копирование появилось только в iPhone 3GS в 2009 году.", "Техника"));
    }

    public static Statement getStatementById(int id) {
        return statementsById.get(id);
    }

    public static List<Integer> getStatementIdsByCategory(String category) {
        return statementsById.entrySet().stream()
                .filter(e -> category.equals("Все") || e.getValue().getCategory().equals(category))
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }

    public static List<String> getAllCategories() {
        return List.of("Животные", "Наука", "Техника");
    }
}