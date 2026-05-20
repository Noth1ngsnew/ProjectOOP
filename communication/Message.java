package communication;
import java.io.Serializable;
import java.util.Date;

import users.User;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private User sender;
    private User receiver;
    private String subject;
    private String content;
    private Date sentDate;
    private boolean official;

    public Message() {
    }

    public Message(User sender, User receiver, String subject, String content) {
        if (subject == null || subject.isBlank()) throw new IllegalArgumentException("Subject cannot be empty");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("Content cannot be empty");
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.content = content;
        this.sentDate = new Date();
        this.official = false;
    }

    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public Date getSentDate() { return new Date(sentDate.getTime()); }
    public boolean isOfficial() { return official; }
    public void setOfficial(boolean official) { this.official = official; }

    @Override
    public String toString() {
        return "Message[" + (sender != null ? sender.getFullName() : "?") +
               " -> " + (receiver != null ? receiver.getFullName() : "?") +
               "] " + subject;
    }
}
