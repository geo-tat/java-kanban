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
        return allTasks.get(id);
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
            updateEpicStatus(epicToUpdate.getId());

        }
    }

    public boolean removeTaskById(int id) {
        if (allTasks.containsKey(id)) {
            Task taskToRemove = allTasks.remove(id);

            if (taskToRemove instanceof Subtask) {
                Subtask subtask = subtasks.remove(taskToRemove.getId());
                epics.get(subtask.getEpicID()).getSubtasksID().remove(subtask.getId());
                updateEpicStatus(subtask.getEpicID());
            } else if (taskToRemove instanceof Epic) {
                Epic epic = epics.remove(taskToRemove.getId());
                for (Integer subtaskID : epic.getSubtasksID()) {
                    subtasks.remove(subtaskID);
                }
            } else {
                tasks.remove(taskToRemove.getId());
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
        if(epic == null) {
            return;
        }
        boolean isNEW = true;
        boolean isDONE = true;

        for (Integer subtaskID : epic.getSubtasksID()) {
            Subtask subtask = subtasks.get(subtaskID);
            isNEW = isNEW && Status.NEW.equals((subtask.getStatus()));
            isDONE = isDONE && Status.DONE.equals(subtask.getStatus());

            if (!isNEW && !isDONE) {
                epic.setStatus(Status.IN_PROGRESS);
                break;
            }
        }
        if (subtasks.isEmpty() || isNEW) {
            epic.setStatus(Status.NEW);
        } else if (isDONE) {
            epic.setStatus(Status.DONE);
        }
    }


}
