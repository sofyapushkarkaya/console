package logic;

import data.StatementData;
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

    private Difficulty difficulty = Difficulty.NORMAL;
    private long questionStartTime = 0;
    private boolean awaitingDifficultyChoice = false;
    private String tempCategory;

    // Поэтапное добавление факта
    private FactStep addFactStep = FactStep.DO_NOTHING;
    private String tempFactText;
    private boolean tempIsTrue;

    public userSession(String userId) {
        this.userId = userId;
    }

    // --- ГЕТТЕРЫ И СЕТТЕРЫ ---

    public FactStep getAddFactStep() { return addFactStep; }
    public void setAddFactStep(FactStep addFactStep) { this.addFactStep = addFactStep; }

    public String getTempCategory() { return tempCategory; }
    public void setTempCategory(String tempCategory) { this.tempCategory = tempCategory; }

    public String getTempFactText() { return tempFactText; }
    public void setTempFactText(String tempFactText) { this.tempFactText = tempFactText; }

    public boolean isTempIsTrue() { return tempIsTrue; }
    public void setTempIsTrue(boolean tempIsTrue) { this.tempIsTrue = tempIsTrue; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public boolean isAwaitingDifficultyChoice() { return awaitingDifficultyChoice; }
    public void setAwaitingDifficultyChoice(boolean awaitingDifficultyChoice) { this.awaitingDifficultyChoice = awaitingDifficultyChoice; }

    // --- ТАЙМЕР ---

    public void startTimer() {
        this.questionStartTime = System.currentTimeMillis();
    }

    public boolean isTimeOut() {
        if (difficulty != Difficulty.HARD) return false;
        if (questionStartTime == 0) return false;
        long elapsed = System.currentTimeMillis() - questionStartTime;
        return elapsed > 15000;
    }

    public void resetTimer() {
        this.questionStartTime = 0;
    }


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
        this.addFactStep = FactStep.DO_NOTHING;
        this.tempFactText = null;
        this.tempIsTrue = false;
        this.difficulty = Difficulty.NORMAL;
        this.awaitingDifficultyChoice = false;
        this.tempCategory = null;
        resetTimer();
    }

    public boolean isGameStarted() { return unaskedIds != null; }

    public boolean hasStatements() {
        return unaskedIds != null && (!unaskedIds.isEmpty() || !askedIds.isEmpty());
    }

    public Statement getNextStatement() {
        if (unaskedIds == null) return null;
        if (unaskedIds.isEmpty()) {
            if (askedIds.isEmpty()) return null;
            unaskedIds = new ArrayList<>(askedIds);
            askedIds.clear();
            Collections.shuffle(unaskedIds);
        }
        int nextId = unaskedIds.remove(0);
        askedIds.add(nextId);
        currentStatement = StatementData.getStatementById(nextId);
        return currentStatement;
    }

    public Statement getCurrentStatement() { return currentStatement; }
    public void setCurrentStatement(Statement currentStatement) { this.currentStatement = currentStatement; }

    public int getCorrectAnswers() { return correctAnswers; }
    public int getTotalAnswers() { return totalAnswers; }
    public void incrementCorrectAnswers() { this.correctAnswers++; }
    public void incrementTotalAnswers() { this.totalAnswers++; }

    public int getWrongStreak() { return wrongStreak; }
    public void setWrongStreak(int wrongStreak) { this.wrongStreak = wrongStreak; }

    public boolean isAwaitingCategoryChoice() { return awaitingCategoryChoice; }
    public void setAwaitingCategoryChoice(boolean awaitingCategoryChoice) { this.awaitingCategoryChoice = awaitingCategoryChoice; }

    public void resetStats() {
        this.correctAnswers = 0;
        this.totalAnswers = 0;
        this.wrongStreak = 0;
    }
}