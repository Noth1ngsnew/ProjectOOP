package organization;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import users.Student;

public class StudentOrganization implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Student head;
    private List<Student> members;

    public StudentOrganization() {
        this.members = new ArrayList<>();
    }

    public StudentOrganization(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Organization name cannot be empty");
        this.name = name;
        this.members = new ArrayList<>();
    }

    public void addMember(Student s) {
        if (s == null) return;
        if (!members.contains(s)) members.add(s);
    }

    public void removeMember(Student s) {
        if (s == null) return;
        members.remove(s);
        if (s.equals(head)) head = null;
    }

    public void setHead(Student s) {
        if (!members.contains(s)) addMember(s);
        this.head = s;
    }

    public String getName() { return name; }
    public Student getHead() { return head; }
    public List<Student> getMembers() { return Collections.unmodifiableList(members); }

    @Override
    public String toString() {
        return "Organization{" + name + ", head=" +
               (head != null ? head.getFullName() : "none") +
               ", members=" + members.size() + "}";
    }
}
