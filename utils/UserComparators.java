package utils;
import java.util.Comparator;

import users.Student;
import users.Teacher;

public class UserComparators {

    public static final Comparator<Student> STUDENT_BY_GPA = new Comparator<Student>() {
        public int compare(Student a, Student b) {
            return Double.compare(b.getGpa(), a.getGpa());
        }
    };

    public static final Comparator<Student> STUDENT_BY_NAME = new Comparator<Student>() {
        public int compare(Student a, Student b) {
            return a.getFullName().compareTo(b.getFullName());
        }
    };

    public static final Comparator<Teacher> TEACHER_BY_RATING = new Comparator<Teacher>() {
        public int compare(Teacher a, Teacher b) {
            return Double.compare(b.getAverageRating(), a.getAverageRating());
        }
    };

    public static final Comparator<Teacher> TEACHER_BY_NAME = new Comparator<Teacher>() {
        public int compare(Teacher a, Teacher b) {
            return a.getFullName().compareTo(b.getFullName());
        }
    };
}
