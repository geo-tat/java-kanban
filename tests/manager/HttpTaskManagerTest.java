package manager;
import api.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskType.Epic;
import taskType.Status;
import taskType.Subtask;
import taskType.Task;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {
    KVServer server;
    TaskManager manager;

    Task task1 = new Task(0, "TASK-1", "description", Status.NEW);
    Epic epic1 = new Epic(0, "Epic-1", "description", Status.NEW, new ArrayList<>());
    Subtask sub1 = new Subtask(0, "Subtask-1", "description", Status.NEW, 1);

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        server = new KVServer();
        server.start();
        manager = Managers.getDefault();
    }

    @AfterEach
    void end() {
        server.stop();
    }

    @Test
    void save() {
        manager.createTask(task1);
        assertEquals(task1, manager.getTaskById(1));

    }

    @Test
    void loadFromServer() {
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        assertEquals(epic1, manager.getEpicById(1));
        assertEquals(sub1, manager.getSubtaskById(2));
    }

    @Test
    void saveAndLoadWhenEmpty() {
        assertEquals(new ArrayList<Task>(), manager.getTasks(), "Список должен быть пустой");
        assertNull(manager.getTaskById(3), "Задачи не должно существовать");

    }
}