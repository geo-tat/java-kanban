import Manager.*;
import TaskType.Epic;
import TaskType.Status;
import TaskType.Subtask;
import TaskType.Task;

import java.util.ArrayList;


public class Main {


    public static void main(String[] args) {

        TaskManager manager = new InMemoryTaskManager();
        HistoryManager historyManager = new InMemoryHistoryManager();

        Epic epic01 = new Epic(0, "Make breakfast", "Maybe eggs?", new ArrayList<>());

        Task task01 = new Task(0, "Wake up", "At 8:00", Status.NEW);
        Task task02 = new Task(0, "Read news", "Searching internet", Status.NEW);
        Subtask subtask01 = new Subtask(0, "Buy eggs", "Where best eggs?", Status.NEW, 1);
        Epic epic02 = new Epic(0, "Work", "Finish project", new ArrayList<>());
        Subtask subtask02 = new Subtask(0, "update method", "make it work", Status.NEW, 4);
        Subtask subtask03 = new Subtask(0, "code test", "create objects", Status.NEW, 4);
        manager.createEpic(epic01);
        manager.createTask(task01);
        manager.createTask(task02);

        manager.createEpic(epic02);
        manager.createSubtask(subtask01);
        manager.createSubtask(subtask02);
        manager.createSubtask(subtask03);

        System.out.println(manager.getTaskById(2));
        System.out.println(manager.getEpicById(1));
        System.out.println(manager.getSubtaskById(6));
        System.out.println(manager.getSubtaskById(7));
        System.out.println(manager.getHistory());

    }
}