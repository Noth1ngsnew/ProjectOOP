package utils;
import java.util.Comparator;

import research.ResearchPaper;

public class ResearchPaperComparators {

    public static final Comparator<ResearchPaper> BY_DATE = new Comparator<ResearchPaper>() {
        public int compare(ResearchPaper a, ResearchPaper b) {
            if (a.getPublicationDate() == null || b.getPublicationDate() == null) return 0;
            return b.getPublicationDate().compareTo(a.getPublicationDate());
        }
    };

    public static final Comparator<ResearchPaper> BY_CITATIONS = new Comparator<ResearchPaper>() {
        public int compare(ResearchPaper a, ResearchPaper b) {
            return Integer.compare(b.getCitations(), a.getCitations());
        }
    };

    public static final Comparator<ResearchPaper> BY_LENGTH = new Comparator<ResearchPaper>() {
        public int compare(ResearchPaper a, ResearchPaper b) {
            return Integer.compare(b.getPages(), a.getPages());
        }
    };
}
