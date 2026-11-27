package logic;

public enum FactStep {
    DO_NOTHING(0),
    CATEGORY(1),
    TEXT(2),
    TRUTH(3),
    EXPLANATION(4);

    private final int code;

    FactStep(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // Вспомогательный метод, если понадобится восстановить шаг из числа
    public static FactStep fromCode(int code) {
        for (FactStep step : values()) {
            if (step.code == code) return step;
        }
        return DO_NOTHING;
    }
}