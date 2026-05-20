package users;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import storage.DataStorage;
import storage.JournalSubscriber;
import storage.LogEntry;
import enums.Language;
import research.Journal;
import research.ResearchPaper;
import communication.News;
import communication.Comment;

public abstract class User implements Serializable, JournalSubscriber {
    protected String id;
    protected String name;
    protected String surname;
    protected String email;
    protected String password;
    protected Language language;
    protected Date createdAt;

    public User() {
    }

    public User(String id, String name, String surname, String email, String password) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.language = Language.EN;
        this.createdAt = new Date();
    }


    public boolean login(String email, String password) {
        if (this.email.equals(email) && this.password.equals(password)) {
            DataStorage.getInstance().addLog(new LogEntry(this, "LOGIN"));
            System.out.println(name + " " + surname + " logged in.");
            return true;
        }
        System.out.println("Login failed for " + email);
        return false;
    }

    public void logout() {
        DataStorage.getInstance().addLog(new LogEntry(this, "LOGOUT"));
        System.out.println(name + " " + surname + " logged out.");
    }

    public List<News> viewNews() {
        return DataStorage.getInstance().getNews();
    }

    public void subscribeToJournal(Journal j) {
        j.subscribe(this);
        System.out.println(name + " subscribed to " + j.getName());
    }

    public void unsubscribeFromJournal(Journal j) {
        j.unsubscribe(this);
    }

    public void changeLanguage(Language l) {
        this.language = l;
        System.out.println("Language changed to " + l.getDisplayName());
    }

    public void commentOnNews(News n, String content) {
        Comment c = new Comment(this, content);
        n.addComment(c);
    }

    public void notifyNewPaper(Journal journal, ResearchPaper paper) { // Реализовывает интерфейс JournalSubscriber
        System.out.println("[" + name + "] New paper in " + journal.getName() + ": \"" + paper.getTitle() + "\"");
    }

    public abstract String getRole();

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getFullName() { return name + " " + surname; }
    public String getEmail() { return email; }
    public Language getLanguage() { return language; }
    public Date getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getRole() + "{id='" + id + "', name='" + name + " " + surname + "'}";
    }
}
