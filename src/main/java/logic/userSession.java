package logic;

import data.StatementRepository;
import model.Statement;

public class userSession {

    private final String userId;
    private List<Statement> currentCategoryStatements;
    private int currentIndex = 0;
    private Statement currentStatement;
    private int correctAnswers = 0;
    private int totalAnswers = 0;
    private int wrongStreak = 0;
    private boolean awaitingCategoryChoice = false;

    public userSession(String userId) {
        this.userId = userId;
    }

    public void setCategory(String categoryName) {
        this.currentCategoryStatements = StatementData.getStatementsByCategory().get(categoryName);
        this.currentIndex = 0;
        this.currentStatement = null;
    }

    public Statement getNextStatement() {
        if (currentCategoryStatements == null || currentCategoryStatements.isEmpty()) {
            return null;
        }
        Statement s = currentCategoryStatements.get(currentIndex);
        currentIndex = (currentIndex + 1) % currentCategoryStatements.size();
        return s;
    }

    public boolean hasStatements() {
        return currentCategoryStatements != null && !currentCategoryStatements.isEmpty();
    }

    public boolean isGameStarted() {
        return currentCategoryStatements != null;
    }

    public Statement getCurrentStatement() {
        return currentStatement;
    }

    public void setCurrentStatement(Statement currentStatement) {
        this.currentStatement = currentStatement;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getTotalAnswers() {
        return totalAnswers;
    }

    public void incrementCorrectAnswers() {
        this.correctAnswers++;
    }

    public void incrementTotalAnswers() {
        this.totalAnswers++;
    }

    public int getWrongStreak() {
        return wrongStreak;
    }

    public void setWrongStreak(int wrongStreak) {
        this.wrongStreak = wrongStreak;
    }


    public void resetStats() {
        this.correctAnswers = 0;
        this.totalAnswers = 0;
        this.wrongStreak = 0;
    }

    public boolean isGameStarted() {
        return repository != null;
    }
}