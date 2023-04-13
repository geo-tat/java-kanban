package manager;


import taskType.Epic;
import taskType.Subtask;
import taskType.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getPrioritizedTasks();
    boolean isValid(Task task);
    List<Task> getHistory();

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    Task createTask(Task task);

    Subtask createSubtask(Subtask subtask);

    Epic createEpic(Epic epic);

    boolean updateTask(Task task);

    boolean updateSubtask(Subtask subtask);

    boolean updateEpic(Epic epic);

    boolean removeTaskById(int id);

    boolean removeSubtaskById(int id);

    boolean removeEpicForId(int id);


    List<Subtask> getSubtasksForEpic(int id);



}
