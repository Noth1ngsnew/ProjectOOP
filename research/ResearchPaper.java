package research;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import enums.Format;

public class ResearchPaper implements Comparable<ResearchPaper>, Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private List<Researcher> authors;
    private Journal journal;
    private int pages;
    private Date publicationDate;
    private String doi;
    private int citations;

    public ResearchPaper() {
        this.authors = new ArrayList<>();
    } // Дурыстау


    public ResearchPaper(String title, List<Researcher> authors, Journal journal,
                         int pages, Date publicationDate, String doi, int citations) {
        if (publicationDate == null) throw new IllegalStateException("Publication date is not set");
        this.title = title;
        this.authors = authors != null ? authors : new ArrayList<>();
        this.journal = journal;
        this.pages = pages;
        this.publicationDate = publicationDate;
        this.doi = doi;
        this.citations = citations;
    }

    public String getCitation(Format f) {
        StringBuilder authorList = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            authorList.append(authors.get(i).getFullName());
            if (i < authors.size() - 1) authorList.append(", ");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(publicationDate);
        int year = cal.get(Calendar.YEAR);
        String journalName = journal != null ? journal.getName() : "Unknown Journal";

        if (f == Format.BIBTEX) {
            String key = "paper" + (doi != null ? doi.replaceAll("[^a-zA-Z0-9]", "") : "");
            return "@article{" + key + ",\n" +
                   "  title = {" + title + "},\n" +
                   "  author = {" + authorList + "},\n" +
                   "  journal = {" + journalName + "},\n" +
                   "  year = {" + year + "},\n" +
                   "  pages = {" + pages + "},\n" +
                   "  doi = {" + doi + "}\n" +
                   "}";
        }
        return authorList + " (" + year + "). " + title + ". " + journalName + ", " + pages + " pages. doi:" + doi;
    }


    public void incrementCitations() { citations++; }

    @Override
    public int compareTo(ResearchPaper other) {
        if (publicationDate == null || other.publicationDate == null) return 0;
        return other.publicationDate.compareTo(this.publicationDate);
    }

    public String getTitle() { return title; }
    public List<Researcher> getAuthors() { return authors; }
    public Journal getJournal() { return journal; }
    public int getPages() { return pages; }
    public Date getPublicationDate() { return publicationDate; }
    public String getDoi() { return doi; }
    public int getCitations() { return citations; }

    public void setTitle(String title) { this.title = title; }
    public void setJournal(Journal journal) { this.journal = journal; }
    public void setCitations(int citations) {
        if (citations < 0) throw new IllegalArgumentException("Citations cannot be negative");
        this.citations = citations;
    }

    public void addAuthor(Researcher r) {
        if (!authors.contains(r)) authors.add(r);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchPaper r)) return false;
        return Objects.equals(doi, r.doi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doi);
    }

    @Override
    public String toString() {
        return "\"" + title + "\" (" + citations + " citations, " + pages + " pages)";
    }
}
