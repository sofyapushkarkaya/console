package data;

import model.Statement;

import java.util.ArrayList;
import java.util.List;

public class StatementData {

    public static List<Statement> getAllStatements() {
        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement("Осьминог чувствует вкус щупальцами", true, "На каждой присоске — рецепторы вкуса."));
        statements.add(new Statement("Бананы радиоактивны", true, "Из-за калия-40. Доза микроскопическая, но физически — да."));
        statements.add(new Statement("Если акула перестанет двигаться, она задохнётся.", true, "Многие виды акул должны постоянно плыть, чтобы вода проходила через жабры."));
        statements.add(new Statement("Первый жёсткий диск весил больше тонны", true, "IBM 350 (1956) весил ~1 тонну и хранил всего 5 МБ."));
        statements.add(new Statement("Первый iPhone не умел копировать и вставлять текст.", true, "Копирование появилось только в iPhone 3GS в 2009 году."));
        statements.add(new Statement("В Австралии есть официальный «день пауков» — когда их нельзя убивать.", false, "Такого дня нет. Но есть законы о защите некоторых видов."));
        return statements;
    }
}
