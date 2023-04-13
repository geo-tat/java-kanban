package manager;

import taskType.Epic;
import taskType.Subtask;
import taskType.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
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
        // When
        manager.createTask(task1);
        manager.getTaskById(1);
        manager.createEpic(epic1);
        manager.createSubtask(sub4);
        manager.getEpicById(2);
        final List<Task> historyTest = manager.getHistory();
        final List<Task> tasksCheck = List.of(task1);
        final List<Subtask> subCheck = List.of(sub4);
        final List<Epic> epicCheck = List.of(epic1);
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
        manager.createEpic(epic1);
        manager.getEpicById(1);
        List<Epic>epicCheck = List.of(epic1);
        // When
        FileBackedTasksManager loadManager = FileBackedTasksManager.loadFromFile(path);final List<Epic> epicLoad = loadManager.getEpics();

        // Then
        assertNotNull(loadManager);
        assertEquals(epicCheck,epicLoad);
    }

    @Test
    void loadWhenNoHistory() {
        // Given
        manager.createEpic(epic1);
        manager.createTask(task1);
        manager.createSubtask(sub1);
        final List<Task> tasksCheck = List.of(task1);
        final List<Subtask> subCheck = List.of(sub1);
        final List<Epic> epicCheck = List.of(epic1);
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