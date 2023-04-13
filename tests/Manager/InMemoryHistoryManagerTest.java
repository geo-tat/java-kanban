package Manager;

import TaskType.Epic;
import TaskType.Status;
import TaskType.Subtask;
import TaskType.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {

    @BeforeEach
    void setUp() {
        manager = new InMemoryHistoryManager();
        HistoryManager manager1 = Managers.getDefaultHistory();
    }

    @Test
    void addWhenEmptyList() {
        // Given
        Task task = new Task(1, "task", "description", Status.NEW);
        final List<Task> list = new ArrayList<>();
        list.add(task);
        // When
        manager.add(task);
        // Then
        assertEquals(list, manager.getHistory());
    }

    @Test
    void addWhenDoubleTask() {
        // Given
        Task task = new Task(1, "task", "description", Status.NEW);
        final List<Task> list = new ArrayList<>();
        list.add(task);
        // When
        manager.add(task);
        manager.add(task);
        // Then
        assertEquals(list, manager.getHistory());
    }

    @Test
    void remove() {
        // Given
        Task task = new Task(1, "task", "description", Status.NEW);
        Task task2 = new Task(2, "task", "description", Status.NEW);
        Epic epic = new Epic(3, "EPIC", "AAAbbbYYY", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(4, "sub-1", "something", Status.NEW, 3);
        Subtask sub2 = new Subtask(5, "Sub-2", "something", Status.NEW, 1);
        Task task3 = new Task(6, "Task03", "something", Status.NEW);
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
        Task task2 = new Task(2, "task", "description", Status.NEW);
        Epic epic = new Epic(3, "EPIC", "AAAbbbYYY", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(4, "sub-1", "something", Status.NEW, 3);
        List<Task> list = new ArrayList<>(List.of(task2,epic,sub1));
        manager.add(task2);
        manager.add(epic);
        manager.add(sub1);
        // When
        List<Task> result = manager.getHistory();
        // Then
        assertEquals(list,result);
    }

}