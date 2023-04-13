package Manager;

import TaskType.Epic;
import TaskType.Status;
import TaskType.Subtask;
import TaskType.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    File path = new File("resources\\text_file.csv");

    @BeforeEach
    void setUp() {

        manager = new FileBackedTasksManager(path);
    }

    @Test
    void save() {
        // Given
        Task task = new Task(0, "task-1", "something", Status.NEW);
        Epic epic = new Epic(0, "epic-1", "description", Status.NEW, new ArrayList<>());
        Subtask sub = new Subtask(0, "sub1", "description", Status.NEW, 2);
        // When
        manager.createTask(task);
        manager.getTaskById(1);
        manager.createEpic(epic);
        manager.createSubtask(sub);
        manager.getEpicById(2);
        final List<Task> historyTest = manager.getHistory();
        final List<Task> tasksCheck = List.of(task);
        final List<Subtask> subCheck = List.of(sub);
        final List<Epic> epicCheck = List.of(epic);
        // Then

        FileBackedTasksManager loadManager = FileBackedTasksManager.loadFromFile(path);
       final List<Task> loadList = loadManager.getHistory();
        assertEquals(historyTest, loadManager.getHistory());
        assertNotNull(loadList);
        assertEquals(tasksCheck,loadManager.getTasks());
        assertEquals(subCheck,loadManager.getSubtasks());
        assertEquals(epicCheck,loadManager.getEpics());

    }


    @Test
    void loadFromFileWhenEmpty() {
        // Given

        // When
        manager.save();
        // Then

        FileBackedTasksManager loadManager = FileBackedTasksManager.loadFromFile(path);
        final List<Task> loadList = loadManager.getHistory();
        assertNotNull(loadList);
        assertEquals(new ArrayList<>(),loadManager.getTasks());
        assertEquals(new ArrayList<>(),loadManager.getSubtasks());
        assertEquals(new ArrayList<>(),loadManager.getEpics());

    }
    @Test
    void loadWhenOnlyEpic() {
        // Given
        Epic epic = new Epic(0, "epic-1", "description", Status.NEW, new ArrayList<>());
        manager.createEpic(epic);
        manager.getEpicById(1);
        List<Epic>epicCheck = List.of(epic);
        // When
        FileBackedTasksManager loadManager = FileBackedTasksManager.loadFromFile(path);
        final List<Epic> epicLoad = loadManager.getEpics();

        // Then
        assertNotNull(loadManager);
        assertEquals(epicCheck,epicLoad);
    }

    @Test
    void loadWhenNoHistory() {
        // Given
        Task task = new Task(0, "task-1", "something", Status.NEW);
        Epic epic = new Epic(0, "epic-1", "description", Status.NEW, new ArrayList<>());
        Subtask sub = new Subtask(0, "sub1", "description", Status.NEW, 2);

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(sub);
        final List<Task> tasksCheck = List.of(task);
        final List<Subtask> subCheck = List.of(sub);
        final List<Epic> epicCheck = List.of(epic);
        // When
        FileBackedTasksManager loadManager = FileBackedTasksManager.loadFromFile(path);
        final List<Epic> epicLoad = loadManager.getEpics();
        final List<Subtask> subLoad = loadManager.getSubtasks();
        final List<Task> taskLoad = loadManager.getTasks();
        // Then
        assertNotNull(loadManager);
        assertEquals(tasksCheck,taskLoad);
        assertEquals(subCheck,subLoad);
        assertEquals(epicCheck,epicLoad);
    }
}