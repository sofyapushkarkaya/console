package logic;

public enum Difficulty {
    EASY("Легкий"),
    NORMAL("Средний"),
    HARD("Сложный");

    private final String displayName;

    Difficulty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Difficulty fromNumber(int number) {
        return switch (number) {
            case 1 -> EASY;
            case 2 -> NORMAL;
            case 3 -> HARD;
            default -> NORMAL; // Если ввели что-то странное, ставим средний
        };
    }
}