package storage;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import users.User;
import users.Student;
import users.Teacher;
import academic.Course;
import academic.Attendance;
import research.ResearchPaper;
import research.ResearchProject;
import research.Researcher;
import research.Journal;
import communication.News;
import communication.Message;
import communication.Request;
import enums.NewsTopic;

public class DataStorage implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FILE_NAME = "university_data.ser";

    private static DataStorage instance;

    private List<User> users;
    private List<Course> courses;
    private List<News> news;
    private List<Journal> journals;
    private List<Request> requests;
    private List<Message> messages;
    private List<LogEntry> logs;
    private List<ResearchPaper> allPapers;
    private List<ResearchProject> projects;
    private List<Attendance> attendances;

    private DataStorage() {
        users = new ArrayList<>();
        courses = new ArrayList<>();
        news = new ArrayList<>();
        journals = new ArrayList<>();
        requests = new ArrayList<>();
        messages = new ArrayList<>();
        logs = new ArrayList<>();
        allPapers = new ArrayList<>();
        projects = new ArrayList<>();
        attendances = new ArrayList<>();
    }

    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(this);
            System.out.println("Data saved to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public void loadData() {
        File f = new File(FILE_NAME);
        if (!f.exists()) {
            System.out.println("No saved data found.");
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            DataStorage loaded = (DataStorage) ois.readObject();
            this.users = loaded.users;
            this.courses = loaded.courses;
            this.news = loaded.news;
            this.journals = loaded.journals;
            this.requests = loaded.requests;
            this.messages = loaded.messages;
            this.logs = loaded.logs;
            this.allPapers = loaded.allPapers;
            this.projects = loaded.projects;
            this.attendances = loaded.attendances;
            System.out.println("Data loaded from " + FILE_NAME);
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }


    public void addUser(User u) { if (!users.contains(u)) users.add(u); }
    public void removeUser(User u) { users.remove(u); }
    public void addCourse(Course c) { if (!courses.contains(c)) courses.add(c); }
    public void addNews(News n) { news.add(n); Collections.sort(news); }
    public void addJournal(Journal j) { if (!journals.contains(j)) journals.add(j); }
    public void addRequest(Request r) { requests.add(r); }
    public void addMessage(Message m) { messages.add(m); }
    public void addLog(LogEntry e) { logs.add(e); }
    public void addProject(ResearchProject p) { if (!projects.contains(p)) projects.add(p); }
    public void addAttendance(Attendance a) { attendances.add(a); }
    public List<Attendance> getAttendances() { return attendances; }


    public void addPaper(ResearchPaper p) {
        if (!allPapers.contains(p)) {
            allPapers.add(p);
            announceTopCitedResearcher();
        }
    }

    public List<User> getUsers() { return users; }
    public List<Course> getCourses() { return courses; }
    public List<News> getNews() { return news; }
    public List<Journal> getJournals() { return journals; }
    public List<Request> getRequests() { return requests; }
    public List<Message> getMessages() { return messages; }
    public List<LogEntry> getLogs() { return logs; }
    public List<ResearchPaper> getAllPapers() { return allPapers; }
    public List<ResearchProject> getProjects() { return projects; }

    public User findUserById(String id) {
        for (User u : users) {
            if (u.getId().equals(id)) return u;
        }
        return null;
    }

    public Course findCourseByCode(String code) {
        for (Course c : courses) {
            if (c.getCode().equals(code)) return c;
        }
        return null;
    }

    // ── top cited: глобально ─────────────────────────────────────────────

    public Researcher getTopCitedResearcher() {
        Researcher top = null;
        int max = -1;
        for (User u : users) {
            if (u instanceof Researcher) {
                Researcher r = (Researcher) u;
                int citations = r.getTotalCitations();
                if (citations > max) {
                    max = citations;
                    top = r;
                }
            }
        }
        return top;
    }

    // ── top cited: по школе / департаменту ───────────────────────────────

    public Researcher getTopCitedResearcherBySchool(String school) {
        Researcher top = null;
        int max = -1;
        for (User u : users) {
            if (!(u instanceof Researcher)) continue;
            Researcher r = (Researcher) u;

            boolean inSchool = false;
            // для Student/GraduateStudent проверяем поле school
            if (u instanceof Student && school.equals(((Student) u).getSchool())) {
                inSchool = true;
            }
            // для Teacher проверяем department
            if (u instanceof Teacher && school.equals(((Teacher) u).getDepartment())) {
                inSchool = true;
            }
            // запасной вариант: имя журнала содержит название школы
            if (!inSchool) {
                for (ResearchPaper p : r.getPapers()) {
                    if (p.getJournal() != null &&
                        p.getJournal().getName().toLowerCase()
                         .contains(school.toLowerCase())) {
                        inSchool = true;
                        break;
                    }
                }
            }

            if (inSchool) {
                int citations = r.getTotalCitations();
                if (citations > max) {
                    max = citations;
                    top = r;
                }
            }
        }
        return top;
    }

    // ── top cited: по году публикации ─────────────────────────────────────

    public Researcher getTopCitedResearcherByYear(int year) {
        Researcher top = null;
        int max = -1;
        for (User u : users) {
            if (!(u instanceof Researcher)) continue;
            Researcher r = (Researcher) u;

            int citations = 0;
            for (ResearchPaper p : r.getPapers()) {
                if (p.getPublicationDate() != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(p.getPublicationDate());
                    if (cal.get(Calendar.YEAR) == year) {
                        citations += p.getCitations();
                    }
                }
            }
            if (citations > max) {
                max = citations;
                top = r;
            }
        }
        return top;
    }

    // ── печать всех бумаг университета отсортированных ───────────────────

    public void printAllPapers(Comparator<ResearchPaper> comparator) {
        List<ResearchPaper> all = new ArrayList<>(allPapers);
        all.sort(comparator);
        System.out.println("\n=== All Research Papers (" + all.size() + ") ===");
        for (ResearchPaper p : all) {
            System.out.println("  " + p);
        }
        System.out.println("=====================================\n");
    }

    // ── приватный метод: авто-новость о top cited ─────────────────────────

    private void announceTopCitedResearcher() {
        Researcher top = getTopCitedResearcher();
        if (top == null) return;
        if (top.getTotalCitations() == 0) return;

        String newsTitle = "Top Cited Researcher";
        String content = top.getName() + " is currently the top cited researcher " +
                         "with " + top.getTotalCitations() + " total citations.";

        // находим User-объект для поля author в News
        User author = null;
        for (User u : users) {
            if (u instanceof Researcher && u.equals(top)) {
                author = u;
                break;
            }
        }

        // удаляем старую новость с тем же заголовком, чтобы не копились дубликаты
        news.removeIf(n -> newsTitle.equals(n.getTitle()));

        News announcement = new News(NewsTopic.RESEARCH, newsTitle, content, author);
        news.add(announcement);
        Collections.sort(news);

        System.out.println("[AUTO NEWS] Top cited researcher updated: " +
                           top.getName() + " (" + top.getTotalCitations() + " citations)");
    }

}
