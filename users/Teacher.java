package users;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import storage.DataStorage;
import storage.LogEntry;
import enums.TeacherPosition;
import enums.UrgencyLevel;
import enums.NewsTopic;
import enums.ManagerType;
import academic.Course;
import academic.Mark;
import academic.Attendance;
import research.Researcher;
import research.ResearchPaper;
import research.ResearchProject;
import communication.News;
import communication.Complaint;
import exceptions.NonResearcherException;

public class Teacher extends Employee implements Researcher {
    private TeacherPosition position;
    private List<Course> courses;
    private List<Double> ratings;
    private List<ResearchPaper> papers;
    private List<ResearchProject> projects;

    public Teacher() {
        this.courses = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public Teacher(String id, String name, String surname, String email, String password,
                   double salary, String department, TeacherPosition position) {
        super(id, name, surname, email, password, salary, department);
        this.position = position;
        this.courses = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public void putMark(Student s, Course c, Mark m) {
        if (!courses.contains(c)) {
            System.out.println("Cannot put mark: " + getFullName() + " does not teach " + c.getName());
            return;
        }
        s.receiveMark(c, m);
        DataStorage.getInstance().addLog(new LogEntry(this,
            "MARK: " + s.getFullName() + " in " + c.getName() + " = " + m.getLetterGrade()));
        System.out.println(getFullName() + " gave " + m.getLetterGrade() + " to " + s.getFullName() +
                           " for " + c.getName());
    }

    public void sendComplaint(Student s, UrgencyLevel u, String text) {
        Manager dean = findDean();
        Complaint c = new Complaint(this, dean, "Complaint about " + s.getFullName(), text, s, u);
        DataStorage.getInstance().addMessage(c);
        System.out.println("Complaint sent (urgency=" + u + ") about " + s.getFullName() + ": " + text);
    }

    private Manager findDean() {
        for (User u : DataStorage.getInstance().getUsers()) {
            if (u instanceof Manager && ((Manager) u).getType() == ManagerType.DEPARTMENT)
                return (Manager) u;
        }
        return null;
    }

    public List<Student> viewStudents(Course c) {
        if (!courses.contains(c)) return new ArrayList<>();
        return DataStorage.getInstance().getStudentsForCourse(c);
    }

    public void manageCourse(Course c) {
        System.out.println(getFullName() + " is managing course: " + c.getName());
    }

    public List<Course> viewCourses() {
        return courses;
    }

    public void addCourse(Course c) {
        if (!courses.contains(c)) courses.add(c);
    }

    public void markAttendance(Student s, Course c, boolean present) {
        if (!courses.contains(c)) {
            System.out.println("Cannot mark attendance: " + getFullName() + " does not teach " + c.getName());
            return;
        }
        Attendance a = new Attendance(s, c, new java.util.Date(), present);
        DataStorage.getInstance().addAttendance(a);
        DataStorage.getInstance().addLog(new LogEntry(this,
                "ATTENDANCE: " + s.getFullName() + " in " + c.getName() + " - " + (present ? "PRESENT" : "ABSENT")));
        System.out.println(getFullName() + " marked " + s.getFullName() +
                " as " + (present ? "PRESENT" : "ABSENT") + " in " + c.getName());
    }

    public void generateMarkReport(Course c) {
        if (!courses.contains(c)) {
            System.out.println("You don't teach " + c.getName());
            return;
        }
        System.out.println("\n--- Mark Report: " + c.getName() + " ---");
        double sum = 0;
        int count = 0;
        int passing = 0;
        for (Student s : DataStorage.getInstance().getStudentsForCourse(c)) {
            Mark m = s.getMarkFor(c);
            if (m != null) {
                System.out.println("  " + s.getFullName() + ": " + m);
                sum += m.getTotal();
                count++;
                if (m.isPassing()) passing++;
            }
        }
        if (count > 0) {
            System.out.printf("Average: %.2f, Passing: %d/%d%n", sum / count, passing, count);
        }
        System.out.println("----------------------------\n");
    }

    public void addRating(double rating) {
        ratings.add(rating);
    }

    public double getAverageRating() {
        if (ratings.isEmpty()) return 0.0;
        double sum = 0;
        for (double r : ratings) sum += r;
        return sum / ratings.size();
    }

    public boolean isResearcher() {
        return position == TeacherPosition.PROFESSOR || !papers.isEmpty();
    }

    @Override
    public String getRole() {
        return "Teacher";
    }

    @Override
    public void publishPaper(ResearchPaper p) {
        if (!papers.contains(p)) papers.add(p);
        if (p.getJournal() != null) p.getJournal().publishPaper(p);
        News news = new News(NewsTopic.RESEARCH,
                "New paper: " + p.getTitle(),
                getFullName() + " published \"" + p.getTitle() + "\"",
                this);
        DataStorage.getInstance().addNews(news);
        System.out.println(getFullName() + " published paper: " + p.getTitle());
    }

    @Override
    public int calculateHIndex() {
        if (papers.isEmpty()) return 0;
        List<Integer> citations = new ArrayList<>();
        for (ResearchPaper p : papers) citations.add(p.getCitations());
        citations.sort(Comparator.reverseOrder());
        int h = 0;
        for (int i = 0; i < citations.size(); i++) {
            if (citations.get(i) >= i + 1) h = i + 1;
            else break;
        }
        return h;
    }

    @Override
    public void joinProject(ResearchProject p) throws NonResearcherException {
        p.addParticipant(this);
        if (!projects.contains(p)) projects.add(p);
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> c) {
        List<ResearchPaper> sorted = new ArrayList<>(papers);
        sorted.sort(c);
        System.out.println("--- Papers of " + getFullName() + " ---");
        for (ResearchPaper p : sorted) System.out.println("  " + p);
    }

    @Override
    public List<ResearchPaper> getPapers() { return papers; }

    @Override
    public List<ResearchProject> getProjects() { return projects; }

    @Override
    public int getTotalCitations() {
        int total = 0;
        for (ResearchPaper p : papers) total += p.getCitations();
        return total;
    }

    public TeacherPosition getPosition() { return position; }
    public void setPosition(TeacherPosition position) { this.position = position; }

    @Override
    public String toString() {
        return "Teacher{name='" + getFullName() + "', position=" + position +
               ", department='" + department +
               "', rating=" + String.format("%.2f", getAverageRating()) + "}";
    }
}
