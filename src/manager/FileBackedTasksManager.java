package manager;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import taskType.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {
    File path;

    public FileBackedTasksManager(File path) {
        this.path = path;
    }

    public FileBackedTasksManager() {
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(path)) {

            fileWriter.write("id,type,name,status,description,startTime,duration,epic" + "\n");

            for (Task task : tasks.values()) {
                fileWriter.write(stringToCsv(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                fileWriter.write(stringToCsv(epic) + "\n");
            }

            for (Subtask sub : subtasks.values()) {
                fileWriter.write(stringToCsv(sub) + "\n");
            }


            fileWriter.write("\n");

            boolean appendComma = false;
            for (Task h : historyManager.getHistory()) {
                if (appendComma) fileWriter.write(",");
                fileWriter.write(Integer.toString(h.getId()));
                appendComma = true;
            }

        } catch (IOException e) {
            throw new ManagerSaveException("ERROR", e);

        }

    }

    public static FileBackedTasksManager loadFromFile(File file) {
        var manager = new FileBackedTasksManager();
        int loadID = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (br.ready()) {
                String line = br.readLine();
                lines.add(line);
            }
            br.close();
            for (String line : lines) {

                if (line.equals("")) {
                    if (lines.get(lines.size() - 1).isEmpty()) {
                        break;
                    }
                    String history = lines.get(lines.size() - 1);
                    List<Integer> l = historyFromString(history);

                    for (Integer id : l) {
                        if (manager.tasks.containsKey(id)) {
                            manager.historyManager.add(manager.tasks.get(id));
                        }
                        if (manager.subtasks.containsKey(id)) {
                            manager.historyManager.add(manager.subtasks.get(id));
                        }
                        if (manager.epics.containsKey(id)) {
                            manager.historyManager.add(manager.epics.get(id));
                        }
                    }

                    break;
                }
                String[] words = line.split(",");

                if (words[1].equals("TASK")) {
                    Task task = fromString(line);
                    manager.tasks.put(task.getId(), task);
                    if (task.getId() > loadID) {
                        loadID = task.getId();
                    }
                    continue;
                }
                if (words[1].equals("SUBTASK")) {
                    Subtask subtask = (Subtask) fromString(line);
                    manager.subtasks.put(subtask.getId(), subtask);
                    manager.epics.get(subtask.getEpicID()).getSubtasksID().add(subtask.getId());
                    if (subtask.getId() > loadID) {
                        loadID = subtask.getId();
                    }
                    continue;
                }
                if (words[1].equals("EPIC")) {
                    Epic epic = (Epic) fromString(line);
                    manager.epics.put(epic.getId(), epic);
                    if (epic.getId() > loadID) {
                        loadID = epic.getId();
                    }
                }

            }


        } catch (IOException e) {
            throw new ManagerLoadException("ERROR", e);
        }
        manager.currentID = loadID;
        return manager;
    }

    public static Task fromString(String value) {
        LocalDateTime startTime;
        Duration duration;
        String[] part = value.split(",");
        int id = Integer.parseInt(part[0]);

        String name = part[2];
        Status.valueOf(part[3]);
        String description = part[4];
        if (part[5].equals("null")) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(part[5]);
        }
        if (part[6].equals("null")) {
            duration = null;
        } else {
            duration = Duration.parse(part[6]);
        }

        switch (Type.valueOf(part[1])) {
            case TASK:
                return new Task(id, name, description, Status.valueOf(part[3]));


            case SUBTASK:
                var epicId = Integer.parseInt(part[7]);
                return new Subtask(id, name, description, Status.valueOf(part[3]), epicId);


            case EPIC:
                return new Epic(id, name, description, Status.valueOf(part[3]), new ArrayList<Integer>());

        }
        return null;
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> his = new ArrayList<>();
        String[] ids = value.split(",");
        for (String i : ids) {
            int id = Integer.parseInt(i);
            his.add(id);
        }


        return his;
    }

    public static void main(String[] args) {


    }

    private static String stringToCsv(Task task) {
        return task.getId() + "," + Type.TASK + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getStartTime() + "," + task.getDuration();
    }

    private static String stringToCsv(Subtask subtask) {
        return subtask.getId() + "," + Type.SUBTASK + "," + subtask.getName() + "," + subtask.getStatus()
                + "," + subtask.getDescription() + "," + subtask.getStartTime() + "," + subtask.getDuration() + ","
                + subtask.getEpicID();
    }

    private static String stringToCsv(Epic epic) {
        return epic.getId() + "," + Type.EPIC + "," + epic.getName() + "," + epic.getStatus() + ","
                + epic.getDescription() + "," + epic.getStartTime() + "," + epic.getDuration();

    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();

        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSub = super.createSubtask(subtask);
        save();
        return newSub;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean updated = super.updateTask(task);
        save();
        return updated;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean updated = super.updateSubtask(subtask);
        save();
        return updated;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean updated = super.updateEpic(epic);
        save();
        return updated;
    }

    @Override
    public boolean removeTaskById(int id) {
        boolean isRemove = super.removeTaskById(id);
        save();
        return isRemove;
    }

    @Override
    public boolean removeSubtaskById(int id) {
        boolean isRemove = super.removeSubtaskById(id);
        save();
        return isRemove;
    }

    @Override
    public boolean removeEpicForId(int id) {
        boolean isRemove = super.removeEpicForId(id);
        save();
        return isRemove;
    }

    @Override
    public Task getTaskById(int id) {
        Task newTask = super.getTaskById(id);
        save();
        return newTask;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask s = super.getSubtaskById(id);
        save();
        return s;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic e = super.getEpicById(id);
        save();
        return e;
    }
}



