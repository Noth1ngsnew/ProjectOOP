package enums;
public enum Language {
    KZ("Қазақша"),
    EN("English"),
    RU("Русский");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
