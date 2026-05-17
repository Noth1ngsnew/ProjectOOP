package research;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import storage.JournalSubscriber;

public class Journal implements Serializable {
    private String name;
    private List<ResearchPaper> publishedPapers;
    private List<JournalSubscriber> subscribers;

    public Journal() {
        this.publishedPapers = new ArrayList<>();
        this.subscribers = new ArrayList<>();
    }

    public Journal(String name) {
        this.name = name;
        this.publishedPapers = new ArrayList<>();
        this.subscribers = new ArrayList<>();
    }

    public void publishPaper(ResearchPaper p) {
        if (!publishedPapers.contains(p)) {
            publishedPapers.add(p);
            notifySubscribers(p);
        }
    }

    public void subscribe(JournalSubscriber s) {
        if (!subscribers.contains(s)) subscribers.add(s);
    }

    public void unsubscribe(JournalSubscriber s) {
        subscribers.remove(s);
    }

    public void notifySubscribers(ResearchPaper p) {
        for (JournalSubscriber s : subscribers) {
            s.notifyNewPaper(this, p);
        }
    }

    public String getName() { return name; }
    public List<ResearchPaper> getPublishedPapers() { return publishedPapers; }
    public List<JournalSubscriber> getSubscribers() { return subscribers; }
    public int getSubscriberCount() { return subscribers.size(); }

    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Journal{name='" + name + "', papers=" + publishedPapers.size() +
               ", subscribers=" + subscribers.size() + "}";
    }
}
