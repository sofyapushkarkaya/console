package logic;

import data.FilteredStatementRepository;
import data.StatementRepository;
import model.Statement;

import java.util.List;

public class userSession {

    private final String userId;
    private StatementRepository repository;      // ← вместо List + currentIndex
    private Statement currentStatement;
    private int correctAnswers = 0;
    private int totalAnswers = 0;
    private int wrongStreak = 0;
    private boolean awaitingCategoryChoice = false;

    public userSession(String userId) {
        this.userId = userId;
    }

    /** Запускает игру по готовому списку фактов (уже отфильтрованному). */
    public void startGame(List<Statement> facts) {
        this.repository = new FilteredStatementRepository(facts);
        this.currentStatement = null;
    }

    public boolean isGameStarted() {
        return repository != null;
    }

    public boolean hasStatements() {
        return repository != null && repository.hasNext();
    }

    public Statement getNextStatement() {
        return repository == null ? null : repository.getNextStatement();
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