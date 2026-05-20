package storage;

import users.*;
import academic.Course;
import academic.Mark;
import enums.*;
import utils.UserFactory;
import organization.StudentOrganization;
import research.Journal;
import research.ResearchPaper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataInitializer {

    public static void initializeData() {
        DataStorage storage = DataStorage.getInstance();

        Admin admin = (Admin) UserFactory.createUser(
                "ADMIN", "A01", "Асхат", "Алиев",
                "admin@kbtu.kz", "admin123",
                500_000.0, "IT Administration"
        );
        storage.addUser(admin);

        Teacher teacher1 = (Teacher) UserFactory.createUser(
                "TEACHER", "T01", "Иван", "Иванов",
                "ivanov@kbtu.kz", "teacher123",
                400_000.0, "FIT", TeacherPosition.PROFESSOR
        );
        Teacher teacher2 = (Teacher) UserFactory.createUser(
                "TEACHER", "T02", "Гульнар", "Смагулова",
                "smagulova@kbtu.kz", "teacher123",
                350_000.0, "FIT", TeacherPosition.SENIOR_LECTOR
        );
        storage.addUser(teacher1);
        storage.addUser(teacher2);

        Student student1 = (Student) UserFactory.createUser(
                "STUDENT", "S01", "Дархан", "Саматов",
                "darkhan@kbtu.kz", "student123",
                "24B030111", "FIT", 2
        );
        Student student2 = (Student) UserFactory.createUser(
                "STUDENT", "S02", "Алина", "Серикова",
                "alina@kbtu.kz", "student123",
                "25B030222", "FIT", 1
        );
        GraduateStudent gradStudent = (GraduateStudent) UserFactory.createUser(
                "GRADUATESTUDENT", "G01", "Максат", "Нурланов",
                "maksat@kbtu.kz", "grad123",
                "24M0101", "FIT", 1, StudentDegree.MASTER
        );
        storage.addUser(student1);
        storage.addUser(student2);
        storage.addUser(gradStudent);

        Manager manager = (Manager) UserFactory.createUser(
                "MANAGER", "M01", "Елена", "Петрова",
                "manager@kbtu.kz", "manager123",
                300_000.0, "OR", ManagerType.OFFICE_OF_REGISTRAR
        );
        storage.addUser(manager);

        TechSupportSpecialist ts = (TechSupportSpecialist) UserFactory.createUser(
                "TECHSUPPORT", "TS01", "Олжас", "Кенесов",
                "support@kbtu.kz", "tech123",
                250_000.0, "IT"
        );
        storage.addUser(ts);


        Course oop = new Course("CS2105", "Object-Oriented Programming", 3, CourseType.MAJOR, "FIT", 30);
        Course alg = new Course("CS2102", "Algorithms and Data Structures", 3, CourseType.MAJOR, "FIT", 25);
        Course db  = new Course("CS3101", "Database Systems", 3, CourseType.MAJOR, "FIT", 20);
        storage.addCourse(oop);
        storage.addCourse(alg);
        storage.addCourse(db);

        manager.assignCourseToTeacher(oop, teacher1, LessonType.LECTURE);
        manager.assignCourseToTeacher(alg, teacher2, LessonType.LECTURE);
        manager.assignCourseToTeacher(db,  teacher1, LessonType.LECTURE);

        try {
            student1.registerForCourse(oop);
            student1.registerForCourse(alg);
            student2.registerForCourse(alg);
            student2.registerForCourse(db);
            gradStudent.registerForCourse(oop);
        } catch (Exception e) {
            System.out.println("[DataInitializer] Ошибка регистрации: " + e.getMessage());
        }


        try {
            teacher1.putMark(student1, oop, new Mark(85, 88, 90));
            teacher2.putMark(student1, alg, new Mark(70, 75, 72));
            teacher2.putMark(student2, alg, new Mark(60, 65, 58));
            teacher1.putMark(student2, db,  new Mark(90, 92, 95));
        } catch (Exception e) {
            System.out.println("[DataInitializer] Ошибка выставления оценок: " + e.getMessage());
        }

        Journal itJournal = new Journal("KBTU IT Journal");
        storage.addJournal(itJournal);

        research.Researcher researcher = (research.Researcher) teacher1;
        List<research.Researcher> authors = new ArrayList<>();
        authors.add(researcher);

        ResearchPaper paper = new ResearchPaper(
                "Advanced OOP Architectural Patterns in Java",
                authors,
                itJournal,
                15,
                new Date(),
                "doi:10.1109/KBTU.2026.01",
                12
        );
        storage.addPaper(paper);
        researcher.publishPaper(paper);

        storage.announceTopCitedResearcher();


        StudentOrganization osit = new StudentOrganization("OSIT");
        StudentOrganization robotics = new StudentOrganization("Robotics Club");
        storage.addOrganization(osit);
        storage.addOrganization(robotics);
        student1.joinOrganization(osit);
        student1.leadOrganization(robotics);

        System.out.println("\n[Система]: Данные успешно загружены в DataStorage!");
        System.out.println("[Система]: Доступные аккаунты для входа:");
        System.out.println("  Admin:     admin@kbtu.kz     / admin123");
        System.out.println("  Manager:   manager@kbtu.kz   / manager123");
        System.out.println("  Teacher 1: ivanov@kbtu.kz    / teacher123");
        System.out.println("  Teacher 2: smagulova@kbtu.kz / teacher123");
        System.out.println("  Student 1: darkhan@kbtu.kz   / student123");
        System.out.println("  Student 2: alina@kbtu.kz     / student123");
        System.out.println("  Grad:      maksat@kbtu.kz    / grad123");
        System.out.println("  Support:   support@kbtu.kz   / tech123");
    }
}