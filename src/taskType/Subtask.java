package taskType;

import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private Integer epicID;

    public Subtask(Integer id, String name, String description, Status status, Integer epicID) {

        super(id, name, description, status);
        this.epicID = epicID;
    }


    public Integer getEpicID() {
        return epicID;
    }

    public void setEpicID(Integer epicID) {
        this.epicID = epicID;
    }
    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", data=" + (getStartTime()==null ? null : getStartTime().format(formatter)) + '\'' +
                ", duration=" + getDuration() +
                '}';

    }


}

/*
public Subtask(Integer id, String name, String description, Status status, LocalDateTime startTime,
                   Duration duration, Integer epicID) {
        super(id, name, description, status,startTime,duration);
        this.epicID = epicID;
    }
 */
