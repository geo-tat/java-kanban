package Manager;
import Exceptions.ManagerLoadException;
import Exceptions.ManagerSaveException;
import TaskType.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {
    static File path;

    public FileBackedTasksManager(File path) {
        this.path = path;
    }

    public FileBackedTasksManager() {
    }

    public void save() {
        try (Writer fileWriter = new FileWriter("resources/history.csv")) {

            fileWriter.write("id,type,name,status,description,epic" + "\n");

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

            for (Task h : historyManager.getHistory()) {
                fileWriter.write(h.getId() + ",");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("ERROR", e);

        }

    }

    public static FileBackedTasksManager loadFromFile(File file) {
        var manager = new FileBackedTasksManager();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (br.ready()) {
                String line = br.readLine();
                lines.add(line);
            }
            br.close();
            for (String line : lines) {

                if (line.equals("")) {
                    String history = lines.get(lines.size() - 1);
                    List<Integer> l = historyFromString(history);
                    for( Integer id : l) {
                        if(manager.tasks.containsKey(id)) {
                            manager.historyManager.add(manager.tasks.get(id));
                        }
                        if(manager.subtasks.containsKey(id)) {
                            manager.historyManager.add(manager.subtasks.get(id));
                        }
                        if(manager.epics.containsKey(id)) {
                            manager.historyManager.add(manager.epics.get(id));
                        }
                    }
                    break;
                }
                String[] words = line.split(",");
                if (words[1].equals("TASK")) {
                    Task task = fromString(line);
                    manager.createTask(task);
                    continue;
                }
                if (words[1].equals("SUBTASK")) {
                    Subtask subtask = (Subtask) fromString(line);
                    manager.createSubtask(subtask);
                    continue;
                }
                if (words[1].equals("EPIC")) {
                    Epic epic = (Epic) fromString(line);
                    manager.createEpic(epic);
                }
            }



        } catch (IOException e) {
            throw new ManagerLoadException("ERROR", e);
        }

        return manager;
    }

    public static Task fromString(String value) {
        String[] part = value.split(",");
        int id = Integer.parseInt(part[0]);

        String name = part[2];
        Status.valueOf(part[3]);
        String description = part[4];

        switch (Type.valueOf(part[1])) {
            case TASK:
                return new Task(id, name, description, Status.valueOf(part[3]));


            case SUBTASK:
                var epicId = Integer.parseInt(part[5]);
                return new Subtask(id, name, description, Status.valueOf(part[3]), epicId);


            case EPIC:
                return new Epic(id, name, description, Status.valueOf(part[3]), new ArrayList<>());

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
        TaskManager manager = Managers.getDefaultFile(new File("resources\\history.csv"));

        Epic epic01 = new Epic(0, "Make breakfast", "ПОЛНЫЙ ЭПИК", Status.NEW, new ArrayList<>());   // 1
        Task task01 = new Task(0, "Wake up", "ТАСК 1", Status.NEW);                                  // 2
        Task task02 = new Task(0, "Read news", "ТАСК 2", Status.NEW);                                // 3
        Epic epic02 = new Epic(0, "Work", "ПУСТОЙ ЭПИК", Status.NEW, new ArrayList<>());             // 4
        Subtask subtask01 = new Subtask(0, "Buy eggs", "САБТАСК 1", Status.NEW, 1);            // 5
        Subtask subtask02 = new Subtask(0, "update method", "САБТАСК 2", Status.NEW, 1);       // 6
        Subtask subtask03 = new Subtask(0, "code test", "САБТАСК 3", Status.NEW, 1);           // 7

        manager.createEpic(epic01);
        manager.createTask(task01);
        manager.createTask(task02);
        manager.createEpic(epic02);
        manager.createSubtask(subtask01);
        manager.createSubtask(subtask02);
        manager.createSubtask(subtask03);


        System.out.println(manager.getTaskById(2));
        System.out.println(manager.getEpicById(1));
        System.out.println(manager.getSubtaskById(7));
        System.out.println(manager.getSubtaskById(7));
        System.out.println("History:" + manager.getHistory());

        File file = new File("resources/history.csv");
        FileBackedTasksManager managerLoad = loadFromFile(file);
        System.out.println("History after load: " + managerLoad.getHistory());
        System.out.println(managerLoad.getTasks());
        System.out.println(managerLoad.getEpics());
        System.out.println(managerLoad.getSubtasks());
        System.out.println("WELL DONE");

    }

    public String stringToCsv (Task task) {
        return task.getId() + "," + Type.TASK + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription();
    }

    public String stringToCsv(Subtask subtask) {
        return subtask.getId() + "," + Type.SUBTASK + "," + subtask.getName() + "," + subtask.getStatus()
                + "," + subtask.getDescription() + "," + subtask.getEpicID();
    }

    public String stringToCsv(Epic epic) {
        return epic.getId() + "," + Type.EPIC + "," + epic.getName() + "," + epic.getStatus() + ","
                + epic.getDescription();

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
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
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


/*

    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    static String historyToString(HistoryManager manager) {

    }

     public static FileBackedTasksManager loadFromFile(File file) {
        TaskManager manager = new FileBackedTasksManager();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            List<String> lines = new ArrayList<>();
            while (br.ready()) {
                String line = br.readLine();
                String[] words = line.split(",");
               lines.add(line);
                if(line.equals("")) {
                    Scanner s = new Scanner(line);
                    String a = s.nextLine();
                    historyFromString(a);
                    continue;
                }
                if (words[1].equals("TASK")) {
                    Task task = fromString(line);
                    manager.createTask(task);
                    continue;
                }
                if (words[1].equals("SUBTASK")) {
                    Subtask subtask = (Subtask) fromString(line);
                    manager.createSubtask(subtask);
                    continue;
                }
                if (words[1].equals("EPIC")) {
                    Epic epic = (Epic) fromString(line);
                    manager.createEpic(epic);
                    continue;
                }
*/
