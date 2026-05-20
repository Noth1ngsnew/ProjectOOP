package communication;
import enums.UrgencyLevel;
import users.User;
import users.Student;

public class Complaint extends Message {
    private static final long serialVersionUID = 1L;
    private Student aboutStudent;
    private UrgencyLevel urgency;

    public Complaint() {
    }

    public Complaint(User sender, User receiver, String subject, String content,
                     Student aboutStudent, UrgencyLevel urgency) {
        super(sender, receiver, subject, content);
        this.aboutStudent = aboutStudent;
        if (urgency == null) throw new IllegalArgumentException("Urgency level cannot be null");
        this.urgency = urgency;
        setOfficial(true);
    }

    public UrgencyLevel getUrgency() { return urgency; }
    public Student getAboutStudent() { return aboutStudent; }

    @Override
    public String toString() {
        return "Complaint[urgency=" + urgency + ", about=" +
               (aboutStudent != null ? aboutStudent.getFullName() : "?") + "] " + getContent();
    }
}
