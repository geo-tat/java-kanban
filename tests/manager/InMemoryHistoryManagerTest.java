package manager;

import taskType.Epic;
import taskType.Status;
import taskType.Subtask;
import taskType.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    Task task1 = new Task(0, "TASK-1", "description", Status.NEW);
    HistoryManager manager;
    @BeforeEach
    void setUp() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    void addWhenEmptyList() {
        // Given
        final List<Task> list = new ArrayList<>();
        list.add(task1);
        // When
        manager.add(task1);
        // Then
        assertEquals(list, manager.getHistory());
    }

    @Test
    void addWhenDoubleTask() {
        // Given
        final List<Task> list = new ArrayList<>();
        list.add(task1);
        // When
        manager.add(task1);
        manager.add(task1);
        // Then
        assertEquals(list, manager.getHistory());
    }

    @Test
    void remove() {
        // Given
        Task task = new Task(1, "Task-1", "description", Status.NEW);
        Task task2 = new Task(2, "Task-2", "description", Status.NEW);
        Epic epic = new Epic(3, "Epic-1", "description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(4, "Sub-1", "description", Status.NEW, 3);
        Subtask sub2 = new Subtask(5, "Sub-2", "description", Status.NEW, 1);
        Task task3 = new Task(6, "Task-3", "description", Status.NEW);
        final List<Task> list = new ArrayList<>(List.of(task, task2, epic, sub1, sub2, task3));
        manager.add(task);
        manager.add(task2);
        manager.add(epic);
        manager.add(sub1);
        manager.add(sub2);
        manager.add(task3);

        // When
        manager.remove(task.getId());
        list.remove(task);
        // Then
        assertEquals(list,manager.getHistory());
        // When
        manager.remove(sub1.getId());
        list.remove(sub1);
        // Then
        assertEquals(list,manager.getHistory());
        // When
        manager.remove(task3.getId());
        list.remove(task3);
        // Then
        assertEquals(list,manager.getHistory());
    }

    @Test
    void shouldGetHistoryWhenEmpty() {
        // Given

        // When
       List<Task> result = manager.getHistory();
        // Then
        assertEquals(new ArrayList<>(),result);
    }

    @Test
    void shouldGetHistory() {
        // Given
        Task task1 = new Task(2, "Task-1", "description", Status.NEW);
        Epic epic = new Epic(3, "Epic-1", "description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(4, "Sub-1", "description", Status.NEW, 3);
        List<Task> list = new ArrayList<>(List.of(task1,epic,sub1));
        manager.add(task1);
        manager.add(epic);
        manager.add(sub1);
        // When
        List<Task> result = manager.getHistory();
        // Then
        assertEquals(list,result);
    }

}