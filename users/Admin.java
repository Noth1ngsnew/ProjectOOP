package users;
import java.util.List;

import storage.DataStorage;
import storage.LogEntry;

public class Admin extends Employee {

    public Admin() {
    }

    public Admin(String id, String name, String surname, String email, String password,
                 double salary, String department) {
        super(id, name, surname, email, password, salary, department);
    }

    public void addUser(User u) {
        DataStorage.getInstance().addUser(u);
        System.out.println(getFullName() + " added user: " + u.getFullName());
    }

    public void removeUser(User u) {
        DataStorage.getInstance().removeUser(u);
        System.out.println(getFullName() + " removed user: " + u.getFullName());
    }

    public void updateUser(User u) {
        System.out.println(getFullName() + " updated user: " + u.getFullName());
    }

    public List<LogEntry> viewLogs() {
        List<LogEntry> logs = DataStorage.getInstance().getLogs();
        System.out.println("System Logs");
        for (LogEntry e : logs) System.out.println("  " + e);
        return logs;
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    @Override
    public String toString() {
        return "Admin{name='" + getFullName() + "'}";
    }
}
