package research;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import users.User;
import enums.NewsTopic;
import storage.DataStorage;
import communication.News;
import exceptions.NonResearcherException;

public class ResearcherDecorator implements Researcher, Serializable {
    private static final long serialVersionUID = 1L;

    private User wrappedUser;
    private List<ResearchPaper> papers;
    private List<ResearchProject> projects;

    public ResearcherDecorator(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Wrapped user cannot be null");
        }
        this.wrappedUser = user;
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    @Override
    public void publishPaper(ResearchPaper p) {
        if (p == null) throw new IllegalArgumentException("Paper cannot be null");
        if (!papers.contains(p)) {
            papers.add(p);
        }
        if (p.getJournal() != null) {
            p.getJournal().publishPaper(p);
        }
        News news = new News(NewsTopic.RESEARCH,
                "New paper by " + wrappedUser.getFullName(),
                wrappedUser.getFullName() + " published \"" + p.getTitle() + "\"",
                wrappedUser);
        DataStorage.getInstance().addNews(news);
        System.out.println(wrappedUser.getFullName() + " (as Researcher) published paper: " + p.getTitle());
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
        System.out.println("--- Papers of " + wrappedUser.getFullName() + " (decorated) ---");
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

    @Override
    public String getName() {
        return wrappedUser.getFullName();
    }

    @Override
    public String getFullName() {
        return wrappedUser.getFullName();
    }

    public User getWrappedUser() {
        return wrappedUser;
    }

    @Override
    public String toString() {
        return "Researcher[wraps " + wrappedUser.getRole() + ": " + wrappedUser.getFullName() +
                ", h-index=" + calculateHIndex() + "]";
    }
}