package organization;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import users.Student;
import academic.Course;
import academic.Mark;

public class Transcript implements Serializable {
    private static final long serialVersionUID = 1L;

    private Student student;
    private Map<Course, Mark> courseGrades;
    private double gpa;

    public Transcript(Student s) {
        if (s == null) throw new IllegalArgumentException("Student cannot be null");
        this.student = s;
        this.courseGrades = new HashMap<>(s.viewMarks());
        this.gpa = s.getGpa();
    }

    public String generate() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n----------- TRANSCRIPT -----------\n");
        sb.append("Student: ").append(student.getFullName()).append("\n");
        sb.append("ID: ").append(student.getStudentId()).append("\n");
        sb.append("School: ").append(student.getSchool()).append("\n");
        sb.append("Year: ").append(student.getYearOfStudy()).append("\n");
        sb.append("--------------------------------\n");
        for (Map.Entry<Course, Mark> entry : courseGrades.entrySet()) {
            sb.append(String.format("  %-30s %s%n",
                entry.getKey().getName(), entry.getValue().getLetterGrade()));
        }
        sb.append("--------------------------------\n");
        sb.append(String.format("GPA: %.2f%n", gpa));
        sb.append("--------------------------------\n");
        return sb.toString();
    }

    public Student getStudent() { return student; }
    public Map<Course, Mark> getCourseGrades() { return Collections.unmodifiableMap(courseGrades); }
    public double getGpa() { return gpa; }

    @Override
    public String toString() {
        return generate();
    }
}
