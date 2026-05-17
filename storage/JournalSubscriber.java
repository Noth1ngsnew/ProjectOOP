package storage;
import java.io.Serializable;

import research.Journal;
import research.ResearchPaper;

public interface JournalSubscriber extends Serializable {
    void notifyNewPaper(Journal journal, ResearchPaper paper);
}
