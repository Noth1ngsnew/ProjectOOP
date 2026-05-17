package research;
import java.util.Comparator;
import java.util.List;

import exceptions.NonResearcherException;

public interface Researcher {
    void publishPaper(ResearchPaper p);
    int calculateHIndex();
    void joinProject(ResearchProject p) throws NonResearcherException;
    void printPapers(Comparator<ResearchPaper> c);
    List<ResearchPaper> getPapers();
    List<ResearchProject> getProjects();
    int getTotalCitations();
    String getName();
    String getFullName();
}
