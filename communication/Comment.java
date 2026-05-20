package communication;
import java.io.Serializable;
import java.util.Date;

import users.User;

public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private User author;
    private String content;
    private Date date;

    public Comment() {
    }

    public Comment(User author, String content) {
        this.author = author;
        if (content == null || content.isBlank()) throw new IllegalArgumentException("Content cannot be empty");
        this.content = content;
        this.date = new Date();
    }

    public User getAuthor() { return author; }
    public String getContent() { return content; }
    public Date getDate() { return new Date(date.getTime()); }

    @Override
    public String toString() {
        return (author != null ? author.getFullName() : "unknown") + ": " + content;
    }
}
