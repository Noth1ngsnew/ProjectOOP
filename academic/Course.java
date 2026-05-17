package academic;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import enums.CourseType;
import users.Teacher;
import users.Student;

public class Course implements Serializable {
    private String code;
    private String name;
    private int credits;
    private CourseType type;
    private String school;
    private int targetYear;
    private String targetMajor;
    private Teacher lectureInstructor;
    private Teacher practiceInstructor;
    private List<Lesson> lessons;
    private List<Student> students; // болу
    private List<Course> prerequisites;
    private int capacity;

    public Course() {
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
        this.prerequisites = new ArrayList<>();
    }

    public Course(String code, String name, int credits, CourseType type, String school, int capacity) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.type = type;
        this.school = school;
        this.capacity = capacity;
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
        this.prerequisites = new ArrayList<>();
    }

    public CourseType getTypeForStudent(Student s) {
        if (s.getSchool() != null && this.school != null && !s.getSchool().equals(this.school)) {
            return CourseType.FREE_ELECTIVE;
        }
        return this.type;
    }

    public void addStudent(Student s) {
        if (!students.contains(s)) students.add(s);
    }

    public void removeStudent(Student s) {
        students.remove(s);
    }

    public void addInstructor(Teacher t) {
        if (lectureInstructor == null) lectureInstructor = t;
        else if (practiceInstructor == null) practiceInstructor = t;
    }

    public void addLesson(Lesson l) {
        lessons.add(l);
    }

    public List<Teacher> getInstructors() {
        List<Teacher> list = new ArrayList<>();
        if (lectureInstructor != null) list.add(lectureInstructor);
        if (practiceInstructor != null) list.add(practiceInstructor);
        return list;
    }

    public boolean hasAvailableSeats() {
        return students.size() < capacity;
    }

    public void addPrerequisite(Course c) {
        prerequisites.add(c);
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCredits() { return credits; }
    public CourseType getType() { return type; }
    public String getSchool() { return school; }
    public int getTargetYear() { return targetYear; }
    public String getTargetMajor() { return targetMajor; }
    public Teacher getLectureInstructor() { return lectureInstructor; }
    public Teacher getPracticeInstructor() { return practiceInstructor; }
    public List<Lesson> getLessons() { return lessons; }
    public List<Student> getStudents() { return students; }
    public List<Course> getPrerequisites() { return prerequisites; }
    public int getCapacity() { return capacity; }

    public void setLectureInstructor(Teacher t) { this.lectureInstructor = t; }
    public void setPracticeInstructor(Teacher t) { this.practiceInstructor = t; }
    public void setTargetYear(int targetYear) { this.targetYear = targetYear; }
    public void setTargetMajor(String targetMajor) { this.targetMajor = targetMajor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course c = (Course) o;
        return Objects.equals(code, c.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Course{" + code + " - " + name + " (" + credits + " cr, " + type +
               ", school=" + school + ", " + students.size() + "/" + capacity + ")}";
    }
}
