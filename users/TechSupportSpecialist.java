package users;
import java.util.ArrayList;
import java.util.List;

import storage.DataStorage;
import enums.RequestStatus;
import communication.Request;

public class TechSupportSpecialist extends Employee {
    private List<Request> assignedRequests;

    public TechSupportSpecialist() {
        this.assignedRequests = new ArrayList<>();
    }

    public TechSupportSpecialist(String id, String name, String surname, String email, String password,
                                  double salary, String department) {
        super(id, name, surname, email, password, salary, department);
        this.assignedRequests = new ArrayList<>();
    }

    public List<Request> viewRequests() {
        List<Request> news = new ArrayList<>();
        for (Request r : DataStorage.getInstance().getRequests()) {
            if (r.getStatus() == RequestStatus.NEW) {
                r.updateStatus(RequestStatus.VIEWED);
                r.assignTo(this);
                assignedRequests.add(r);
                news.add(r);
            }
        }
        System.out.println(getFullName() + " viewed " + news.size() + " new requests.");
        return news;
    }

    public void acceptRequest(Request r) {
        r.updateStatus(RequestStatus.ACCEPTED);
        System.out.println(getFullName() + " accepted: " + r.getDescription());
    }

    public void rejectRequest(Request r) {
        r.updateStatus(RequestStatus.REJECTED);
        System.out.println(getFullName() + " rejected: " + r.getDescription());
    }

    public void markDone(Request r) {
        r.updateStatus(RequestStatus.DONE);
        System.out.println(getFullName() + " completed: " + r.getDescription());
    }

    @Override
    public String getRole() {
        return "TechSupportSpecialist";
    }

    public List<Request> getAssignedRequests() { return assignedRequests; }

    @Override
    public String toString() {
        return "TechSupport{name='" + getFullName() + "'}";
    }
}
