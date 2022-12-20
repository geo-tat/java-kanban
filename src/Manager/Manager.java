package Manager;


import TaskType.Epic;
import TaskType.Status;
import TaskType.Subtask;
import TaskType.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {

    private static int currentID;
    private final Map<Integer, Task> allTasks = new HashMap<>();
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();


    public List<Task> getTasks() {

        return List.copyOf(tasks.values());
    }

    public List<Epic> getEpics() {
        return List.copyOf(epics.values());
    }

    public List<Subtask> getSubtasks() {
        return List.copyOf(subtasks.values());
    }

    public void removeAllTasks() {
        allTasks.clear();
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
        }
    }

    public void removeAllEpics() {
        epics.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Task createTask(Task task) {
        currentID++;

        task.setId(currentID);
        allTasks.put(currentID, task);
        tasks.put(currentID, task);

        return task;
    }

    public Subtask createSubtask(Subtask subtask) {
        currentID++;
        subtask.setId(currentID);
        allTasks.put(currentID, subtask);
        subtasks.put(currentID, subtask);
        epics.get(subtask.getEpicID()).getSubtasksID().add(subtask.getId());
        updateEpicStatus(subtask.getEpicID());
        return subtask;
    }

    public Epic createEpic(Epic epic) {
        currentID++;
        epic.setId(currentID);
        allTasks.put(currentID, epic);
        epics.put(currentID, epic);


        return epic;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            allTasks.put(task.getId(), task);
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            allTasks.put(subtask.getId(), subtask);
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicID());
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic epicToUpdate = epics.get(epic.getId());
            epicToUpdate.setName(epic.getName());
            epicToUpdate.setDescription(epic.getDescription());


        }
    }

    public boolean removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return true;
        }
        return false;
    }

    public boolean removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            epics.get(subtask.getEpicID()).getSubtasksID().remove(subtask.getId());
            updateEpicStatus(subtask.getEpicID());
            return true;
        }
        return false;
    }

    public boolean removeEpicForId(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            for (Integer subtaskID : epic.getSubtasksID()) {
                subtasks.remove(subtaskID);
            }
            return true;
        }
        return false;
    }


    public List<Subtask> getSubtasksForEpic(int id) {
        List<Subtask> list = new ArrayList<>();
        for (Integer subtaskID : epics.get(id).getSubtasksID()) {
            Subtask subtask = subtasks.get(subtaskID);
            list.add(subtask);
        }
        return list;
    }

    private void updateEpicStatus(Integer epicID) {
        Epic epic = epics.get(epicID);
        if (epic == null) {
            return;
        }
        boolean isNEW = true;
        boolean isDONE = true;

        for (Integer subtaskID : epic.getSubtasksID()) {
            Subtask subtask = subtasks.get(subtaskID);
            isNEW = isNEW && Status.NEW == subtask.getStatus();
            isDONE = isDONE && Status.DONE.equals(subtask.getStatus());

            if (!isNEW && !isDONE) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }
        if (subtasks.isEmpty() || isNEW) {
            epic.setStatus(Status.NEW);
        } else if (isDONE) {
            epic.setStatus(Status.DONE);
        }
    }


}
