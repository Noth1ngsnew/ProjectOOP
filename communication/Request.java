package communication;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import users.User;
import users.TechSupportSpecialist;
import enums.RequestStatus;

public class Request implements Serializable { //extends Message
    private String id;
    private User sender;
    private String description;
    private RequestStatus status;
    private Date createdDate;
    private TechSupportSpecialist assignedTo;

    public Request() {
    }

    public Request(User sender, String description) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.sender = sender;
        this.description = description;
        this.status = RequestStatus.NEW;
        this.createdDate = new Date();
    }

    public void updateStatus(RequestStatus s) {
        this.status = s;
    }

    public void assignTo(TechSupportSpecialist t) {
        this.assignedTo = t;
    }

    public String getId() { return id; }
    public User getSender() { return sender; }
    public String getDescription() { return description; }
    public RequestStatus getStatus() { return status; }
    public Date getCreatedDate() { return createdDate; }
    public TechSupportSpecialist getAssignedTo() { return assignedTo; }

    @Override
    public String toString() {
        return "Request[" + id + ", " + status + "] " + description +
               " (from " + (sender != null ? sender.getFullName() : "?") + ")";
    }
}
