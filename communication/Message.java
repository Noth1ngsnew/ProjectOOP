package communication;
import java.io.Serializable;
import java.util.Date;

import users.User;

public class Message implements Serializable {
    protected User sender;
    protected User receiver;
    protected String subject;
    protected String content;
    protected Date sentDate;
    protected boolean isOfficial;

    public Message() {
    }

    public Message(User sender, User receiver, String subject, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.content = content;
        this.sentDate = new Date();
        this.isOfficial = false;
    }

    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public Date getSentDate() { return sentDate; }
    public boolean isOfficial() { return isOfficial; }

    public void setOfficial(boolean official) { this.isOfficial = official; }

    @Override
    public String toString() {
        return "Message[" + (sender != null ? sender.getFullName() : "?") +
               " -> " + (receiver != null ? receiver.getFullName() : "?") +
               "] " + subject;
    }
}
