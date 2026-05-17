package users;
import java.util.Date;

import storage.DataStorage;
import storage.LogEntry;
import communication.Message;
import communication.Request;

public abstract class Employee extends User {
    protected double salary;
    protected String department;
    protected Date hireDate;

    public Employee() {
    }

    public Employee(String id, String name, String surname, String email, String password,
                    double salary, String department) {
        super(id, name, surname, email, password);
        this.salary = salary;
        this.department = department;
        this.hireDate = new Date();
    }

    public Request sendRequest(String description) {
        Request r = new Request(this, description);
        DataStorage.getInstance().addRequest(r);
        DataStorage.getInstance().addLog(new LogEntry(this,
                "SENT REQUEST: " + description));
        System.out.println(getFullName() + " sent a request: " + description);
        return r;
    }

    public void sendMessage(User receiver, String subject, String content) {
        Message m = new Message(this, receiver, subject, content);
        DataStorage.getInstance().addMessage(m);
        System.out.println("Message sent from " + this.name + " to " + receiver.name);
    }

    public double getSalary() { return salary; }
    public String getDepartment() { return department; }
    public Date getHireDate() { return hireDate; }

    public void setSalary(double salary) { this.salary = salary; }
    public void setDepartment(String department) { this.department = department; }
}
