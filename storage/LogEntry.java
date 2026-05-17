package storage;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import users.User;

public class LogEntry implements Serializable {
    private User user;
    private String action;
    private Date timestamp;

    public LogEntry() {
    }

    public LogEntry(User user, String action) {
        this.user = user;
        this.action = action;
        this.timestamp = new Date();
    }

    public User getUser() { return user; }
    public String getAction() { return action; }
    public Date getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "[" + sdf.format(timestamp) + "] " +
               (user != null ? user.getFullName() : "system") + ": " + action;
    }
}
