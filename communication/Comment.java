package communication;
import java.io.Serializable;
import java.util.Date;

import users.User;

public class Comment implements Serializable {
    private User author;
    private String content;
    private Date date;

    public Comment() {
    }

    public Comment(User author, String content) {
        this.author = author;
        this.content = content;
        this.date = new Date();
    }

    public User getAuthor() { return author; }
    public String getContent() { return content; }
    public Date getDate() { return date; }

    @Override
    public String toString() {
        return author.getFullName() + ": " + content;
    }
}
