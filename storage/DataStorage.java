package storage;
import java.io.*;
import java.util.*;

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
    private Map<Course, List<Student>> enrollments;

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
        enrollments = new HashMap<>();
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

            this.users = loaded.users != null ? loaded.users : new ArrayList<>();
            this.courses = loaded.courses != null ? loaded.courses : new ArrayList<>();
            this.news = loaded.news != null ? loaded.news : new ArrayList<>();
            this.journals = loaded.journals != null ? loaded.journals : new ArrayList<>();
            this.requests = loaded.requests != null ? loaded.requests : new ArrayList<>();
            this.messages = loaded.messages != null ? loaded.messages : new ArrayList<>();
            this.logs = loaded.logs != null ? loaded.logs : new ArrayList<>();
            this.allPapers = loaded.allPapers != null ? loaded.allPapers : new ArrayList<>();
            this.projects = loaded.projects != null ? loaded.projects : new ArrayList<>();
            this.attendances = loaded.attendances != null ? loaded.attendances : new ArrayList<>();
            this.enrollments = loaded.enrollments != null ? loaded.enrollments : new HashMap<>();

            System.out.println("Data loaded successfully from " + FILE_NAME);

        } catch (ClassNotFoundException e) {
            System.out.println("Saved data is outdated or corrupted: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Couldn't read the save file: " + e.getMessage());
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

    public void enrollStudent(Course c, Student s) {
        enrollments.computeIfAbsent(c, k -> new ArrayList<>()).add(s);
    }

    public void unenrollStudent(Course c, Student s) {
        if (enrollments.containsKey(c)) enrollments.get(c).remove(s);
    }

    public List<Student> getStudentsForCourse(Course c) {
        return enrollments.getOrDefault(c, new ArrayList<>());
    }

    public int getEnrolledCount(Course c) {
        return enrollments.getOrDefault(c, new ArrayList<>()).size();
    }

    public Researcher getTopCitedResearcher() {
        Researcher top = null;
        int max = -1;
        for (User u : users) {
            if (u instanceof Researcher r) {
                int citations = r.getTotalCitations();
                if (citations > max) {
                    max = citations;
                    top = r;
                }
            }
        }
        return top;
    }

    public Researcher getTopCitedResearcherBySchool(String school) {
        Researcher top = null;
        int max = -1;
        for (User u : users) {
            if (!(u instanceof Researcher r)) continue;

            boolean inSchool = false;
            if (u instanceof Student && school.equals(((Student) u).getSchool())) {
                inSchool = true;
            }
            if (u instanceof Teacher && school.equals(((Teacher) u).getDepartment())) {
                inSchool = true;
            }
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

    public void printAllPapers(Comparator<ResearchPaper> comparator) { // Использование Strategy Pattern
        List<ResearchPaper> all = new ArrayList<>(allPapers);
        all.sort(comparator);
        System.out.println("\n--- All Research Papers (" + all.size() + ") ---");
        for (ResearchPaper p : all) {
            System.out.println("  " + p);
        }
        System.out.println("-------------------------------------\n");
    }

    public void announceTopCitedResearcher() {
        Researcher top = getTopCitedResearcher();
        if (top == null) return;
        if (top.getTotalCitations() == 0) return;

        String newsTitle = "Top Cited Researcher";
        String content = top.getName() + " is currently the top cited researcher " +
                         "with " + top.getTotalCitations() + " total citations.";

        User author = null;
        for (User u : users) {
            if (u instanceof Researcher && u.equals(top)) {
                author = u;
                break;
            }
        }

        news.removeIf(n -> newsTitle.equals(n.getTitle()));

        News announcement = new News(NewsTopic.RESEARCH, newsTitle, content, author);
        news.add(announcement);
        Collections.sort(news);

        System.out.println("[AUTO NEWS] Top cited researcher updated: " +
                           top.getName() + " (" + top.getTotalCitations() + " citations)");
    }

}
