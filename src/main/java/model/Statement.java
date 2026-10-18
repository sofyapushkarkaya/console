package model;

public class Statement {
    private final String text;
    private final boolean isTrue;
    private final String explanation;

    public Statement(String text, boolean isTrue, String explanation) {
        this.text = text;
        this.isTrue = isTrue;
        this.explanation = explanation;
    }

    public String getText() {
        return text;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public String getExplanation() {
        return explanation;
    }
}