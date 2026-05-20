package users;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import storage.DataStorage;
import storage.LogEntry;
import academic.Course;
import academic.Mark;
import academic.Attendance;
import organization.StudentOrganization;
import organization.Transcript;
import exceptions.CreditLimitExceededException;
import exceptions.FailLimitExceededException;

public class Student extends User implements Comparable<Student> {
    public static final int MAX_CREDITS = 21;
    public static final int MAX_FAILS = 3;

    protected String studentId;
    protected String school;
    protected int yearOfStudy;
    protected double gpa;
    protected int failedCount;
    protected int totalCredits;
    protected List<Course> courses;
    protected Map<Course, Mark> marks;
    protected List<StudentOrganization> organizations;

    public Student() {
        this.courses = new ArrayList<>();
        this.marks = new HashMap<>();
        this.organizations = new ArrayList<>();
    }

    public Student(String id, String name, String surname, String email, String password,
                   String studentId, String school, int yearOfStudy) {
        super(id, name, surname, email, password);
        this.studentId = studentId;
        this.school = school;
        this.yearOfStudy = yearOfStudy;
        this.gpa = 0.0;
        this.failedCount = 0;
        this.totalCredits = 0;
        this.courses = new ArrayList<>();
        this.marks = new HashMap<>();
        this.organizations = new ArrayList<>();
    }

    public void registerForCourse(Course c) throws CreditLimitExceededException, FailLimitExceededException {
        if (failedCount >= MAX_FAILS) {
            throw new FailLimitExceededException(
                "Student " + getFullName() + " has failed " + failedCount + " times (max " + MAX_FAILS + ")");
        }
        if (totalCredits + c.getCredits() > MAX_CREDITS) {
            throw new CreditLimitExceededException(
                "Cannot register: total credits would be " + (totalCredits + c.getCredits()) +
                " (max " + MAX_CREDITS + ")");
        }
        if (!c.hasAvailableSeats(DataStorage.getInstance().getEnrolledCount(c))) {
            System.out.println("Course " + c.getName() + " is full.");
            return;
        }
        courses.add(c);
        DataStorage.getInstance().enrollStudent(c, this);
        totalCredits += c.getCredits();
        DataStorage.getInstance().addLog(new LogEntry(this, "REGISTERED for " + c.getName()));
        System.out.println(getFullName() + " registered for " + c.getName() +
                           " (total credits: " + totalCredits + ")");
    }

    public void dropCourse(Course c) {
        if (courses.remove(c)) {
            DataStorage.getInstance().unenrollStudent(c, this);
            totalCredits -= c.getCredits();
            System.out.println(getFullName() + " dropped " + c.getName());
        }
    }

    public Map<Course, Mark> viewMarks() {
        return marks;
    }

    public Mark getMarkFor(Course c) {
        return marks.get(c);
    }

    public void receiveMark(Course c, Mark m) {
        marks.put(c, m);
        if (!m.isPassing()) {
            failedCount++;
        }
        recalculateGpa();
    }

    public java.util.List<Attendance> viewAttendance() {
        java.util.List<Attendance> mine = new java.util.ArrayList<>();
        for (Attendance a : DataStorage.getInstance().getAttendances()) {
            if (a.getStudent().equals(this)) mine.add(a);
        }
        return mine;
    }

    public void recalculateGpa() {
        if (marks.isEmpty()) {
            gpa = 0.0;
            return;
        }
        double sum = 0;
        int count = 0;
        for (Mark m : marks.values()) {
            sum += m.getTotal();
            count++;
        }
        gpa = sum / count;
    }

    public Transcript viewTranscript() {
        return new Transcript(this);
    }

    public void rateTeacher(Teacher t, double rating) {
        t.addRating(rating);
        System.out.println(getFullName() + " rated " + t.getFullName() + " with " + rating);
    }

    public void viewCourseInfo(Course c) {
        System.out.println(c);
    }

    public void viewTeacherInfo(Teacher t) {
        System.out.println(t);
    }

    public void joinOrganization(StudentOrganization o) {
        organizations.add(o);
        o.addMember(this);
    }

    public void leadOrganization(StudentOrganization o) {
        if (!organizations.contains(o)) {
            joinOrganization(o);
        }
        o.setHead(this);
        System.out.println(getFullName() + " became head of " + o.getName());
    }

    @Override
    public int compareTo(Student other) {
        return Double.compare(other.gpa, this.gpa);
    }

    @Override
    public String getRole() {
        return "Student";
    }

    public String getStudentId() { return studentId; }
    public String getSchool() { return school; }
    public int getYearOfStudy() { return yearOfStudy; }
    public double getGpa() { return gpa; }
    public int getFailedCount() { return failedCount; }
    public int getTotalCredits() { return totalCredits; }
    public List<Course> getCourses() { return courses; }
    public List<StudentOrganization> getOrganizations() { return organizations; }

    public void setSchool(String school) { this.school = school; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    @Override
    public String toString() {
        return "Student{id='" + studentId + "', name='" + getFullName() +
               "', school='" + school + "', year=" + yearOfStudy +
               ", GPA=" + String.format("%.2f", gpa) +
               ", credits=" + totalCredits + "}";
    }
}
