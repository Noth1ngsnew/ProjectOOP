package organization;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import users.Student;

public class StudentOrganization implements Serializable {
    private String name;
    private Student head;
    private List<Student> members;

    public StudentOrganization() {
        this.members = new ArrayList<>();
    }

    public StudentOrganization(String name) {
        this.name = name;
        this.members = new ArrayList<>();
    }

    public void addMember(Student s) {
        if (!members.contains(s)) members.add(s);
    }

    public void removeMember(Student s) {
        members.remove(s);
        if (s.equals(head)) head = null;
    }

    public void setHead(Student s) {
        if (!members.contains(s)) addMember(s);
        this.head = s;
    }

    public String getName() { return name; }
    public Student getHead() { return head; }
    public List<Student> getMembers() { return members; }

    @Override
    public String toString() {
        return "Organization{" + name + ", head=" +
               (head != null ? head.getFullName() : "none") +
               ", members=" + members.size() + "}";
    }
}
