package TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    List<Subtask> subtasks = new ArrayList<>();

    public Epic(int id, String name, String description, List<Subtask> subtasks) {
        super(id, name, description, Status.NEW);
        this.subtasks = subtasks;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public void setStatus(Status status) {

    }

    public void updateStatus() {
        boolean isNew = true;
        for (Subtask subtask : subtasks) {
            isNew = isNew && Status.NEW.equals(subtask.getStatus());
            if (!isNew) {
                break;
            }
        }
        if (subtasks.isEmpty() || isNew) {
            this.setStatus(Status.NEW);
        }

        boolean isDone = true;
        for (Subtask subtask : subtasks) {
            isDone = isDone && Status.DONE.equals(subtask.getStatus());

            if (!isDone) {
                break;
            }
        }

        if (isDone) {
            this.setStatus(Status.DONE);
        }
        this.setStatus(Status.IN_PROGRESS);
    }
}
