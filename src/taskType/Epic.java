package taskType;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Epic extends Task {

    List<Integer> subtasksID;
    LocalDateTime endTime;

    public Epic(Integer id, String name, String description, Status status, List<Integer> subtasksID) {
        super(id, name, description, Status.NEW);
        this.subtasksID = subtasksID;
        this.endTime = null;
    }


    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getSubtasksID() {
        return subtasksID;
    }

    public void setSubtasksID(List<Integer> subtasksID) {
        this.subtasksID = subtasksID;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", data=" + (getStartTime()==null ? null : getStartTime().format(formatter)) + '\'' +
                ", duration=" + getDuration() +
                '}';

    }


}

