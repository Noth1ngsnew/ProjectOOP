package users;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import storage.DataStorage;
import storage.LogEntry;
import enums.LessonType;
import enums.ManagerType;
import academic.Course;
import communication.News;
import communication.Message;
import communication.Complaint;
import communication.Request;
import organization.Report;

public class Manager extends Employee {
    private ManagerType type;
    private List<Course> registrationCourses;
    private Map<Course, Integer> courseTargetYear = new HashMap<>();
    private Map<Course, String> courseTargetMajor = new HashMap<>();

    public Manager() {
        this.registrationCourses = new ArrayList<>();
    }

    public Manager(String id, String name, String surname, String email, String password,
                   double salary, String department, ManagerType type) {
        super(id, name, surname, email, password, salary, department);
        this.type = type;
        this.registrationCourses = new ArrayList<>();
    }

    public void assignCourseToTeacher(Course c, Teacher t, LessonType type) {
        c.addInstructor(type, t);
        t.addCourse(c);
        DataStorage.getInstance().addLog(new LogEntry(this,
                "Assigned " + c.getName() + " to " + t.getFullName()));
        System.out.println(getFullName() + " assigned " + c.getName() + " to " + t.getFullName());
    }

    public void approveRegistration(Student s, Course c) {
        if (registrationCourses.contains(c)) {
            System.out.println("Approved: " + s.getFullName() + " for " + c.getName());
        } else {
            System.out.println("Course not in registration list yet.");
        }
    }

    public void addCourseForRegistration(Course c, int year, String major) {
        courseTargetYear.put(c, year);
        courseTargetMajor.put(c, major);
        if (!registrationCourses.contains(c)) registrationCourses.add(c);
        System.out.println("Course " + c.getName() + " added for year " + year + ", major " + major);
    }

    public Report createReport() {
        return new Report(this);
    }

    public void manageNews(News n) {
        DataStorage.getInstance().addNews(n);
        System.out.println("News managed: " + n.getTitle());
    }

    public List<Request> viewRequests() {
        List<Request> all = DataStorage.getInstance().getRequests();
        System.out.println("--- All Requests ---");
        for (Request r : all) System.out.println("  " + r);
        return all;
    }

    public List<Complaint> viewComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        for (Message m : DataStorage.getInstance().getMessages()) {
            if (m instanceof Complaint) complaints.add((Complaint) m);
        }
        System.out.println("--- Complaints (" + complaints.size() + ") ---");
        for (Complaint c : complaints) System.out.println("  " + c);
        return complaints;
    }

    public void considerComplaint(Complaint c) {
        if (c == null) {
            System.out.println("No complaint to consider.");
            return;
        }
        DataStorage.getInstance().addLog(new LogEntry(this,
                "REVIEWED COMPLAINT about " + (c.getAboutStudent() != null ? c.getAboutStudent().getFullName() : "?") +
                        " (urgency=" + c.getUrgency() + ")"));
        System.out.println(getFullName() + " reviewed complaint about " +
                (c.getAboutStudent() != null ? c.getAboutStudent().getFullName() : "?") +
                " (urgency: " + c.getUrgency() + ")");
    }

    public List<Student> viewStudentsSorted(Comparator<Student> c) {
        List<Student> students = new ArrayList<>();
        for (User u : DataStorage.getInstance().getUsers()) {
            if (u instanceof Student ) {
                students.add((Student) u);
            }
        }
        students.sort(c);
        return students;
    }

    public List<Teacher> viewTeachersSorted(Comparator<Teacher> c) {
        List<Teacher> teachers = new ArrayList<>();
        for (User u : DataStorage.getInstance().getUsers()) {
            if (u instanceof Teacher) teachers.add((Teacher) u);
        }
        teachers.sort(c);
        return teachers;
    }

    @Override
    public String getRole() {
        return "Manager";
    }

    public ManagerType getType() { return type; }
    public List<Course> getRegistrationCourses() { return registrationCourses; }
    public Integer getTargetYearForCourse(Course c) { return courseTargetYear.get(c); }
    public String getTargetMajorForCourse(Course c) { return courseTargetMajor.get(c); }

    @Override
    public String toString() {
        return "Manager{name='" + getFullName() + "', type=" + type + "}";
    }
}
