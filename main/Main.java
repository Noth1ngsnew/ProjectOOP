package main;

import academic.Course;
import academic.Lesson;
import academic.Mark;
import academic.Attendance;
import communication.Complaint;
import communication.News;
import enums.*;
import exceptions.*;
import organization.Report;
import organization.StudentOrganization;
import organization.Transcript;
import research.*;
import storage.DataStorage;
import users.*;
import utils.ResearchPaperComparators;
import utils.UserComparators;
import utils.UserFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Main {

    static void header(String title) {
        System.out.println("\n=== " + title + " ===");
    }

    static Date date(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day);
        return c.getTime();
    }

    public static void main(String[] args) {

        DataStorage db = DataStorage.getInstance();

        // --------------------------
        header("USER FACTORY");

        Admin admin = (Admin) UserFactory.createUser(
                "ADMIN", "A01", "Galym", "Bektenov",
                "admin@uni.kz", "admin123", 200_000.0, "IT");

        Manager registrar = (Manager) UserFactory.createUser(
                "MANAGER", "M01", "Ainur", "Seitkali",
                "registrar@uni.kz", "reg123",
                180_000.0, "Registrar", ManagerType.OFFICE_OF_REGISTRAR);

        Manager dean = (Manager) UserFactory.createUser(
                "MANAGER", "M02", "Nurlan", "Ospanov",
                "dean@uni.kz", "dean123",
                200_000.0, "CS Department", ManagerType.DEPARTMENT);

        Teacher profAliya = (Teacher) UserFactory.createUser(
                "TEACHER", "T01", "Aliya", "Nurova",
                "aliya@uni.kz", "pass123",
                150_000.0, "CS", TeacherPosition.PROFESSOR);

        Teacher lectureMarat = (Teacher) UserFactory.createUser(
                "TEACHER", "T02", "Marat", "Seitkali",
                "marat@uni.kz", "pass456",
                120_000.0, "CS", TeacherPosition.LECTOR);

        Student studentAsel = (Student) UserFactory.createUser(
                "STUDENT", "S01", "Asel", "Karimova",
                "asel@uni.kz", "s123", "2024001", "CS", 2);

        Student studentDamir = (Student) UserFactory.createUser(
                "STUDENT", "S02", "Damir", "Umarov",
                "damir@uni.kz", "s456", "2024002", "CS", 2);

        GraduateStudent gradBerik = (GraduateStudent) UserFactory.createUser(
                "GRADUATESTUDENT", "G01", "Berik", "Askarov",
                "berik@uni.kz", "g123", "2022001", "CS", 3,
                StudentDegree.MASTER);

        TechSupportSpecialist techDenis = (TechSupportSpecialist) UserFactory.createUser(
                "TECHSUPPORT", "TS01", "Denis", "Petrov",
                "denis@uni.kz", "ts123", 90_000.0, "IT Support");

        ResearchStaff researchIvan = (ResearchStaff) UserFactory.createUser(
                "RESEARCHSTAFF", "RS01", "Ivan", "Sokolov",
                "ivan@uni.kz", "rs123",
                100_000.0, "Research Lab", "Machine Learning", "Senior Researcher");

        System.out.println("Created: " + admin);
        System.out.println("Created: " + registrar);
        System.out.println("Created: " + dean);
        System.out.println("Created: " + profAliya);
        System.out.println("Created: " + lectureMarat);
        System.out.println("Created: " + studentAsel);
        System.out.println("Created: " + studentDamir);
        System.out.println("Created: " + gradBerik);
        System.out.println("Created: " + techDenis);
        System.out.println("Created: " + researchIvan);

        // ---------------------------
        header("DATASTORAGE");

        admin.addUser(admin);
        admin.addUser(registrar);
        admin.addUser(dean);
        admin.addUser(profAliya);
        admin.addUser(lectureMarat);
        admin.addUser(studentAsel);
        admin.addUser(studentDamir);
        admin.addUser(gradBerik);
        admin.addUser(techDenis);
        admin.addUser(researchIvan);

        System.out.println("Total users in storage: " + db.getUsers().size());

        // --------------------------
        header("LOGIN / LOGOUT");

        studentAsel.login("asel@uni.kz", "s123");
        studentAsel.login("asel@uni.kz", "wrongPassword");
        profAliya.login("aliya@uni.kz", "pass123");
        studentAsel.logout();

        // --------------------------
        header("COURSES & LESSONS");

        Course oop = new Course("CS101", "Object-Oriented Programming", 5, CourseType.MAJOR, "CS", 30);
        Course ml  = new Course("CS301", "Machine Learning", 5, CourseType.MAJOR, "CS", 25);
        Course math = new Course("MATH201", "Discrete Mathematics", 4, CourseType.MINOR, "MATH", 40);

        db.addCourse(oop);
        db.addCourse(ml);
        db.addCourse(math);

        Lesson oopLecture  = new Lesson(LessonType.LECTURE,  "Room 101", DayOfWeek.MONDAY,    "09:00", 90, profAliya);
        Lesson oopPractice = new Lesson(LessonType.PRACTICE, "Lab 201",  DayOfWeek.WEDNESDAY, "11:00", 90, lectureMarat);
        Lesson mlLecture   = new Lesson(LessonType.LECTURE,  "Room 302", DayOfWeek.TUESDAY,   "13:00", 90, profAliya);

        oop.addLesson(oopLecture);
        oop.addLesson(oopPractice);
        ml.addLesson(mlLecture);

        System.out.println("Course added: " + oop);
        System.out.println("Course added: " + ml);
        System.out.println("Lessons in OOP: " + oop.getLessons().size());
        System.out.println(oopLecture);

        registrar.assignCourseToTeacher(oop, profAliya,    LessonType.LECTURE);
        registrar.assignCourseToTeacher(oop, lectureMarat, LessonType.PRACTICE);
        registrar.assignCourseToTeacher(ml,  profAliya,    LessonType.LECTURE);

        registrar.addCourseForRegistration(oop, 2, "CS");
        registrar.addCourseForRegistration(ml,  3, "CS");
        registrar.addCourseForRegistration(math, 2, "CS");

        // --------------------------
        header("STUDENT COURSE REGISTRATION");

        try {
            studentAsel.registerForCourse(oop);
            studentAsel.registerForCourse(math);
            studentDamir.registerForCourse(oop);
            studentDamir.registerForCourse(ml);
            gradBerik.registerForCourse(ml);
        } catch (CreditLimitExceededException | FailLimitExceededException e) {
            System.out.println("Registration error: " + e.getMessage());
        }

        try {
            Course extra1 = new Course("EX1", "Extra1", 8, CourseType.FREE_ELECTIVE, "CS", 10);
            Course extra2 = new Course("EX2", "Extra2", 8, CourseType.FREE_ELECTIVE, "CS", 10);
            db.addCourse(extra1);
            db.addCourse(extra2);
            studentAsel.registerForCourse(extra1);
            studentAsel.registerForCourse(extra2);
        } catch (CreditLimitExceededException e) {
            System.out.println("Caught CreditLimitExceededException: " + e.getMessage());
        } catch (FailLimitExceededException e) {
            System.out.println("Caught FailLimitExceededException: " + e.getMessage());
        }

        System.out.println(studentAsel.getFullName() + " enrolled credits: " + studentAsel.getTotalCredits());
        System.out.println(studentDamir.getFullName() + " enrolled credits: " + studentDamir.getTotalCredits());

        // --------------------------
        header("MARKS & GPA");

        Mark aselOopMark  = new Mark(25.5, 27.0, 35.2);
        Mark aselMathMark = new Mark(21.0, 22.5, 32.0);
        Mark damirOopMark = new Mark(15.0, 13.5, 22.0);
        Mark damirMlMark  = new Mark(27.6, 28.5, 36.0);

        profAliya.putMark(studentAsel,  oop,  aselOopMark);
        profAliya.putMark(studentAsel,  math, aselMathMark);
        lectureMarat.putMark(studentDamir, oop, damirOopMark);
        profAliya.putMark(studentDamir, ml, damirMlMark);

        System.out.println(studentAsel.getFullName()  + " GPA: " + String.format("%.2f", studentAsel.getGpa()));
        System.out.println(studentDamir.getFullName() + " GPA: " + String.format("%.2f", studentDamir.getGpa()));
        System.out.println("Damir OOP mark: " + damirOopMark + " | Passing: " + damirOopMark.isPassing());

        // --------------------------
        header("TRANSCRIPT");

        Transcript aselTranscript = studentAsel.viewTranscript();
        System.out.println(aselTranscript.generate());

        // --------------------------
        header("ATTENDANCE");

        profAliya.markAttendance(studentAsel,  oop, true);
        profAliya.markAttendance(studentDamir, oop, false);
        profAliya.markAttendance(studentAsel,  oop, true);

        List<Attendance> aselAttendance = studentAsel.viewAttendance();
        System.out.println(studentAsel.getFullName() + " attendance records: " + aselAttendance.size());
        for (Attendance a : aselAttendance) System.out.println("  " + a);

        // --------------------------
        header("TEACHER RATING");

        studentAsel.rateTeacher(profAliya, 4.8);
        studentDamir.rateTeacher(profAliya, 4.5);
        studentAsel.rateTeacher(lectureMarat, 3.9);

        System.out.println(profAliya.getFullName() + " average rating: " +
                String.format("%.2f", profAliya.getAverageRating()));

        profAliya.generateMarkReport(ml);
        lectureMarat.generateMarkReport(oop);

        // --------------------------
        header("JOURNAL (Observer)");

        Journal journalAI = new Journal("AI & Machine Learning Journal");
        Journal journalCS = new Journal("Journal of Computer Science");
        db.addJournal(journalAI);
        db.addJournal(journalCS);

        studentAsel.subscribeToJournal(journalAI);
        studentDamir.subscribeToJournal(journalAI);
        gradBerik.subscribeToJournal(journalAI);
        profAliya.subscribeToJournal(journalCS);

        System.out.println(journalAI.getName() + " subscribers: " + journalAI.getSubscriberCount());

        // --------------------------
        header("RESEARCH PAPERS & H-INDEX");

        ResearchPaper paper1 = new ResearchPaper(
                "Deep Learning in NLP",
                List.of(profAliya),
                journalCS, 12, date(2022, 3, 15), "10.1000/xyz001", 30);

        ResearchPaper paper2 = new ResearchPaper(
                "Transformer Architectures Survey",
                List.of(profAliya),
                journalCS, 20, date(2023, 6, 10), "10.1000/xyz002", 15);

        ResearchPaper paper3 = new ResearchPaper(
                "Attention Is All You Need: Revisited",
                List.of(profAliya),
                journalAI, 18, date(2023, 11, 1), "10.1000/xyz003", 8);

        System.out.println("\n--- Publishing papers ---");
        profAliya.publishPaper(paper1);
        profAliya.publishPaper(paper2);
        profAliya.publishPaper(paper3);

        db.addPaper(paper1);
        db.addPaper(paper2);
        db.addPaper(paper3);

        System.out.println(profAliya.getFullName() + " h-index: " + profAliya.calculateHIndex());
        System.out.println(profAliya.getFullName() + " total citations: " + profAliya.getTotalCitations());

        ResearchPaper paper4 = new ResearchPaper(
                "Introduction to Algorithm Complexity",
                List.of(lectureMarat),
                journalCS, 8, date(2021, 1, 20), "10.1000/xyz004", 5);
        lectureMarat.publishPaper(paper4);
        db.addPaper(paper4);

        System.out.println(lectureMarat.getFullName() + " h-index: " + lectureMarat.calculateHIndex());

        System.out.println("\n--- Papers sorted by citations ---");
        profAliya.printPapers(ResearchPaperComparators.BY_CITATIONS);

        System.out.println("\n--- Papers sorted by date ---");
        profAliya.printPapers(ResearchPaperComparators.BY_DATE);

        System.out.println("\n--- Papers sorted by length ---");
        profAliya.printPapers(ResearchPaperComparators.BY_LENGTH);

        System.out.println("\n--- All papers (by citations) ---");
        db.printAllPapers(ResearchPaperComparators.BY_CITATIONS);

        // --------------------------
        header("GRADUATE STUDENT & SUPERVISOR");

        try {
            gradBerik.setSupervisor(lectureMarat);
        } catch (LowHIndexException e) {
            System.out.println("Caught LowHIndexException: " + e.getMessage());
        }

        try {
            gradBerik.setSupervisor(profAliya);
        } catch (LowHIndexException e) {
            System.out.println("Unexpected: " + e.getMessage());
        }

        ResearchPaper diplomaPaper = new ResearchPaper(
                "Graph Neural Networks for Knowledge Graphs",
                List.of(gradBerik),
                journalAI, 25, date(2024, 5, 20), "10.1000/xyz005", 3);

        gradBerik.addDiplomaPaper(diplomaPaper);
        gradBerik.publishPaper(diplomaPaper);
        db.addPaper(diplomaPaper);

        System.out.println(gradBerik.getFullName() + " h-index: " + gradBerik.calculateHIndex());
        System.out.println(gradBerik);

        // --------------------------
        header("RESEARCH PROJECT");

        ResearchProject project = new ResearchProject("AI for Education", date(2024, 1, 1));
        db.addProject(project);

        try {
            profAliya.joinProject(project);
            gradBerik.joinProject(project);
            project.addParticipant(techDenis);
        } catch (NonResearcherException e) {
            System.out.println("Caught NonResearcherException: " + e.getMessage());
        }

        project.publishPaper(paper1);
        project.publishPaper(diplomaPaper);
        System.out.println(project);

        // --------------------------
        header("ADAPTER PATTERN");

        ResearcherAdapter ivanAsResearcher = new ResearcherAdapter(researchIvan);

        ResearchPaper ivanPaper = new ResearchPaper(
                "Federated Learning: A Survey",
                List.of(ivanAsResearcher),
                journalAI, 30, date(2023, 8, 5), "10.1000/xyz006", 20);

        ivanAsResearcher.publishPaper(ivanPaper);
        db.addPaper(ivanPaper);

        System.out.println(ivanAsResearcher);
        System.out.println("Ivan h-index (via adapter): " + ivanAsResearcher.calculateHIndex());

        try {
            ivanAsResearcher.joinProject(project);
        } catch (NonResearcherException e) {
            System.out.println("Unexpected: " + e.getMessage());
        }

        // --------------------------
        header("STUDENT ORGANIZATION");

        StudentOrganization aiClub = new StudentOrganization("AI Research Club");
        studentAsel.leadOrganization(aiClub);
        studentDamir.joinOrganization(aiClub);
        gradBerik.joinOrganization(aiClub);

        System.out.println(aiClub);
        for (Student m : aiClub.getMembers()) {
            System.out.println("  - " + m.getFullName());
        }

        // --------------------------
        header("MESSAGES & NEWS");

        profAliya.sendMessage(studentAsel, "Exam Reminder", "Don't forget the exam on Friday!");

        News generalNews = new News(NewsTopic.GENERAL,
                "University Anniversary",
                "We celebrate 30 years of our university!",
                dean);
        dean.manageNews(generalNews);

        List<News> allNews = studentAsel.viewNews();
        System.out.println("\nNews (" + allNews.size() + "):");
        for (News n : allNews) {
            System.out.println("  " + n);
        }

        studentAsel.commentOnNews(generalNews, "Congratulations to the university!");
        studentDamir.commentOnNews(generalNews, "30 years - amazing achievement!");

        System.out.println("\nComments on '" + generalNews.getTitle() + "':");
        for (var c : generalNews.getComments()) System.out.println("  " + c);

        db.announceTopCitedResearcher();

        // --------------------------
        header("COMPLAINTS");

        profAliya.sendComplaint(studentDamir, UrgencyLevel.MEDIUM, "Student missed 5 consecutive classes.");

        List<Complaint> complaints = dean.viewComplaints();
        if (!complaints.isEmpty()) {
            dean.considerComplaint(complaints.get(0));
        }

        // --------------------------
        header("TECH SUPPORT");

        profAliya.sendRequest("Projector in Room 101 is broken");
        lectureMarat.sendRequest("Need MATLAB license renewal");

        List<communication.Request> viewed = techDenis.viewRequests();
        if (viewed.size() >= 2) {
            techDenis.acceptRequest(viewed.get(0));
            techDenis.markDone(viewed.get(0));
            techDenis.rejectRequest(viewed.get(1));
        }

        dean.viewRequests();

        // --------------------------
        header("SORTING (Strategy)");

        System.out.println("--- Students by GPA ---");
        List<Student> byGpa = dean.viewStudentsSorted(UserComparators.STUDENT_BY_GPA);
        for (Student s : byGpa) {
            System.out.printf("  %-25s GPA: %.2f%n", s.getFullName(), s.getGpa());
        }

        System.out.println("\n--- Students by name ---");
        List<Student> byName = dean.viewStudentsSorted(UserComparators.STUDENT_BY_NAME);
        for (Student s : byName) {
            System.out.println("  " + s.getFullName());
        }

        System.out.println("\n--- Teachers by rating ---");
        List<Teacher> byRating = dean.viewTeachersSorted(UserComparators.TEACHER_BY_RATING);
        for (Teacher t : byRating) {
            System.out.printf("  %-25s Rating: %.2f%n", t.getFullName(), t.getAverageRating());
        }

        // --------------------------
        header("REPORT");

        Report report = dean.createReport();
        System.out.println(report);

        // --------------------------
        header("CITATION FORMATS");

        System.out.println(paper1.getCitation(Format.PLAIN_TEXT));
        System.out.println(paper1.getCitation(Format.BIBTEX));

        // --------------------------
        header("TOP CITED RESEARCHER");

        Researcher top = db.getTopCitedResearcher();
        if (top != null) {
            System.out.println("Top cited: " + top.getName() + " (" + top.getTotalCitations() + " citations)");
        }

        Researcher topByYear = db.getTopCitedResearcherByYear(2023);
        if (topByYear != null) {
            System.out.println("Top cited in 2023: " + topByYear.getName());
        }

        // --------------------------
        header("LANGUAGE CHANGE");

        studentAsel.changeLanguage(Language.KZ);
        studentDamir.changeLanguage(Language.RU);
        System.out.println(studentAsel.getFullName() + " language: " + studentAsel.getLanguage().getDisplayName());
        System.out.println(studentDamir.getFullName() + " language: " + studentDamir.getLanguage().getDisplayName());

        // --------------------------
        header("DROP COURSE");

        System.out.println("before drop: " + studentAsel.getTotalCredits());
        studentAsel.dropCourse(math);
        System.out.println("after drop: " + studentAsel.getTotalCredits());

        // --------------------------
        header("ADMIN LOGS");

        admin.updateUser(studentAsel);
        List<storage.LogEntry> logs = admin.viewLogs();
        System.out.println("Total log entries: " + logs.size());
        int from = Math.max(0, logs.size() - 5);
        for (int i = from; i < logs.size(); i++) {
            System.out.println("  " + logs.get(i));
        }

        // --------------------------
        header("SAVE & LOAD");

        db.saveData();
        db.loadData();
        System.out.println("Users after reload: " + db.getUsers().size());
        System.out.println("Courses after reload: " + db.getCourses().size());
        System.out.println("Papers after reload: " + db.getAllPapers().size());

        System.out.println("\nDone.");
    }
}