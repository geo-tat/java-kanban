package Manager;


import TaskType.Epic;
import TaskType.Subtask;
import TaskType.Task;

import java.util.LinkedList;
import java.util.List;

public interface TaskManager {


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

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    boolean removeTaskById(int id);

    boolean removeSubtaskById(int id);

    boolean removeEpicForId(int id);


    List<Subtask> getSubtasksForEpic(int id);



}
