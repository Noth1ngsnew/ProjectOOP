package storage;
import java.io.Serializable;

import research.Journal;
import research.ResearchPaper;

public interface JournalSubscriber extends Serializable { // Observer Interface, можно сказать наш ютуб канал
    void notifyNewPaper(Journal journal, ResearchPaper paper);
}
