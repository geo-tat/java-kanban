package TaskType;


import java.util.List;

public class Epic extends Task {
    List<Integer> subtasksID;

    public Epic(Integer id, String name, String description, Status status, List<Integer> subtasksID) {
        super(id, name, description, Status.NEW);
        this.subtasksID = subtasksID;
    }


    public List<Integer> getSubtasksID() {
        return subtasksID;
    }

    public void setSubtasksID(List subtasksID) {
        this.subtasksID = subtasksID;
    }



    @Override
    public String toString() {
        return getId() + "," + Type.EPIC + "," + getName() + "," + getStatus() + "," + getDescription();

    }
}

// old toString
/*
public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
 */