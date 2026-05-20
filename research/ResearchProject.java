package research;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import exceptions.NonResearcherException;

public class ResearchProject implements Serializable {
    private static final long serialVersionUID = 1L;

    private String topic;
    private List<Researcher> participants;
    private List<ResearchPaper> publishedPapers;
    private Date startDate;

    public ResearchProject() {
        this.participants = new ArrayList<>();
        this.publishedPapers = new ArrayList<>();
    }

    public ResearchProject(String topic, Date startDate) {
        this.topic = topic;
        this.startDate = startDate;
        this.participants = new ArrayList<>();
        this.publishedPapers = new ArrayList<>();
    }

    public void addParticipant(Object person) throws NonResearcherException {
        if (!(person instanceof Researcher)) {
            throw new NonResearcherException(
                "Cannot join project '" + topic + "': only Researchers can join (got " +
                (person == null ? "null" : person.getClass().getSimpleName()) + ")");
        }
        Researcher r = (Researcher) person;
        if (!participants.contains(r)) {
            participants.add(r);
            System.out.println(r.getName() + " joined research project: " + topic);
        }
    }

    public void publishPaper(ResearchPaper p) {
        if (!publishedPapers.contains(p)) {
            publishedPapers.add(p);
        }
    }

    public String getTopic() { return topic; }
    public List<Researcher> getParticipants() { return participants; }
    public List<ResearchPaper> getPublishedPapers() { return publishedPapers; }
    public Date getStartDate() { return startDate; }

    public void setTopic(String topic) { this.topic = topic; }

    @Override
    public String toString() {
        return "ResearchProject{topic='" + topic + "', participants=" + participants.size() +
               ", papers=" + publishedPapers.size() + "}";
    }
}
