package model;

public enum Category {
    ANIMALS("Животные"),
    SCIENCE("Наука"),
    TECH("Техника"),
    TEST("Для тестов");

    private final String displayName;

    Category(String displayName) { this.displayName = displayName; }

    public String displayName() { return displayName; }


    public static Category fromDisplayName(String raw) {
        if (raw == null) return null;
        String t = raw.trim();
        if (t.isEmpty()) return null;
        for (Category c : values()) {
            if (c.displayName.equalsIgnoreCase(t)) return c;
        }
        return null;
    }
}