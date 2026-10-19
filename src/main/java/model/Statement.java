package model;

public class Statement {
    private final String text;
    private final boolean isTrue;
    private final String explanation;
    private final String category;

    public Statement(String text, boolean isTrue, String explanation, String category) {
        this.text = text;
        this.isTrue = isTrue;
        this.explanation = explanation;
        this.category = category;
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

    public String getCategory() {
        return category;
    }
}