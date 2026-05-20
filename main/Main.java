package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import academic.Course;
import academic.Mark;
import communication.Complaint;
import communication.Message;
import communication.News;
import communication.Request;
import enums.*;
import exceptions.CreditLimitExceededException;
import exceptions.FailLimitExceededException;
import exceptions.LowHIndexException;
import exceptions.NonResearcherException;
import organization.StudentOrganization;
import research.Journal;
import research.ResearchPaper;
import research.ResearchProject;
import research.Researcher;
import storage.DataInitializer;
import storage.DataStorage;
import users.*;
import utils.UserComparators;
import utils.UserFactory;

public class Main {

    public static void main(String[] args) {
        DataInitializer.initializeData();
        DataStorage storage = DataStorage.getInstance();
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== ДОБРО ПОЖАЛОВАТЬ В УНИВЕРСИТЕТСКУЮ СИСТЕМУ ===");

        while (true) {
            System.out.println("\n--- Главное меню ---");
            System.out.println("1. Войти в систему");
            System.out.println("2. Общая статистика");
            System.out.println("3. Выйти");
            System.out.print("Выбор: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Email: ");
                String email = scanner.nextLine();
                System.out.print("Пароль: ");
                String password = scanner.nextLine();

                User loggedUser = null;
                for (User u : storage.getUsers()) {
                    if (u.getEmail().equalsIgnoreCase(email.trim())) {
                        loggedUser = u;
                        break;
                    }
                }

                if (loggedUser != null && loggedUser.login(email, password)) {
                    System.out.println("[Успешно]: Добро пожаловать, " + loggedUser.getFullName() + "!");
                    runUserMenu(loggedUser, scanner);
                } else {
                    System.out.println("[Ошибка]: Неверный email или пароль.");
                }
            } else if (choice.equals("2")) {
                System.out.println("\n--- Статистика системы ---");
                System.out.println("Пользователей: " + storage.getUsers().size());
                System.out.println("Курсов:        " + storage.getCourses().size());
                System.out.println("Организаций:   " + storage.getOrganizations().size());
            } else if (choice.equals("3")) {
                System.out.println("До свидания!");
                break;
            } else {
                System.out.println("[Ошибка]: Неверный ввод.");
            }
        }
        scanner.close();
    }

    // Роутер меню (GraduateStudent проверяется раньше Student)
    private static void runUserMenu(User user, Scanner scanner) {
        if (user instanceof Admin) runAdminMenu((Admin) user, scanner);
        else if (user instanceof Manager) runManagerMenu((Manager) user, scanner);
        else if (user instanceof Teacher) runTeacherMenu((Teacher) user, scanner);
        else if (user instanceof GraduateStudent) runGraduateStudentMenu((GraduateStudent) user, scanner);
        else if (user instanceof Student) runStudentMenu((Student) user, scanner);
        else if (user instanceof TechSupportSpecialist) runTechSupportMenu((TechSupportSpecialist) user, scanner);
        else {
            System.out.println("[Ошибка]: Роль не поддерживается.");
            user.logout();
        }
    }

    // Общие действия, доступные всем пользователям через сквозные команды
    private static boolean handleCommonActions(String choice, User user, Scanner scanner) {
        DataStorage storage = DataStorage.getInstance();
        switch (choice) {
            case "news":
                List<News> allNews = user.viewNews();
                if (allNews.isEmpty()) {
                    System.out.println("Новостей нет.");
                } else {
                    System.out.println("\n--- Новости (" + allNews.size() + ") ---");
                    for (int i = 0; i < allNews.size(); i++) {
                        System.out.println((i + 1) + ". " + allNews.get(i));
                    }
                    System.out.print("Номер для комментария (0 — пропустить): ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine()) - 1;
                        if (idx >= 0 && idx < allNews.size()) {
                            System.out.print("Комментарий: ");
                            user.commentOnNews(allNews.get(idx), scanner.nextLine());
                            System.out.println("[Успешно]: Комментарий добавлен.");
                        }
                    } catch (Exception ignored) {
                    }
                }
                return true;

            case "journal":
                List<Journal> journals = storage.getJournals();
                if (journals.isEmpty()) {
                    System.out.println("Журналов нет.");
                    return true;
                }
                System.out.println("\n--- Доступные журналы ---");
                for (int i = 0; i < journals.size(); i++) {
                    System.out.println((i + 1) + ". " + journals.get(i).getName()
                            + " (подписчиков: " + journals.get(i).getSubscriberCount() + ")");
                }
                System.out.print("Выберите номер для подписки (0 — назад): ");
                try {
                    int idx = Integer.parseInt(scanner.nextLine()) - 1;
                    if (idx >= 0 && idx < journals.size()) {
                        user.subscribeToJournal(journals.get(idx));
                    }
                } catch (Exception ignored) {
                }
                return true;

            case "lang":
                System.out.println("\n--- Смена языка / Change Language ---");
                System.out.println("1. English  2. Русский  3. Қазақша");
                System.out.print("Выбор: ");
                String l = scanner.nextLine();
                if (l.equals("1")) user.changeLanguage(Language.EN);
                else if (l.equals("2")) user.changeLanguage(Language.RU);
                else if (l.equals("3")) user.changeLanguage(Language.KZ);
                return true;

            default:
                return false;
        }
    }

    // ── ADMIN ──────────────────────────────────────────────────────────
    private static void runAdminMenu(Admin admin, Scanner scanner) {
        DataStorage storage = DataStorage.getInstance();
        boolean active = true;
        while (active) {
            System.out.println("\n--- МЕНЮ АДМИНИСТРАТОРА (" + admin.getFullName() + ") ---");
            System.out.println("1.  Системные логи");
            System.out.println("2.  Создать пользователя");
            System.out.println("3.  Список пользователей");
            System.out.println("4.  Удалить пользователя");
            System.out.println("5.  Мой профиль");
            System.out.println("--- Общие действия ---");
            System.out.println("6.  Входящие сообщения");
            System.out.println("7.  Новости / комментировать");
            System.out.println("8.  Подписаться на журнал");
            System.out.println("9.  Мои подписки");
            System.out.println("10. Сменить язык");
            System.out.println("0.  Выйти");
            System.out.print("Выбор: ");
            String choice = scanner.nextLine();

            if (handleCommonActions(choice.equals("7") ? "news"
                    : choice.equals("8") ? "journal"
                    : choice.equals("10") ? "lang" : "", admin, scanner)) continue;

            switch (choice) {
                case "1":
                    admin.viewLogs();
                    break;
                case "2":
                    System.out.println("\nТип: 1.STUDENT  2.TEACHER  3.MANAGER  4.TECHSUPPORT");
                    System.out.print("Выбор типа: ");
                    String typeChoice = scanner.nextLine();
                    System.out.print("ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Имя: ");
                    String name = scanner.nextLine();
                    System.out.print("Фамилия: ");
                    String surname = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Пароль: ");
                    String pass = scanner.nextLine();
                    User newUser = null;
                    try {
                        if (typeChoice.equals("1")) {
                            System.out.print("Школа: ");
                            String school = scanner.nextLine();
                            System.out.print("Курс: ");
                            int year = Integer.parseInt(scanner.nextLine());
                            newUser = UserFactory.createUser("STUDENT", id, name, surname, email, pass, id, school, year);
                        } else if (typeChoice.equals("2")) {
                            System.out.print("Зарплата: ");
                            double sal = Double.parseDouble(scanner.nextLine());
                            System.out.print("Департамент: ");
                            String dept = scanner.nextLine();
                            newUser = UserFactory.createUser("TEACHER", id, name, surname, email, pass, sal, dept, TeacherPosition.LECTOR);
                        } else if (typeChoice.equals("3")) {
                            System.out.print("Зарплата: ");
                            double sal = Double.parseDouble(scanner.nextLine());
                            System.out.print("Департамент: ");
                            String dept = scanner.nextLine();
                            newUser = UserFactory.createUser("MANAGER", id, name, surname, email, pass, sal, dept, ManagerType.OFFICE_OF_REGISTRAR);
                        } else if (typeChoice.equals("4")) {
                            System.out.print("Зарплата: ");
                            double sal = Double.parseDouble(scanner.nextLine());
                            System.out.print("Департамент: ");
                            String dept = scanner.nextLine();
                            newUser = UserFactory.createUser("TECHSUPPORT", id, name, surname, email, pass, sal, dept);
                        }
                        if (newUser != null) {
                            storage.addUser(newUser);
                            System.out.println("[Успешно]: Пользователь " + newUser.getFullName() + " добавлен.");
                        } else {
                            System.out.println("[Ошибка]: Неверный тип пользователя.");
                        }
                    } catch (Exception e) {
                        System.out.println("[Ошибка создания]: " + e.getMessage());
                    }
                    break;
                case "3":
                    System.out.println("\n--- Список всех пользователей ---");
                    for (User u : storage.getUsers()) {
                        System.out.println("[" + u.getRole() + "] " + u.getFullName() + " | " + u.getEmail());
                    }
                    break;
                case "4":
                    System.out.print("Email для удаления: ");
                    String tEmail = scanner.nextLine();
                    User toRemove = null;
                    for (User u : storage.getUsers()) {
                        if (u.getEmail().equalsIgnoreCase(tEmail.trim())) {
                            toRemove = u;
                            break;
                        }
                    }
                    if (toRemove != null) {
                        storage.removeUser(toRemove);
                        System.out.println("[Успешно]: Пользователь удалён.");
                    } else {
                        System.out.println("[Ошибка]: Пользователь с таким email не найден.");
                    }
                    break;
                case "5":
                    System.out.println("\n--- Профиль Администратора ---");
                    System.out.println(admin);
                    break;
                case "6":
                    showInbox(admin, storage, scanner);
                    break;
                case "9":
                    showMySubscriptions(admin, storage);
                    break;
                case "0":
                    admin.logout();
                    active = false;
                    break;
                default:
                    System.out.println("Неверный ввод.");
            }
        }
    }

    // ── MANAGER ────────────────────────────────────────────────────────
    private static void runManagerMenu(Manager manager, Scanner scanner) {
        DataStorage storage = DataStorage.getInstance();
        boolean active = true;
        while (active) {
            System.out.println("\n--- МЕНЮ МЕНЕДЖЕРА (" + manager.getFullName() + ") ---");
            System.out.println("1.  Студенты по GPA");
            System.out.println("2.  Академический отчёт");
            System.out.println("3.  Просмотреть жалобы");
            System.out.println("4.  Рассмотреть жалобу");
            System.out.println("5.  Добавить курс");
            System.out.println("6.  Назначить курс преподавателю");
            System.out.println("7.  Управление новостями");
            System.out.println("--- Общие действия ---");
            System.out.println("8.  Входящие сообщения");
            System.out.println("9.  Новости / комментировать");
            System.out.println("10. Подписаться на журнал");
            System.out.println("11. Мои подписки");
            System.out.println("12. Сменить язык");
            System.out.println("0.  Выйти");
            System.out.print("Выбор: ");
            String choice = scanner.nextLine();

            if (handleCommonActions(choice.equals("9") ? "news"
                    : choice.equals("10") ? "journal"
                    : choice.equals("12") ? "lang" : "", manager, scanner)) continue;

            switch (choice) {
                case "1":
                    System.out.println("\n--- Студенты, отсортированные по GPA ---");
                    for (Student s : manager.viewStudentsSorted(UserComparators.STUDENT_BY_GPA)) {
                        System.out.println(s.getFullName() + " | GPA: " + String.format("%.2f", s.getGpa()));
                    }
                    break;
                case "2":
                    System.out.println("\n--- Академический отчёт ---");
                    System.out.println(manager.createReport().getContent());
                    break;
                case "3":
                    manager.viewComplaints();
                    break;
                case "4":
                    List<Message> msgs = storage.getMessages();
                    List<Complaint> complaints = new ArrayList<>();
                    for (Message m : msgs) {
                        if (m instanceof Complaint) complaints.add((Complaint) m);
                    }
                    if (complaints.isEmpty()) {
                        System.out.println("Активных жалоб нет.");
                        break;
                    }
                    System.out.println("\n--- Список жалоб ---");
                    for (int i = 0; i < complaints.size(); i++) {
                        System.out.println((i + 1) + ". " + complaints.get(i));
                    }
                    System.out.print("Выберите номер для рассмотрения: ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine()) - 1;
                        if (idx >= 0 && idx < complaints.size()) {
                            manager.considerComplaint(complaints.get(idx));
                        } else {
                            System.out.println("[Ошибка]: Неверный индекс.");
                        }
                    } catch (Exception e) {
                        System.out.println("[Ошибка]: " + e.getMessage());
                    }
                    break;
                case "5":
                    try {
                        System.out.println("\n--- Создание нового курса ---");
                        System.out.print("Код курса (например, CS101): ");
                        String code = scanner.nextLine();
                        System.out.print("Название: ");
                        String cname = scanner.nextLine();
                        System.out.print("Количество кредитов: ");
                        int credits = Integer.parseInt(scanner.nextLine());
                        System.out.print("Вместимость (мест): ");
                        int seats = Integer.parseInt(scanner.nextLine());
                        Course nc = new Course(code, cname, credits, CourseType.MAJOR, "FIT", seats);
                        storage.addCourse(nc);
                        System.out.println("[Успешно]: Курс \"" + cname + "\" добавлен в базу.");
                    } catch (Exception e) {
                        System.out.println("[Ошибка ввода данных]: " + e.getMessage());
                    }
                    break;
                case "6":
                    List<Course> courses = storage.getCourses();
                    if (courses.isEmpty()) {
                        System.out.println("Курсы в базе отсутствуют.");
                        break;
                    }
                    System.out.println("\n--- Выберите курс ---");
                    for (int i = 0; i < courses.size(); i++) {
                        System.out.println((i + 1) + ". " + courses.get(i).getCode() + " — " + courses.get(i).getName());
                    }
                    System.out.print("Курс №: ");
                    try {
                        int cIdx = Integer.parseInt(scanner.nextLine()) - 1;
                        List<Teacher> teachers = storage.getUsers().stream()
                                .filter(u -> u instanceof Teacher).map(u -> (Teacher) u).toList();

                        if (teachers.isEmpty()) {
                            System.out.println("Преподаватели отсутствуют.");
                            break;
                        }
                        System.out.println("\n--- Выберите преподавателя ---");
                        for (int i = 0; i < teachers.size(); i++) {
                            System.out.println((i + 1) + ". " + teachers.get(i).getFullName());
                        }
                        System.out.print("Преподаватель №: ");
                        int tIdx = Integer.parseInt(scanner.nextLine()) - 1;

                        if (cIdx >= 0 && cIdx < courses.size() && tIdx >= 0 && tIdx < teachers.size()) {
                            manager.assignCourseToTeacher(courses.get(cIdx), teachers.get(tIdx), LessonType.LECTURE);
                        } else {
                            System.out.println("[Ошибка]: Неверные индексы выбора.");
                        }
                    } catch (Exception e) {
                        System.out.println("[Ошибка назначения]: " + e.getMessage());
                    }
                    break;
                case "7":
                    System.out.println("\n--- Публикация новости ---");
                    System.out.print("Заголовок: ");
                    String newsTitle = scanner.nextLine();
                    System.out.print("Текст: ");
                    String newsContent = scanner.nextLine();
                    System.out.println("Тема: 1.GENERAL  2.ACADEMIC  3.EVENT");
                    System.out.print("Выбор: ");
                    String topicChoice = scanner.nextLine();
                    NewsTopic topic = topicChoice.equals("2") ? NewsTopic.ACADEMIC
                            : topicChoice.equals("3") ? NewsTopic.EVENT : NewsTopic.GENERAL;
                    manager.manageNews(new News(topic, newsTitle, newsContent, manager));
                    break;
                case "8":
                    showInbox(manager, storage, scanner);
                    break;
                case "11":
                    showMySubscriptions(manager, storage);
                    break;
                case "0":
                    manager.logout();
                    active = false;
                    break;
                default:
                    System.out.println("Неверный ввод.");
            }
        }
    }

    // ── TEACHER ────────────────────────────────────────────────────────
    private static void runTeacherMenu(Teacher teacher, Scanner scanner) {
        DataStorage storage = DataStorage.getInstance();
        boolean active = true;
        while (active) {
            System.out.println("\n--- МЕНЮ ПРЕПОДАВАТЕЛЯ (" + teacher.getFullName() + ") ---");
            System.out.println("1.  Мои курсы и студенты");
            System.out.println("2.  Поставить оценку");
            System.out.println("3.  Отметить посещаемость");
            System.out.println("4.  Отчёт по оценкам");
            System.out.println("5.  Отправить жалобу");
            System.out.println("--- Исследования (Researcher) ---");
            System.out.println("6.  H-index и цитирования");
            System.out.println("7.  Опубликовать статью");
            System.out.println("8.  Список статей");
            System.out.println("9.  Получить цитату");
            System.out.println("10. Вступить в исследовательский проект");
            System.out.println("--- Общие действия ---");
            System.out.println("11. Входящие сообщения");
            System.out.println("12. Отправить сообщение");
            System.out.println("13. Отправить заявку в IT");
            System.out.println("14. Мои заявки (статус)");
            System.out.println("15. Новости / комментировать");
            System.out.println("16. Подписаться на журнал");
            System.out.println("17. Мои подписки");
            System.out.println("18. Сменить язык");
            System.out.println("0.  Выйти");
            System.out.print("Выбор: ");
            String choice = scanner.nextLine();

            if (handleCommonActions(choice.equals("15") ? "news"
                    : choice.equals("16") ? "journal"
                    : choice.equals("18") ? "lang" : "", teacher, scanner)) continue;

            switch (choice) {
                case "1":
                    List<Course> myCourses = teacher.viewCourses();
                    if (myCourses.isEmpty()) {
                        System.out.println("У вас нет назначенных курсов.");
                        break;
                    }
                    for (Course c : myCourses) {
                        System.out.println("\n" + c.getCode() + " — " + c.getName());
                        List<Student> enrolled = storage.getStudentsForCourse(c);
                        if (enrolled.isEmpty()) {
                            System.out.println("  (нет зарегистрированных студентов)");
                        } else {
                            enrolled.forEach(s -> System.out.println("  - " + s.getFullName() + " (" + s.getStudentId() + ")"));
                        }
                    }
                    break;
                case "2":
                    List<Course> cForMark = teacher.viewCourses();
                    if (cForMark.isEmpty()) {
                        System.out.println("Нет доступных курсов.");
                        break;
                    }
                    System.out.println("\n--- Выберите курс ---");
                    printList(cForMark, Course::getName);
                    System.out.print("Курс №: ");
                    try {
                        int cIdx = Integer.parseInt(scanner.nextLine()) - 1;
                        if (cIdx < 0 || cIdx >= cForMark.size()) {
                            System.out.println("[Ошибка]: Неверный индекс.");
                            break;
                        }
                        System.out.print("Student ID: ");
                        Student ts = findStudentById(scanner.nextLine().trim(), storage);
                        if (ts == null) {
                            System.out.println("[Ошибка]: Студент не найден.");
                            break;
                        }
                        System.out.print("1-я аттестация (0-30): ");
                        double fa = Double.parseDouble(scanner.nextLine());
                        System.out.print("2-я аттестация (0-30): ");
                        double sa = Double.parseDouble(scanner.nextLine());
                        System.out.print("Финальный экзамен (0-40): ");
                        double fe = Double.parseDouble(scanner.nextLine());

                        Mark m = new Mark(fa, sa, fe);
                        teacher.putMark(ts, cForMark.get(cIdx), m);
                        System.out.println("[Успешно]: Оценка выставлена: " + m.getLetterGrade() + " (" + String.format("%.2f", m.getTotal()) + ")");
                    } catch (IllegalArgumentException e) {
                        System.out.println("[Ошибка валидации баллов]: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("[Ошибка ввода]: " + e.getMessage());
                    }
                    break;
                case "3":
                    List<Course> cForAtt = teacher.viewCourses();
                    if (cForAtt.isEmpty()) {
                        System.out.println("Нет доступных курсов.");
                        break;
                    }
                    System.out.println("\n--- Выберите курс ---");
                    printList(cForAtt, Course::getName);
                    System.out.print("Курс №: ");
                    try {
                        int cIdx = Integer.parseInt(scanner.nextLine()) - 1;
                        if (cIdx < 0 || cIdx >= cForAtt.size()) {
                            System.out.println("[Ошибка]: Неверный индекс.");
                            break;
                        }
                        System.out.print("Student ID: ");
                        Student ts = findStudentById(scanner.nextLine().trim(), storage);
                        if (ts == null) {
                            System.out.println("[Ошибка]: Студент не найден.");
                            break;
                        }
                        System.out.print("Присутствует? (y/n): ");
                        teacher.markAttendance(ts, cForAtt.get(cIdx), scanner.nextLine().equalsIgnoreCase("y"));
                        System.out.println("[Успешно]: Посещаемость отмечена.");
                    } catch (Exception e) {
                        System.out.println("[Ошибка]: " + e.getMessage());
                    }
                    break;
                case "4":
                    List<Course> cForRep = teacher.viewCourses();
                    if (cForRep.isEmpty()) {
                        System.out.println("Нет доступных курсов.");
                        break;
                    }
                    System.out.println("\n--- Выберите курс для генерации отчёта ---");
                    printList(cForRep, Course::getName);
                    System.out.print("Курс №: ");
                    try {
                        int cIdx = Integer.parseInt(scanner.nextLine()) - 1;
                        if (cIdx >= 0 && cIdx < cForRep.size()) {
                            teacher.generateMarkReport(cForRep.get(cIdx));
                        } else {
                            System.out.println("[Ошибка]: Неверный индекс.");
                        }
                    } catch (Exception e) {
                        System.out.println("[Ошибка генерации отчёта]: " + e.getMessage());
                    }
                    break;
                case "5":
                    System.out.print("Student ID нарушителя: ");
                    Student bad = findStudentById(scanner.nextLine().trim(), storage);
                    if (bad == null) {
                        System.out.println("[Ошибка]: Студент не найден.");
                        break;
                    }
                    System.out.print("Текст жалобы: ");
                    teacher.sendComplaint(bad, UrgencyLevel.HIGH, scanner.nextLine());
                    System.out.println("[Успешно]: Жалоба отправлена менеджеру.");
                    break;
                case "6":
                    System.out.println("\n--- Наукометрические показатели ---");
                    System.out.println("H-index:     " + teacher.calculateHIndex());
                    System.out.println("Цитирований: " + teacher.getTotalCitations());
                    System.out.println("Статей:      " + teacher.getPapers().size());
                    break;
                case "7":
                    publishPaperFlow(teacher, scanner, storage);
                    break;
                case "8":
                    if (teacher.getPapers().isEmpty()) {
                        System.out.println("У вас пока нет публикаций.");
                        break;
                    }
                    System.out.println("Сортировка статей: 1. По дате  2. По цитированиям");
                    System.out.print("Выбор: ");
                    teacher.printPapers(scanner.nextLine().equals("2")
                            ? utils.ResearchPaperComparators.BY_CITATIONS
                            : utils.ResearchPaperComparators.BY_DATE);
                    break;
                case "9":
                    getCitationFlow(teacher.getPapers(), scanner);
                    break;
                case "10":
                    joinProjectFlow(teacher, scanner, storage);
                    break;
                case "11":
                    showInbox(teacher, storage, scanner);
                    break;
                case "12":
                    sendMessageFlow(teacher, scanner, storage);
                    break;
                case "13":
                    System.out.print("Опишите техническую проблему: ");
                    teacher.sendRequest(scanner.nextLine());
                    System.out.println("[Успешно]: Заявка зарегистрирована.");
                    break;
                case "14":
                    showMyRequests(teacher, storage);
                    break;
                case "17":
                    showMySubscriptions(teacher, storage);
                    break;
                case "0":
                    teacher.logout();
                    active = false;
                    break;
                default:
                    System.out.println("Неверный ввод.");
            }
        }
    }

    // ── STUDENT ────────────────────────────────────────────────────────
    private static void runStudentMenu(Student student, Scanner scanner) {
        DataStorage storage = DataStorage.getInstance();
        boolean active = true;
        while (active) {
            System.out.println("\n--- МЕНЮ СТУДЕНТА (" + student.getFullName() + ") ---");
            System.out.println("Текущий GPA: " + String.format("%.2f", student.getGpa())
                    + " | Кредиты: " + student.getTotalCredits() + "/" + Student.MAX_CREDITS);
            System.out.println("1.  Посмотреть транскрипт");
            System.out.println("2.  Мои курсы и оценки");
            System.out.println("3.  Зарегистрироваться на курс");
            System.out.println("4.  Отчислиться с курса");
            System.out.println("5.  История посещаемости");
            System.out.println("6.  Оценить преподавателя (1.0–5.0)");
            System.out.println("7.  Студенческие организации");
            System.out.println("8.  Стать главой организации");
            System.out.println("--- Общие действия ---");
            System.out.println("9.  Входящие сообщения");
            System.out.println("10. Новости / комментировать");
            System.out.println("11. Подписаться на журнал");
            System.out.println("12. Мои подписки");
            System.out.println("13. Сменить язык");
            System.out.println("0.  Выйти");
            System.out.print("Выбор: ");
            String choice = scanner.nextLine();

            if (handleCommonActions(choice.equals("10") ? "news"
                    : choice.equals("11") ? "journal"
                    : choice.equals("13") ? "lang" : "", student, scanner)) continue;

            switch (choice) {
                case "1":
                    System.out.println("\n--- Официальный транскрипт ---");
                    System.out.println(student.viewTranscript().generate());
                    break;
                case "2":
                    if (student.getCourses().isEmpty()) {
                        System.out.println("Вы не зарегистрированы ни на один курс.");
                        break;
                    }
                    System.out.println("\n--- Мои курсы и текущие оценки ---");
                    for (Course c : student.getCourses()) {
                        Mark m = student.getMarkFor(c);
                        String grade = (m != null) ? m.getLetterGrade() + " (" + String.format("%.2f", m.getTotal()) + ")" : "Оценка отсутствует";
                        System.out.println(" - " + c.getCode() + ": " + c.getName() + " [" + c.getCredits() + " cr] | " + grade);
                    }
                    break;
                case "3":
                    List<Course> nonReg = storage.getCourses().stream()
                            .filter(c -> !student.getCourses().contains(c)).toList();
                    if (nonReg.isEmpty()) {
                        System.out.println("Нет доступных новых курсов для регистрации.");
                        break;
                    }
                    System.out.println("\n--- Доступные курсы ---");
                    for (int i = 0; i < nonReg.size(); i++) {
                        Course c = nonReg.get(i);
                        System.out.println((i + 1) + ". " + c.getCode() + " — " + c.getName()
                                + " (" + c.getCredits() + " cr, мест: " + c.getCapacity() + ")");
                    }
                    System.out.print("Выберите номер курса для регистрации: ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine()) - 1;
                        if (idx >= 0 && idx < nonReg.size()) {
                            student.registerForCourse(nonReg.get(idx));
                            System.out.println("[Успешно]: Вы зарегистрированы на курс.");
                        } else {
                            System.out.println("[Ошибка]: Неверный индекс выборки.");
                        }
                    } catch (CreditLimitExceededException | FailLimitExceededException e) {
                        System.out.println("[ОТКАЗ РЕГИСТРАЦИИ]: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("[Ошибка]: " + e.getMessage());
                    }
                    break;
                case "4":
                    if (student.getCourses().isEmpty()) {
                        System.out.println("У вас нет курсов для удаления.");
                        break;
                    }
                    List<Course> enrolled = new ArrayList<>(student.getCourses());
                    System.out.println("\n--- С каких курсов вы хотите отчислиться? ---");
                    printList(enrolled, Course::getName);
                    System.out.print("Курс №: ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine()) - 1;
                        if (idx >= 0 && idx < enrolled.size()) {
                            student.dropCourse(enrolled.get(idx));
                            System.out.println("[Успешно]: Вы отчислены с курса.");
                        } else {
                            System.out.println("[Ошибка]: Неверный индекс.");
                        }
                    } catch (Exception e) {
                        System.out.println("[Ошибка удаления]: " + e.getMessage());
                    }
                    break;
                case "5":
                    System.out.println("\n--- История посещаемости ---");
                    List<academic.Attendance> atts = student.viewAttendance();
                    if (atts.isEmpty()) System.out.println("Записей о посещаемости пока нет.");
                    else atts.forEach(System.out::println);
                    break;
                case "6":
                    rateTeacherFlow(student, scanner, storage);
                    break;
                case "7":
                    joinOrgFlow(student, scanner, storage);
                    break;
                case "8":
                    leadOrgFlow(student, scanner, storage);
                    break;
                case "9":
                    showInbox(student, storage, scanner);
                    break;
                case "12":
                    showMySubscriptions(student, storage);
                    break;
                case "0":
                    student.logout();
                    active = false;
                    break;
                default:
                    System.out.println("Неверный ввод.");
            }
        }
    }

    // ── GRADUATE STUDENT ───────────────────────────────────────────────
    private static void runGraduateStudentMenu(GraduateStudent grad, Scanner scanner) {
        DataStorage storage = DataStorage.getInstance();
        boolean active = true;
        while (active) {
            System.out.println("\n--- МЕНЮ АСПИРАНТА (" + grad.getFullName() + ", " + grad.getDegree() + ") ---");
            System.out.println("GPA: " + String.format("%.2f", grad.getGpa())
                    + " | Кредиты: " + grad.getTotalCredits() + "/" + Student.MAX_CREDITS);
            System.out.println("--- Учебный блок (Студент) ---");
            System.out.println("1.  Посмотреть транскрипт");
            System.out.println("2.  Мои курсы и оценки");
            System.out.println("3.  Зарегистрироваться на курс");
            System.out.println("4.  Отчислиться с курса");
            System.out.println("5.  История посещаемости");
            System.out.println("6.  Оценить преподавателя (1.0–5.0)");
            System.out.println("7.  Студенческие организации");
            System.out.println("8.  Стать главой организации");
            System.out.println("--- Исследовательский блок (Researcher) ---");
            System.out.println("9.  Назначить научного руководителя");
            System.out.println("10. Добавить дипломную статью");
            System.out.println("11. Опубликовать обычную статью");
            System.out.println("12. H-index и цитирования");
            System.out.println("13. Список статей");
            System.out.println("14. Получить цитату");
            System.out.println("15. Вступить в исследовательский проект");
            System.out.println("--- Общие действия ---");
            System.out.println("16. Входящие сообщения");
            System.out.println("17. Новости / комментировать");
            System.out.println("18. Подписаться на журнал");
            System.out.println("19. Мои подписки");
            System.out.println("20. Сменить язык");
            System.out.println("0.  Выйти");
            System.out.print("Выбор: ");
            String choice = scanner.nextLine();

            if (handleCommonActions(choice.equals("17") ? "news"
                    : choice.equals("18") ? "journal"
                    : choice.equals("20") ? "lang" : "", grad, scanner)) continue;

            switch (choice) {
                case "1":
                    System.out.println(grad.viewTranscript().generate());
                    break;
                case "2":
                    if (grad.getCourses().isEmpty()) {
                        System.out.println("Нет курсов.");
                        break;
                    }
                    for (Course c : grad.getCourses()) {
                        Mark m = grad.getMarkFor(c);
                        System.out.println(" - " + c.getName() + " | " + (m != null ? m.getLetterGrade() : "Нет оценки"));
                    }
                    break;
                case "3":
                    List<Course> nonReg = storage.getCourses().stream()
                            .filter(c -> !grad.getCourses().contains(c)).toList();
                    if (nonReg.isEmpty()) {
                        System.out.println("Новых курсов нет.");
                        break;
                    }
                    printList(nonReg, c -> c.getCode() + " — " + c.getName() + " (" + c.getCredits() + " cr)");
                    System.out.print("Курс №: ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine()) - 1;
                        if (idx >= 0 && idx < nonReg.size()) grad.registerForCourse(nonReg.get(idx));
                    } catch (CreditLimitExceededException | FailLimitExceededException e) {
                        System.out.println("[ОТКАЗ]: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("[Ошибка]: " + e.getMessage());
                    }
                    break;
                case "4":
                    if (grad.getCourses().isEmpty()) {
                        System.out.println("Курсов нет.");
                        break;
                    }
                    List<Course> enr = new ArrayList<>(grad.getCourses());
                    printList(enr, Course::getName);
                    System.out.print("Курс №: ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine()) - 1;
                        if (idx >= 0 && idx < enr.size()) grad.dropCourse(enr.get(idx));
                    } catch (Exception e) {
                        System.out.println("[Ошибка]: " + e.getMessage());
                    }
                    break;
                case "5":
                    grad.viewAttendance().forEach(System.out::println);
                    break;
                case "6":
                    rateTeacherFlow(grad, scanner, storage);
                    break;
                case "7":
                    joinOrgFlow(grad, scanner, storage);
                    break;
                case "8":
                    leadOrgFlow(grad, scanner, storage);
                    break;
                case "9":
                    List<User> researchers = storage.getUsers().stream()
                            .filter(u -> u instanceof Researcher).toList();
                    if (researchers.isEmpty()) {
                        System.out.println("Нет доступных исследователей.");
                        break;
                    }
                    System.out.println("\n--- Доступные ученые ---");
                    for (int i = 0; i < researchers.size(); i++) {
                        Researcher r = (Researcher) researchers.get(i);
                        System.out.println((i + 1) + ". " + r.getName() + " (h-index: " + r.calculateHIndex() + ")");
                    }
                    System.out.print("Выберите руководителя №: ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine()) - 1;
                        if (idx >= 0 && idx < researchers.size()) {
                            grad.setSupervisor((Researcher) researchers.get(idx));
                            System.out.println("[Успешно]: Научный руководитель назначен.");
                        }
                    } catch (LowHIndexException e) {
                        System.out.println("[ОТКАЗ НАЗНАЧЕНИЯ]: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("[Ошибка]: " + e.getMessage());
                    }
                    break;
                case "10":
                    publishPaperFlow(grad, scanner, storage, true);
                    break;
                case "11":
                    publishPaperFlow(grad, scanner, storage, false);
                    break;
                case "12":
                    System.out.println("\n--- Наукометрические показатели аспиранта ---");
                    System.out.println("H-index:     " + grad.calculateHIndex());
                    System.out.println("Цитирований: " + grad.getTotalCitations());
                    break;
                case "13":
                    if (grad.getPapers().isEmpty()) {
                        System.out.println("У вас нет научных статей.");
                        break;
                    }
                    grad.printPapers(utils.ResearchPaperComparators.BY_DATE);
                    break;
                case "14":
                    getCitationFlow(grad.getPapers(), scanner);
                    break;
                case "15":
                    joinProjectFlow(grad, scanner, storage);
                    break;
                case "16":
                    showInbox(grad, storage, scanner);
                    break;
                case "19":
                    showMySubscriptions(grad, storage);
                    break;
                case "0":
                    grad.logout();
                    active = false;
                    break;
                default:
                    System.out.println("Неверный ввод.");
            }
        }
    }

    // ── TECH SUPPORT ───────────────────────────────────────────────────
    private static void runTechSupportMenu(TechSupportSpecialist ts, Scanner scanner) {
        DataStorage storage = DataStorage.getInstance();
        boolean active = true;
        while (active) {
            System.out.println("\n--- МЕНЮ ТЕХПОДДЕРЖКИ (" + ts.getFullName() + ") ---");
            System.out.println("1. Просмотреть новые заявки");
            System.out.println("2. Принять заявку в работу");
            System.out.println("3. Отклонить заявку");
            System.out.println("4. Отметить заявку как выполненную");
            System.out.println("5. Входящие сообщения");
            System.out.println("6. Новости / комментировать");
            System.out.println("7. Сменить язык");
            System.out.println("0. Выйти");
            System.out.print("Выбор: ");
            String choice = scanner.nextLine();

            if (handleCommonActions(choice.equals("6") ? "news" : choice.equals("7") ? "lang" : "", ts, scanner)) continue;

            switch (choice) {
                case "1":
                    List<Request> newR = ts.viewRequests();
                    if (newR.isEmpty()) System.out.println("Новых необработанных заявлений нет.");
                    break;
                case "2":
                case "3":
                case "4":
                    List<Request> all = storage.getRequests();
                    if (all.isEmpty()) {
                        System.out.println("Список заявок пуст.");
                        break;
                    }
                    System.out.println("\n--- Все системные заявки ---");
                    printList(all, Request::toString);
                    System.out.print("Выберите номер заявки для обработки: ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine()) - 1;
                        if (idx >= 0 && idx < all.size()) {
                            if (choice.equals("2")) ts.acceptRequest(all.get(idx));
                            else if (choice.equals("3")) ts.rejectRequest(all.get(idx));
                            else ts.markDone(all.get(idx));
                        } else {
                            System.out.println("[Ошибка]: Неверный индекс заявки.");
                        }
                    } catch (Exception e) {
                        System.out.println("[Ошибка изменения статуса]: " + e.getMessage());
                    }
                    break;
                case "5":
                    showInbox(ts, storage, scanner);
                    break;
                case "0":
                    ts.logout();
                    active = false;
                    break;
                default:
                    System.out.println("Неверный ввод.");
            }
        }
    }

    // ── Вспомогательные инкапсулированные потоки (Flows) ─────────────────

    // Метод просмотра почты с возможностью поочередного открытия писем
    private static void showInbox(User user, DataStorage storage, Scanner scanner) {
        List<Message> inbox = new ArrayList<>();
        for (Message m : storage.getMessages()) {
            if (m.getReceiver() != null && m.getReceiver().equals(user)) {
                inbox.add(m);
            }
        }
        if (inbox.isEmpty()) {
            System.out.println("Входящих сообщений нет.");
            return;
        }
        System.out.println("\n--- Входящие сообщения (" + inbox.size() + ") ---");
        for (int i = 0; i < inbox.size(); i++) {
            Message m = inbox.get(i);
            System.out.println((i + 1) + ". От: " + m.getSender().getFullName() + " | Тема: " + m.getSubject());
        }
        System.out.print("Номер для прочтения (0 — назад): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < inbox.size()) {
                Message m = inbox.get(idx);
                System.out.println("\n========================================");
                System.out.println("От:    " + m.getSender().getFullName());
                System.out.println("Тема:  " + m.getSubject());
                System.out.println("Дата:  " + m.getSentDate());
                System.out.println("----------------------------------------");
                System.out.println("Текст: " + m.getContent());
                System.out.println("========================================");
            }
        } catch (Exception ignored) {
        }
    }

    private static void showMyRequests(User user, DataStorage storage) {
        List<Request> mine = new ArrayList<>();
        for (Request r : storage.getRequests()) {
            if (r.getSender() != null && r.getSender().equals(user)) {
                mine.add(r);
            }
        }
        if (mine.isEmpty()) {
            System.out.println("Вы еще не отправляли заявок техподдержке.");
            return;
        }
        System.out.println("\n--- Статус моих заявок (" + mine.size() + ") ---");
        for (Request r : mine) {
            String assignee = r.getAssignedTo() != null ? r.getAssignedTo().getFullName() : "не назначен";
            System.out.println("  [" + r.getStatus() + "] " + r.getDescription() + " | Исполнитель: " + assignee);
        }
    }

    private static void showMySubscriptions(User user, DataStorage storage) {
        List<Journal> mine = new ArrayList<>();
        for (Journal j : storage.getJournals()) {
            if (j.getSubscribers().contains(user)) mine.add(j);
        }
        if (mine.isEmpty()) {
            System.out.println("Вы не подписаны ни на один научный журнал.");
            return;
        }
        System.out.println("\n--- Мои подписки на журналы (" + mine.size() + ") ---");
        for (Journal j : mine) {
            System.out.println("  " + j.getName() + " — публикаций: " + j.getPublishedPapers().size());
            j.getPublishedPapers().forEach(p -> System.out.println("      • " + p.getTitle()));
        }
    }

    private static void rateTeacherFlow(Student student, Scanner scanner, DataStorage storage) {
        List<Teacher> teachers = storage.getUsers().stream()
                .filter(u -> u instanceof Teacher).map(u -> (Teacher) u).toList();
        if (teachers.isEmpty()) {
            System.out.println("Преподаватели для оценки отсутствуют.");
            return;
        }
        System.out.println("\n--- Рейтинг преподавателей ---");
        for (int i = 0; i < teachers.size(); i++) {
            System.out.println((i + 1) + ". " + teachers.get(i).getFullName()
                    + " (текущий рейтинг: " + String.format("%.2f", teachers.get(i).getAverageRating()) + ")");
        }
        System.out.print("Выберите номер для оценки: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx < 0 || idx >= teachers.size()) {
                System.out.println("[Ошибка]: Неверный индекс.");
                return;
            }
            System.out.print("Выставьте оценку (1.0–5.0): ");
            double rating = Double.parseDouble(scanner.nextLine());
            if (rating < 1.0 || rating > 5.0) {
                System.out.println("[Ошибка]: Оценка выходит за диапазон 1.0–5.0");
                return;
            }
            student.rateTeacher(teachers.get(idx), rating);
            System.out.println("[Успешно]: Ваш отзыв учтен в рейтинге преподавателя.");
        } catch (Exception e) {
            System.out.println("[Ошибка]: " + e.getMessage());
        }
    }

    private static void publishPaperFlow(Teacher teacher, Scanner scanner, DataStorage storage) {
        List<Journal> journals = storage.getJournals();
        if (journals.isEmpty()) {
            System.out.println("Нет доступных научных журналов для публикации.");
            return;
        }
        System.out.print("Название научной статьи: ");
        String title = scanner.nextLine();
        System.out.print("Идентификатор DOI: ");
        String doi = scanner.nextLine();
        System.out.print("Количество страниц: ");
        try {
            int pages = Integer.parseInt(scanner.nextLine());
            System.out.println("\n--- Выберите журнал ---");
            printList(journals, Journal::getName);
            System.out.print("Журнал №: ");
            int jIdx = Integer.parseInt(scanner.nextLine()) - 1;
            if (jIdx < 0 || jIdx >= journals.size()) {
                System.out.println("[Ошибка]: Неверный индекс журнала.");
                return;
            }
            List<Researcher> authors = new ArrayList<>();
            authors.add(teacher);
            ResearchPaper paper = new ResearchPaper(title, authors, journals.get(jIdx), pages, new java.util.Date(), doi, 0);
            teacher.publishPaper(paper);
            storage.addPaper(paper);
            System.out.println("[Успешно]: Статья успешно отправлена и опубликована.");
        } catch (Exception e) {
            System.out.println("[Ошибка публикации]: " + e.getMessage());
        }
    }

    private static void publishPaperFlow(GraduateStudent grad, Scanner scanner, DataStorage storage, boolean isDiploma) {
        List<Journal> journals = storage.getJournals();
        if (journals.isEmpty()) {
            System.out.println("Нет доступных журналов.");
            return;
        }
        System.out.print("Название статьи: ");
        String title = scanner.nextLine();
        System.out.print("DOI: ");
        String doi = scanner.nextLine();
        System.out.print("Количество страниц: ");
        try {
            int pages = Integer.parseInt(scanner.nextLine());
            System.out.println("\n--- Выберите журнал ---");
            printList(journals, Journal::getName);
            System.out.print("Журнал №: ");
            int jIdx = Integer.parseInt(scanner.nextLine()) - 1;
            if (jIdx < 0 || jIdx >= journals.size()) {
                System.out.println("[Ошибка]: Неверный индекс.");
                return;
            }
            List<Researcher> authors = new ArrayList<>();
            authors.add(grad);
            ResearchPaper paper = new ResearchPaper(title, authors, journals.get(jIdx), pages, new java.util.Date(), doi, 0);
            if (isDiploma) {
                grad.addDiplomaPaper(paper);
                System.out.println("[Успешно]: Статья добавлена в дипломную работу.");
            }
            grad.publishPaper(paper);
            storage.addPaper(paper);
            System.out.println("[Успешно]: Публикация завершена.");
        } catch (Exception e) {
            System.out.println("[Ошибка публикации]: " + e.getMessage());
        }
    }

    private static void joinProjectFlow(Researcher researcher, Scanner scanner, DataStorage storage) {
        List<ResearchProject> projects = storage.getProjects();
        if (projects.isEmpty()) {
            System.out.println("Нет активных исследовательских проектов.");
            return;
        }
        System.out.println("\n--- Доступные проекты ---");
        printList(projects, p -> p.getTopic() + " (" + p.getParticipants().size() + " уч.)");
        System.out.print("Проект №: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < projects.size()) {
                researcher.joinProject(projects.get(idx));
                System.out.println("[Успешно]: Вы добавлены в команду проекта.");
            } else {
                System.out.println("[Ошибка]: Неверный индекс.");
            }
        } catch (NonResearcherException e) {
            System.out.println("[ОТКАЗ]: У вас нет прав исследователя для вступления: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Ошибка]: " + e.getMessage());
        }
    }

    private static void getCitationFlow(List<ResearchPaper> papers, Scanner scanner) {
        if (papers.isEmpty()) {
            System.out.println("У вас нет статей для генерации цитат.");
            return;
        }
        System.out.println("\n--- Выберите статью ---");
        printList(papers, ResearchPaper::getTitle);
        System.out.print("Статья №: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx < 0 || idx >= papers.size()) {
                System.out.println("[Ошибка]: Неверный индекс.");
                return;
            }
            System.out.println("Формат вывода: 1. Plain Text  2. BibTeX");
            System.out.print("Выбор: ");
            String fmt = scanner.nextLine();
            System.out.println("\n--- Сгенерированная цитата ---");
            System.out.println(papers.get(idx).getCitation(fmt.equals("2") ? Format.BIBTEX : Format.PLAIN_TEXT));
        } catch (Exception e) {
            System.out.println("[Ошибка генерации]: " + e.getMessage());
        }
    }

    private static void sendMessageFlow(Employee sender, Scanner scanner, DataStorage storage) {
        List<User> users = storage.getUsers();
        System.out.println("\n--- Адресная книга университета ---");
        printList(users, u -> "[" + u.getRole() + "] " + u.getFullName());
        System.out.print("Выберите получателя №: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx < 0 || idx >= users.size()) {
                System.out.println("[Ошибка]: Неверный индекс.");
                return;
            }
            System.out.print("Тема сообщения: ");
            String subj = scanner.nextLine();
            System.out.print("Текст сообщения: ");
            String body = scanner.nextLine();
            sender.sendMessage(users.get(idx), subj, body);
            System.out.println("[Успешно]: Письмо доставлено адресату.");
        } catch (Exception e) {
            System.out.println("[Ошибка отправки]: " + e.getMessage());
        }
    }

    private static void joinOrgFlow(Student student, Scanner scanner, DataStorage storage) {
        List<StudentOrganization> orgs = storage.getOrganizations();
        if (orgs.isEmpty()) {
            System.out.println("Студенческие организации отсутствуют.");
            return;
        }
        System.out.println("Ваши текущие организации: " + student.getOrganizations().stream().map(StudentOrganization::getName).toList());
        System.out.println("\n--- Список всех организаций ---");
        printList(orgs, o -> o.getName() + " (глава: " + (o.getHead() != null ? o.getHead().getFullName() : "—") + ")");
        System.out.print("Вступить в № (0 — назад): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx < 0) return;
            if (idx < orgs.size()) {
                if (student.getOrganizations().contains(orgs.get(idx))) {
                    System.out.println("[Внимание]: Вы уже состоите в этой организации.");
                } else {
                    student.joinOrganization(orgs.get(idx));
                    System.out.println("[Успешно]: Вы успешно вступили в " + orgs.get(idx).getName());
                }
            }
        } catch (Exception e) {
            System.out.println("[Ошибка]: " + e.getMessage());
        }
    }

    private static void leadOrgFlow(Student student, Scanner scanner, DataStorage storage) {
        List<StudentOrganization> orgs = storage.getOrganizations();
        if (orgs.isEmpty()) {
            System.out.println("Организаций нет.");
            return;
        }
        System.out.println("\n--- Назначение руководителя организации ---");
        printList(orgs, o -> o.getName() + " (текущий глава: " + (o.getHead() != null ? o.getHead().getFullName() : "—") + ")");
        System.out.print("Возглавить № (0 — назад): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx < 0) return;
            if (idx < orgs.size()) {
                student.leadOrganization(orgs.get(idx));
                System.out.println("[Успешно]: Статус лидера обновлен.");
            }
        } catch (Exception e) {
            System.out.println("[Ошибка]: " + e.getMessage());
        }
    }

    // ── Утилитарные методы (Helpers) ──────────────────────────────────────

    private static Student findStudentById(String studentId, DataStorage storage) {
        for (Student s : storage.getStudents()) {
            if (s.getStudentId().equals(studentId)) return s;
        }
        return null;
    }

    @FunctionalInterface
    interface Formatter<T> {
        String format(T item);
    }

    private static <T> void printList(List<T> list, Formatter<T> fmt) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ". " + fmt.format(list.get(i)));
        }
    }
}