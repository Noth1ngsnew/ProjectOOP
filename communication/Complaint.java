package communication;
import enums.UrgencyLevel;
import users.User;
import users.Student;

public class Complaint extends Message {
    private Student aboutStudent;
    private UrgencyLevel urgency;

    public Complaint() {
    }

    public Complaint(User sender, User receiver, String subject, String content,
                     Student aboutStudent, UrgencyLevel urgency) {
        super(sender, receiver, subject, content);
        this.aboutStudent = aboutStudent;
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
