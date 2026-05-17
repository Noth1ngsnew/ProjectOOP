package users;

public class ResearchStaff extends Employee {
    private String researchArea;
    private String position;

    public ResearchStaff() {
    }

    public ResearchStaff(String id, String name, String surname, String email, String password,
                         double salary, String department, String researchArea, String position) {
        super(id, name, surname, email, password, salary, department);
        this.researchArea = researchArea;
        this.position = position;
    }

    @Override
    public String getRole() {
        return "ResearchStaff";
    }

    public String getResearchArea() { return researchArea; }
    public String getPosition() { return position; }

    public void setResearchArea(String researchArea) { this.researchArea = researchArea; }
    public void setPosition(String position) { this.position = position; }

    @Override
    public String toString() {
        return "ResearchStaff{name='" + getFullName() + "', area='" + researchArea +
                "', position='" + position + "'}";
    }
}