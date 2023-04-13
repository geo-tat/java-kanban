package taskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;

    private Duration duration;
    private LocalDateTime startTime;
private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd Время HH-mm");

    public Task(Integer id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = null;
        this.startTime = null;

    }


    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }



    public LocalDateTime getEndTime() {
        if (startTime != null) {
            if (duration != null) {
                return startTime.plus(duration);
            } else {
                return startTime;
            }
        } else {
            return null;
        }
    }
    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status &&
                startTime==task.startTime && duration==task.duration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status,startTime,duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", data=" + (startTime==null ? null : startTime.format(formatter))  + '\'' +
                ", duration=" + getDuration() +
                '}';
    }
}
