package Manager;


import TaskType.Epic;
import TaskType.Subtask;
import TaskType.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {

    private static int currentID;
    private Map<Integer, Task> allTasks = new HashMap<>();
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();


    public void getTasks() {

        for (Integer id : tasks.keySet()) {
            System.out.println(tasks.get(id).toString());
        }
    }

    public void getEpics() {
        for (Integer id : epics.keySet()) {
            System.out.println(epics.get(id).toString());
        }
    }

    public void getSubtasks() {
        for (Integer id : subtasks.keySet()) {
            System.out.println(subtasks.get(id).toString());
        }
    }

    public void removeAllTasks() {
        allTasks.clear();
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
    }

    public Task getTaskById(int id) {
        return allTasks.get(id);
    }

    public Task createTask(Task task) {
        currentID++;
        Task newTask = new Task(currentID, task.getName(), task.getDescription(), task.getStatus());
        allTasks.put(newTask.getId(), newTask);

        tasks.put(currentID, newTask);
        return newTask;
    }

    public Subtask createSubtask(Subtask subtask) {
        currentID++;
        Subtask newSubtask = new Subtask(currentID, subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpic());
        allTasks.put(currentID, newSubtask);
        tasks.put(currentID, newSubtask);

        newSubtask.getEpic().updateStatus();
        return newSubtask;
    }

    public Epic createEpic(Epic epic) {
        currentID++;
        Epic newEpic = new Epic(currentID, epic.getName(), epic.getDescription(), epic.getSubtasks());
        allTasks.put(currentID, newEpic);
        tasks.put(currentID, newEpic);

        newEpic.updateStatus();
        return newEpic;
    }

    public void updateTask(Task task) {
        if (allTasks.containsKey(task.getId())) {
            allTasks.put(task.getId(), task);

            if (task instanceof Subtask) {
                subtasks.put(task.getId(), (Subtask) task);
            }
            if (task instanceof Epic) {
                epics.put(task.getId(), (Epic) task);
            }
        }
    }

    public boolean removeTaskById(int id) {
        if (allTasks.containsKey(id)) {
            Task taskToRemove = allTasks.remove(id);

            if (taskToRemove instanceof Subtask) {
                Subtask subtask = subtasks.remove(taskToRemove.getId());

                subtask.getEpic().updateStatus();
            } else if (taskToRemove instanceof Epic) {
                epics.remove(taskToRemove.getId());
            } else {
                tasks.remove(taskToRemove.getId());
            }
            return true;
        }
        return false;
    }

    public List<Subtask> getSubtasksForEpic(int id) {
        return epics.get(id).getSubtasks();
    }

    private void updateEpic(Epic epicToUpdate, Epic epic) {
        epicToUpdate.setSubtasks(epic.getSubtasks());
        epicToUpdate.updateStatus();
    }

    private void updateSubtask(Subtask subtaskToUpdate, Subtask subtask) {
        subtaskToUpdate.setEpic(subtask.getEpic());

        Epic epic = subtaskToUpdate.getEpic();
        epic.updateStatus();
    }
}
