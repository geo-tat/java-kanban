import Manager.Managers;
import Manager.TaskManager;
import TaskType.Epic;
import TaskType.Status;
import TaskType.Subtask;
import TaskType.Task;

import java.util.ArrayList;


public class Main {


    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();


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

        System.out.println(manager.getTaskById(3));
        System.out.println(manager.getSubtaskById(6));
        System.out.println(manager.getSubtaskById(5));
        System.out.println(manager.getEpicById(4));
        System.out.println(manager.getEpicById(4));
        System.out.println("History:" + manager.getHistory());

        manager.removeTaskById(2);
        System.out.println("History:" + manager.getHistory());

        manager.removeEpicForId(1);
        System.out.println("History:" + manager.getHistory());

    }
}