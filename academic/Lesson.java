package academic;
import java.io.Serializable;

import enums.DayOfWeek;
import enums.LessonType;
import users.Teacher;

public class Lesson implements Serializable {
    private static final long serialVersionUID = 1L;

    private LessonType type;
    private String room;
    private DayOfWeek dayOfWeek;
    private String startTime;
    private int duration;
    private Teacher instructor;


    public Lesson() {
    }

    public Lesson(LessonType type, String room, DayOfWeek dayOfWeek, String startTime, int duration, Teacher instructor) {
        this.type = type;
        this.room = room;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        if (duration <= 0) throw new IllegalArgumentException("Duration must be positive");
        this.duration = duration;
        this.instructor = instructor;
    }

    public LessonType getType() { return type; }
    public String getRoom() { return room; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public int getDuration() { return duration; }
    public Teacher getInstructor() { return instructor; }

    public void setType(LessonType type) { this.type = type; }
    public void setRoom(String room) { this.room = room; }
    public void setInstructor(Teacher instructor) { this.instructor = instructor; }

    @Override
    public String toString() {
        return (type != null ? type : "NO_TYPE") +
                " in " + (room != null ? room : "NO_ROOM") +
                " on " + (dayOfWeek != null ? dayOfWeek : "NO_DAY") +
                " at " + (startTime != null ? startTime : "NO_TIME") +
                " (" + duration + "min)";
    }
}
