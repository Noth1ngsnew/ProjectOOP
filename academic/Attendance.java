package academic;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import users.Student;

public class Attendance implements Serializable {
    private Student student;
    private Course course;
    private Date date;
    private boolean present;

    public Attendance() {
    }

    public Attendance(Student student, Course course, Date date, boolean present) {
        this.student = student;
        this.course = course;
        this.date = date;
        this.present = present;
    }

    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public Date getDate() { return date; }
    public boolean isPresent() { return present; }

    public void setPresent(boolean present) { this.present = present; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attendance a)) return false;
        return Objects.equals(student, a.student) &&
                Objects.equals(course, a.course) &&
                Objects.equals(date, a.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, course, date);
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "[" + sdf.format(date) + "] " + student.getFullName() +
                " in " + course.getName() + ": " + (present ? "PRESENT" : "ABSENT");
    }
}