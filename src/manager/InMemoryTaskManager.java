package manager;

import exceptions.TaskIntersectionException;
import taskType.Epic;
import taskType.Status;
import taskType.Subtask;
import taskType.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected int currentID;

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected  TreeSet<Task> prioritizedTask = new TreeSet<>(new Comparator<Task>() {
        @Override
        public int compare(Task t1, Task t2) {
            if (t1.getStartTime() == null && t2.getStartTime() != null) {
                return 1;
            }
            if (t2.getStartTime() == null && t1.getStartTime() != null) {
                return -1;
            }
            if (t1.getStartTime() == null && t2.getStartTime() == null) {
                return 1;
            }
            if (t1.getStartTime().isBefore(t2.getStartTime())) {
                return -1;
            }
            if (t1.getStartTime().isAfter(t2.getStartTime())) {
                return 1;
            }
            return 0;
        }
    });


    HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTask);
    }

    @Override
    public boolean isValid(Task task) {
        LocalDateTime taskStart = task.getStartTime();
        LocalDateTime taskEnd = task.getEndTime();
        List<Task> taskList = new ArrayList<>();
        for (Task t : getPrioritizedTasks()) {                      // Убираю из списка задачи без заданного времени
            if (t.getStartTime() != null) {
                taskList.add(t);
            }
        }
        boolean result = true;
        if (taskStart != null) {
            for (Task task1 : taskList) {
                if (task1.getStartTime().isBefore(taskStart) && task1.getEndTime().isBefore(taskStart) ||
                        task1.getStartTime().isAfter(taskEnd) && task1.getEndTime().isAfter(taskEnd)) {
                    if (task1.getStartTime().equals(taskStart) || task1.getStartTime().equals(taskEnd) ||
                            task1.getEndTime().equals(taskStart) || task1.getEndTime().equals(taskEnd)) {
                        result = false;
                    } else {
                        result = true;
                    }
                } else {
                    result = false;
                }

            }
        }
        return result;
    }

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
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();

    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            updateEpicStatus(epic.getId());
            timeCalculationForEpic(epic.getId());
        }
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        epics.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            return null;
        }
        historyManager.add(tasks.get(id));

        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            return null;
        }
        historyManager.add(subtasks.get(id));

        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (!epics.containsKey(id)) {
            return null;
        }
        historyManager.add(epics.get(id));

        return epics.get(id);
    }

    @Override
    public Task createTask(Task task) {
        if (task.getId() == 0) {
            currentID++;
            task.setId(currentID);
            tasks.put(currentID, task);
        } else {
            tasks.put(task.getId(), task);
        }
        if (isValid(task)) {
            prioritizedTask.add(task);
        } else {
            throw new TaskIntersectionException("Задачи пересекаются во времени!");
        }
        return task;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask.getId() == 0) {
            currentID++;
            subtask.setId(currentID);
            subtasks.put(currentID, subtask);
            epics.get(subtask.getEpicID()).getSubtasksID().add(subtask.getId());

            updateEpicStatus(subtask.getEpicID());
            timeCalculationForEpic(subtask.getEpicID());
        } else {
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicID()).getSubtasksID().add(subtask.getId());
            updateEpicStatus(subtask.getEpicID());
            timeCalculationForEpic(subtask.getEpicID());
        }
        if (isValid(subtask)) {
            prioritizedTask.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic.getId() == 0) {
            currentID++;
            epic.setId(currentID);
            epics.put(currentID, epic);
        } else {
            epics.put(epic.getId(), epic);
        }


        return epic;
    }

    @Override
    public boolean updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            if (isValid(task)) {
                prioritizedTask.add(task);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicID());
            timeCalculationForEpic(subtask.getEpicID());
            if (isValid(subtask)) {
                prioritizedTask.add(subtask);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic epicToUpdate = epics.get(epic.getId());
            epicToUpdate.setName(epic.getName());
            epicToUpdate.setDescription(epic.getDescription());
            return true;

        } else {
            return false;
        }
    }

    @Override
    public boolean removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            prioritizedTask.remove(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);

            return true;
        }
        return false;
    }

    @Override
    public boolean removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            prioritizedTask.remove(subtasks.get(id));
            Subtask subtask = subtasks.remove(id);
            epics.get(subtask.getEpicID()).getSubtasksID().remove((Integer) subtask.getId());
            updateEpicStatus(subtask.getEpicID());
            timeCalculationForEpic(subtask.getEpicID());

            return true;
        }
        historyManager.remove(id);
        return false;
    }

    @Override
    public boolean removeEpicForId(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            for (Integer subtaskID : epic.getSubtasksID()) {
                subtasks.remove(subtaskID);
                historyManager.remove(subtaskID);
            }
            historyManager.remove(id);
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


    protected void updateEpicStatus(Integer epicID) {
        Epic epic = epics.get(epicID);
        if (epic == null) {
            return;
        }
        boolean isNEW = true;
        boolean isDONE = true;

        for (Integer subtaskID : epic.getSubtasksID()) {
            Subtask subtask = subtasks.get(subtaskID);
            if (subtask == null) {
                epic.setStatus(Status.NEW);

            } else {
                isNEW = isNEW && Status.NEW == subtask.getStatus();
                isDONE = isDONE && Status.DONE.equals(subtask.getStatus());

                if (!isNEW && !isDONE) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
        }
        if (subtasks.isEmpty() || isNEW) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.DONE);
        }
    }

    protected void timeCalculationForEpic(Integer epicID) {
        //      Map<Integer, Epic> epicMap = storage.getEpics();
        Epic epic = epics.get(epicID);
        List<Integer> epicSubs = epic.getSubtasksID();
        if (!epicSubs.isEmpty()) {
            LocalDateTime startEpic = null;
            LocalDateTime endEpic = null;
            for (Integer id : epicSubs) {
                Subtask sub = subtasks.get(id);
                if (sub == null) {
                    break;
                }
                if (sub.getStartTime() != null) {
                    startEpic = sub.getStartTime();
                    endEpic = sub.getEndTime();
                    break;
                }
            }
            if (startEpic != null) {
                for (Integer id : epicSubs) {
                    Subtask sub = subtasks.get(id);
                    if (sub.getStartTime() != null) {
                        LocalDateTime startSubtask = sub.getStartTime();
                        LocalDateTime endSubtask = sub.getEndTime();
                        if (startSubtask.isBefore(startEpic)) {
                            startEpic = startSubtask;
                        }
                        if (endSubtask.isAfter(endEpic)) {
                            endEpic = endSubtask;
                        }
                    }
                }
            }
            epic.setStartTime(startEpic);
            epic.setEndTime(endEpic);
            if (startEpic != null) {
                Duration epicDuration = Duration.between(startEpic, endEpic);
                epic.setDuration(epicDuration);
            }
        } else {
            throw new RuntimeException("Отсутствуют подзадачи!");
        }
    }
}






