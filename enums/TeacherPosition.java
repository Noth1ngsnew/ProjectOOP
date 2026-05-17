package enums;
public enum TeacherPosition {
    TUTOR(false),
    LECTOR(false),
    SENIOR_LECTOR(false),
    PROFESSOR(true);

    private final boolean alwaysResearcher;

    TeacherPosition(boolean alwaysResearcher) {
        this.alwaysResearcher = alwaysResearcher;
    }

    public boolean isAlwaysResearcher() {
        return alwaysResearcher;
    }
}
