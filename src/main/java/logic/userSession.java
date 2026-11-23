package logic;

import data.StatementData;
import model.Category;
import model.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class userSession {

    private final String userId;

    private List<Integer> unaskedIds;
    private List<Integer> askedIds;

    private Statement currentStatement;
    private int correctAnswers = 0;
    private int totalAnswers = 0;
    private int wrongStreak = 0;
    private boolean awaitingCategoryChoice = false;

    // поэтапное добавление факта
    private int addFactStep = 0; // 0 - не добавляем, 1 - категория, 2 - текст, 3 - правда/ложь, 4 - пояснение
    private Category tempCategory;
    private String tempFactText;
    private boolean tempIsTrue;

    public userSession(String userId) {
        this.userId = userId;
    }

    // геттеры и сеттеры для добавления
    public int getAddFactStep() { return addFactStep; }
    public void setAddFactStep(int addFactStep) { this.addFactStep = addFactStep; }

    public Category getTempCategory() { return tempCategory; }
    public void setTempCategory(Category tempCategory) { this.tempCategory = tempCategory; }

    public String getTempFactText() { return tempFactText; }
    public void setTempFactText(String tempFactText) { this.tempFactText = tempFactText; }

    public boolean isTempIsTrue() { return tempIsTrue; }
    public void setTempIsTrue(boolean tempIsTrue) { this.tempIsTrue = tempIsTrue; }

    public void startGame(List<Integer> availableIds) {
        this.unaskedIds = new ArrayList<>(availableIds);
        this.askedIds = new ArrayList<>();
        this.currentStatement = null;
    }

    public void resetGame() {
        this.unaskedIds = null;
        this.askedIds = null;
        this.currentStatement = null;
        this.awaitingCategoryChoice = true;
        this.addFactStep = 0; // Сбрасываем добавление факта
        this.tempCategory = null;
        this.tempFactText = null;
    }

    public boolean isGameStarted() {
        return unaskedIds != null;
    }

    public boolean hasStatements() {
        return unaskedIds != null && (!unaskedIds.isEmpty() || !askedIds.isEmpty());
    }

    public Statement getNextStatement() {
        if (unaskedIds == null) return null;

        // Если вопросы закончились — начинаем новый круг
        if (unaskedIds.isEmpty()) {
            if (askedIds.isEmpty()) return null;
            unaskedIds = new ArrayList<>(askedIds);
            askedIds.clear();
            Collections.shuffle(unaskedIds); // перемешиваем, чтобы порядок был другим
        }

        int nextId = unaskedIds.remove(0);
        askedIds.add(nextId);

        currentStatement = StatementData.getStatementById(nextId);
        return currentStatement;
    }

    public Statement getCurrentStatement() {
        return currentStatement;
    }

    public void setCurrentStatement(Statement currentStatement) {
        this.currentStatement = currentStatement;
    }

    public int getCorrectAnswers() { return correctAnswers; }
    public int getTotalAnswers() { return totalAnswers; }

    public void incrementCorrectAnswers() { this.correctAnswers++; }
    public void incrementTotalAnswers() { this.totalAnswers++; }

    public int getWrongStreak() { return wrongStreak; }
    public void setWrongStreak(int wrongStreak) { this.wrongStreak = wrongStreak; }

    public boolean isAwaitingCategoryChoice() { return awaitingCategoryChoice; }
    public void setAwaitingCategoryChoice(boolean awaitingCategoryChoice) {
        this.awaitingCategoryChoice = awaitingCategoryChoice;
    }

    public void resetStats() {
        this.correctAnswers = 0;
        this.totalAnswers = 0;
        this.wrongStreak = 0;
    }
}