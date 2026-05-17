package utils;
import users.*;
import enums.StudentDegree;
import enums.TeacherPosition;
import enums.ManagerType;
public class UserFactory {

    public static User createUser(String role, String id, String name, String surname,
                                   String email, String password, Object... extraArgs) {
        switch (role.toUpperCase()) {
            case "STUDENT":
                String studentId = (String) extraArgs[0];
                String school = (String) extraArgs[1];
                int yearOfStudy = (int) extraArgs[2];
                return new Student(id, name, surname, email, password, studentId, school, yearOfStudy);

            case "GRADUATESTUDENT":
                String gsId = (String) extraArgs[0];
                String gsSchool = (String) extraArgs[1];
                int gsYear = (int) extraArgs[2];
                StudentDegree degree = (StudentDegree) extraArgs[3];
                return new GraduateStudent(id, name, surname, email, password, gsId, gsSchool, gsYear, degree);

            case "TEACHER":
                double tSalary = (double) extraArgs[0];
                String tDept = (String) extraArgs[1];
                TeacherPosition position = (TeacherPosition) extraArgs[2];
                return new Teacher(id, name, surname, email, password, tSalary, tDept, position);

            case "MANAGER":
                double mSalary = (double) extraArgs[0];
                String mDept = (String) extraArgs[1];
                ManagerType mType = (ManagerType) extraArgs[2];
                return new Manager(id, name, surname, email, password, mSalary, mDept, mType);

            case "ADMIN":
                double aSalary = (double) extraArgs[0];
                String aDept = (String) extraArgs[1];
                return new Admin(id, name, surname, email, password, aSalary, aDept);

            case "TECHSUPPORT":
                double tsSalary = (double) extraArgs[0];
                String tsDept = (String) extraArgs[1];
                return new TechSupportSpecialist(id, name, surname, email, password, tsSalary, tsDept);
            case "RESEARCHSTAFF":
                double rsSalary = (double) extraArgs[0];
                String rsDept = (String) extraArgs[1];
                String area = (String) extraArgs[2];
                String pos = (String) extraArgs[3];
                return new ResearchStaff(id, name, surname, email, password, rsSalary, rsDept, area, pos);

            default:
                throw new IllegalArgumentException("Unknown user role: " + role);
        }
    }
}
