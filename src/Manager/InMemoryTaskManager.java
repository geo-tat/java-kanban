package Manager;

import TaskType.Epic;
import TaskType.Status;
import TaskType.Subtask;
import TaskType.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private static int currentID;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    HistoryManager historyManager = new InMemoryHistoryManager();

    @Override
    public List<Task> getHistory() {

        return historyManager.getHistory();
    }


    @Override
    public List<Task> getTasks() {

        return List.copyOf(tasks.values());

    }

    @Override
    public List<Epic> getEpics() {
        return List.copyOf(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return List.copyOf(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));

        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));

        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));

        return epics.get(id);
    }

    @Override
    public Task createTask(Task task) {
        currentID++;

        task.setId(currentID);
        tasks.put(currentID, task);

        return task;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        currentID++;
        subtask.setId(currentID);
        subtasks.put(currentID, subtask);
        epics.get(subtask.getEpicID()).getSubtasksID().add(subtask.getId());
        updateEpicStatus(subtask.getEpicID());
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        currentID++;
        epic.setId(currentID);
        epics.put(currentID, epic);


        return epic;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicID());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic epicToUpdate = epics.get(epic.getId());
            epicToUpdate.setName(epic.getName());
            epicToUpdate.setDescription(epic.getDescription());


        }
    }

    @Override
    public boolean removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.remove(id);
            epics.get(subtask.getEpicID()).getSubtasksID().remove(subtask.getId());
            updateEpicStatus(subtask.getEpicID());
            return true;
        }
        return false;
    }

    @Override
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

    @Override
    public List<Subtask> getSubtasksForEpic(int id) {
        List<Subtask> list = new ArrayList<>();
        for (Integer subtaskID : epics.get(id).getSubtasksID()) {
            Subtask subtask = subtasks.get(subtaskID);
            list.add(subtask);
        }
        return list;
    }

    @Override
    public void updateEpicStatus(Integer epicID) {
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