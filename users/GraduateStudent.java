package users;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import storage.DataStorage;
import enums.StudentDegree;
import enums.NewsTopic;
import research.Researcher;
import research.ResearchPaper;
import research.ResearchProject;
import communication.News;
import exceptions.LowHIndexException;
import exceptions.NonResearcherException;

public class GraduateStudent extends Student implements Researcher {
    private StudentDegree degree;
    private Researcher supervisor;
    private List<ResearchPaper> diplomaProject;
    private List<ResearchPaper> papers;
    private List<ResearchProject> projects;

    public GraduateStudent() {
        super();
        this.diplomaProject = new ArrayList<>();
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public GraduateStudent(String id, String name, String surname, String email, String password,
                           String studentId, String school, int yearOfStudy, StudentDegree degree) {
        super(id, name, surname, email, password, studentId, school, yearOfStudy);
        this.degree = degree;
        this.diplomaProject = new ArrayList<>();
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    public void setSupervisor(Researcher r) throws LowHIndexException {
        if (r == null) {
            throw new IllegalArgumentException("Supervisor cannot be null");
        }
        int hindex = r.calculateHIndex();
        if (hindex < 3) {
            throw new LowHIndexException(
                "Cannot assign " + r.getName() + " as supervisor: h-index is " + hindex + " (minimum 3)");
        }
        this.supervisor = r;
        System.out.println(getFullName() + " assigned " + r.getName() + " as supervisor (h-index=" + hindex + ")");
    }

    public void addDiplomaPaper(ResearchPaper p) {
        diplomaProject.add(p);
        if (!papers.contains(p)) {
            papers.add(p);
        }
    }

    @Override
    public String getRole() {
        return "GraduateStudent";
    }

    @Override
    public void publishPaper(ResearchPaper p) {
        if (!papers.contains(p)) {
            papers.add(p);
        }
        if (p.getJournal() != null) {
            p.getJournal().publishPaper(p);
        }
        News news = new News(NewsTopic.RESEARCH,
            "New paper: " + p.getTitle(),
            getFullName() + " published \"" + p.getTitle() + "\"",
            this);
        DataStorage.getInstance().addNews(news);
        System.out.println(getFullName() + " published paper: " + p.getTitle());
    }

    @Override
    public int calculateHIndex() {
        if (papers.isEmpty()) return 0;
        List<Integer> citations = new ArrayList<>();
        for (ResearchPaper p : papers) citations.add(p.getCitations());
        citations.sort(Comparator.reverseOrder());
        int h = 0;
        for (int i = 0; i < citations.size(); i++) {
            if (citations.get(i) >= i + 1) h = i + 1;
            else break;
        }
        return h;
    }

    @Override
    public void joinProject(ResearchProject p) throws NonResearcherException {
        p.addParticipant(this);
        if (!projects.contains(p)) projects.add(p);
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> c) {
        List<ResearchPaper> sorted = new ArrayList<>(papers);
        sorted.sort(c);
        System.out.println("Papers of " + getFullName() + " ");
        for (ResearchPaper p : sorted) System.out.println("  " + p);
    }

    @Override
    public List<ResearchPaper> getPapers() { return papers; }

    @Override
    public List<ResearchProject> getProjects() { return projects; }

    @Override
    public int getTotalCitations() {
        int total = 0;
        for (ResearchPaper p : papers) total += p.getCitations();
        return total;
    }

    public StudentDegree getDegree() { return degree; }
    public Researcher getSupervisor() { return supervisor; }
    public List<ResearchPaper> getDiplomaProject() { return diplomaProject; }

    public void setDegree(StudentDegree degree) { this.degree = degree; }

    @Override
    public String toString() {
        return "GraduateStudent{name='" + getFullName() + "', degree=" + degree +
               ", supervisor=" + (supervisor != null ? supervisor.getName() : "none") +
               ", h-index=" + calculateHIndex() + "}";
    }
}
