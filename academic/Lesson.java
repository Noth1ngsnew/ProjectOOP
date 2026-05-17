package academic;
import java.io.Serializable;

import enums.LessonType;
import users.Teacher;

public class Lesson implements Serializable {
    private LessonType type;
    private String room;
    private String dayOfWeek;
    private String startTime;
    private int duration;
    private Teacher instructor;

    public Lesson() {
    }

    public Lesson(LessonType type, String room, String dayOfWeek, String startTime, int duration, Teacher instructor) {
        this.type = type;
        this.room = room;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.duration = duration;
        this.instructor = instructor;
    }

    public LessonType getType() { return type; }
    public String getRoom() { return room; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public int getDuration() { return duration; }
    public Teacher getInstructor() { return instructor; }

    public void setType(LessonType type) { this.type = type; }
    public void setRoom(String room) { this.room = room; }
    public void setInstructor(Teacher instructor) { this.instructor = instructor; }

    @Override
    public String toString() {
        return type + " in " + room + " on " + dayOfWeek + " at " + startTime + " (" + duration + "min)";
    }
}
