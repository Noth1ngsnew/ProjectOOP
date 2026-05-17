package academic;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import enums.LessonType;
import enums.CourseType;
import users.Teacher;
import users.Student;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;
    private String name;
    private int credits;
    private CourseType type;
    private String school;
    private Map<LessonType, Teacher> instructors = new HashMap<>();
    private List<Lesson> lessons;
    private List<Course> prerequisites;
    private int capacity;


    public Course() {
        this.lessons = new ArrayList<>();
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
        this.prerequisites = new ArrayList<>();
    }


    public CourseType getTypeForStudent(Student s) {
        if (s.getSchool() != null && this.school != null && !s.getSchool().equals(this.school)) {
            return CourseType.FREE_ELECTIVE;
        }
        return this.type;
    }


    public void addInstructor(LessonType type, Teacher t) {
        instructors.put(type, t);
    }

    public void addLesson(Lesson l) {
        lessons.add(l);
    }

    public List<Teacher> getInstructors() {
        return new ArrayList<>(instructors.values());
    }

    public boolean hasAvailableSeats(int enrolledCount) {
        return enrolledCount < capacity;
    }

    public void addPrerequisite(Course c) {
        if (!prerequisites.contains(c)) prerequisites.add(c);
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCredits() { return credits; }
    public CourseType getType() { return type; }
    public String getSchool() { return school; }
    public Teacher getLectureInstructor() { return instructors.get(LessonType.LECTURE); }
    public Teacher getPracticeInstructor() { return instructors.get(LessonType.PRACTICE); }
    public List<Lesson> getLessons() { return Collections.unmodifiableList(lessons); }
    public List<Course> getPrerequisites() { return Collections.unmodifiableList(prerequisites); }
    public int getCapacity() { return capacity; }

    public void setLectureInstructor(Teacher t) { instructors.put(LessonType.LECTURE, t); }
    public void setPracticeInstructor(Teacher t) { instructors.put(LessonType.PRACTICE, t); }

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
                ", school=" + school + ", capacity=" + capacity + ")}";
    }
}
