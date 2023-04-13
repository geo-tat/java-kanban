package manager;

import exceptions.TaskIntersectionException;
import taskType.Epic;
import taskType.Status;
import taskType.Subtask;
import taskType.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    T manager;
    Task task1 = new Task(0, "TASK-1", "description", Status.NEW);
    Task task2 = new Task(0, "TASK-2", "description", Status.NEW);
    Task task3 = new Task(0, "TASK-3", "description", Status.NEW);
    Subtask sub1 = new Subtask(0, "Subtask-1", "description", Status.NEW, 1);
    Subtask sub2 = new Subtask(0, "Subtask-2", "description", Status.NEW, 1);
    Subtask sub3 = new Subtask(0, "Subtask-3", "description", Status.NEW, 1);
    Subtask sub4 = new Subtask(0, "Subtask-4", "description", Status.NEW, 2);
    Epic epic1 = new Epic(0, "Epic-1", "description", Status.NEW, new ArrayList<>());
    Epic epic2 = new Epic(0, "Epic-2", "description", Status.NEW, new ArrayList<>());
    Epic epic3 = new Epic(0, "Epic-3", "description", Status.NEW, new ArrayList<>());

    // -----Create Task-----//
    @Test
    void shouldCreateTask() {
        final int taskId = manager.createTask(task1).getId();

        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");


    }

    @Test
    void CreateTaskWithIncorrectId() {
        // Given
        final int taskIncorrectId = task1.getId() + 10;
        // When
        final Task savedTask = manager.createTask(task1);
        // Then
        assertNotEquals(taskIncorrectId, savedTask.getId(), "Неверная обработка ID");
    }

    //-----Create Subtask-----//
    @Test
    void shouldCreateSubtask() {
        // Given
        manager.createEpic(epic1);
        // When
        final int subtaskId = manager.createSubtask(sub1).getId();
        final Subtask savedSubtask = manager.getSubtaskById(subtaskId);
        // Then
        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(sub1, savedSubtask, "Задачи не совпадают");

        final List<Subtask> subtasks = manager.getSubtasks();
        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(sub1, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void createSubTaskWithIncorrectId() {
        // Given
        final int taskIncorrectId = sub1.getId() + 10;
        // When
        manager.createEpic(epic1);
        final Subtask savedSubtask = manager.createSubtask(sub1);
        // Then
        assertNotEquals(taskIncorrectId, savedSubtask.getId(), "Неверная обработка ID");
    }

    //-----Create Epic-----//
    @Test
    void shouldCreateEpic() {
        // Given

        // When
        final int EpicId = manager.createEpic(epic1).getId();
        final Epic savedEpic = manager.getEpicById(EpicId);
        // Then
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают");

        final List<Epic> epics = manager.getEpics();
        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic1, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void createEpicWithIncorrectId() {
        // Given
        final int epicIncorrectId = epic1.getId() + 10;
        // When

        final Epic savedEpic = manager.createEpic(epic1);
        // Then
        assertNotEquals(epicIncorrectId, savedEpic.getId(), "Неверная обработка ID");
    }

    //-----Update Task-----//
    @Test
    void shouldUpdateTaskWhenNormal() {
        // Given
        final int taskID = manager.createTask(task1).getId();
        task1.setDescription("NEW DESCRIPTION");
        task1.setName("NEW_TASK-1");
        task1.setStatus(Status.DONE);
        // When
        boolean result = manager.updateTask(task1);
        // Then
        assertTrue(result, "Задача не обновилась");
        assertEquals(task1, manager.getTaskById(taskID), "Задачи не совпадают");
    }

    @Test
    void updateTaskWhenEmptyList() {
        // Given
        // When
        boolean result = manager.updateTask(task1);
        // Then
        assertFalse(result);
    }

    @Test
    void updateTaskWhenIncorrectId() {
        // Given
        manager.createTask(task1);
        task1.setStatus(Status.IN_PROGRESS);
        task1.setId(33);
        // When
        boolean result = manager.updateTask(task1);
        // Then
        assertFalse(result);
    }

    //-----Update SUBTASK-----//
    @Test
    void shouldUpdateSubtaskWhenNormal() {
        // Given
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        sub1.setStatus(Status.IN_PROGRESS);
        // When
        boolean result = manager.updateSubtask(sub1);
        // Then
        assertTrue(result);
    }

    @Test
    void updateSubtaskWhenEmptyList() {
        // Given
        sub1.setStatus(Status.IN_PROGRESS);
        // When
        boolean result = manager.updateSubtask(sub1);
        // Then
        assertFalse(result);
    }

    @Test
    void updateSubtaskWhenIncorrectId() {
        // Given
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        sub1.setId(33);
        sub1.setStatus(Status.IN_PROGRESS);
        // When
        boolean result = manager.updateSubtask(sub1);
        // Then
        assertFalse(result);
    }

//-----Update EPIC-----//

    @Test
    void shouldUpdateEpicWhenNormal() {
        // Given
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        epic1.setDescription("Add NEW SUB");
        // When
        boolean result = manager.updateEpic(epic1);
        // Then
        assertTrue(result);
    }

    @Test
    void updateEpicWhenEmptyList() {
        // Given
        epic1.setDescription("List is empty");
        // When
        boolean result = manager.updateEpic(epic1);
        // Then
        assertFalse(result);
    }

    @Test
    void updateEpicWhenIncorrectId() {
        // Given
        manager.createEpic(epic1);
        epic1.setDescription("List is empty");
        epic1.setId(44);
        // When
        boolean result = manager.updateEpic(epic1);
        // Then
        assertFalse(result);
    }

    //-----Get TASKS-----//
    @Test
    void shouldGetTasks() {
        // Given
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        // When
        List<Task> result = manager.getTasks();
        // Then
        assertEquals(List.of(task1, task2, task3), result);
    }

    @Test
    void getTasksWhenEmpty() {
        assertEquals(new ArrayList<>(), manager.getTasks());
    }

    //-----Get SUBTASKS-----//
    @Test
    void shouldGetSubtasks() {
        // Given
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        // When
        List<Subtask> subtasks = manager.getSubtasks();
        // Then
        assertEquals(List.of(sub1, sub2), subtasks);
    }

    @Test
    void getSubtasksWhenEmpty() {
        assertEquals(new ArrayList<>(), manager.getSubtasks());
    }

    //-----Get EPICS-----//
    @Test
    void shouldGetEpics() {
        // Given
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        // When
        List<Epic> result = manager.getEpics();
        // Then
        assertEquals(List.of(epic1, epic2, epic3), result, "Список вызывается неправильно.");
    }

    @Test
    void getEpicsWhenEmpty() {
        assertEquals(new ArrayList<>(), manager.getEpics());
    }

    //-----Get TASK by ID-----//
    @Test
    void getTaskByIdNormal() {
        // Given
        final int taskID = manager.createTask(task1).getId();
        // When
        Task savedTask = manager.getTaskById(taskID);
        // Then
        assertEquals(task1, savedTask);
    }

    @Test
    void getTaskByIdWhenEmptyList() {
        // Given
        // When
        final Task result = manager.getTaskById(task1.getId());
        // Then
        assertNull(result);
    }

    @Test
    void getTaskByIdWhenIncorrectId() {
        // Given
        manager.createTask(task1);
        // When
        final Task result = manager.getTaskById(30);
        // Then
        assertNull(result);
    }

//-----Get SUBTASK By ID-----//

    @Test
    void getSubtaskByIdWhenNormal() {
        // Given
        manager.createEpic(epic1);
        final int subtaskID = manager.createSubtask(sub1).getId();
        // When
        Task savedSubtask = manager.getSubtaskById(subtaskID);
        // Then
        assertEquals(sub1, savedSubtask);
    }

    @Test
    void getSubtaskByIdWhenEmptyList() {
        // Given
        // When
        Subtask result = manager.getSubtaskById(sub1.getId());
        // Then
        assertNull(result);
    }

    @Test
    void getSubtaskByIdWhenIncorrectId() {
        // Given
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        // When
        Subtask result = manager.getSubtaskById(40);
        // Then
        assertNull(result);
    }

//-----Get EPIC By ID-----//

    @Test
    void getEpicByIdWhenNormal() {
        // Given
        final int epicID = manager.createEpic(epic1).getId();
        // When
        Epic result = manager.getEpicById(epicID);
        // Then
        assertEquals(epic1, result);
    }

    @Test
    void getEpicByIdWhenEmptyList() {
        // Given
        // When
        Epic result = manager.getEpicById(1);
        // Then
        assertNull(result);
    }

    @Test
    void getEpicByIdWhenIncorrectId() {
        // Given
        manager.createEpic(epic1);
        // When
        Epic result = manager.getEpicById(99);
        // Then
        assertNull(result);
    }

    //-----Remove All TASKS-----//

    @Test
    void shouldRemoveAllTasks() {
        // Given
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        // When
        manager.removeAllTasks();
        // Then
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    void shouldRemoveAllTasksWhenEmptyList() {
        // Given
        // When
        manager.removeAllTasks();
        // Then
        assertEquals(0, manager.getTasks().size());
    }

    //-----Remove All SUBTASKS-----//

    @Test
    void shouldRemoveAllSubtasks() {
        // Given
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        // When
        manager.removeAllSubtasks();
        // Then
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    void shouldRemoveAllSubtasksWhenEmptyList() {
        // Given
        // When
        manager.removeAllSubtasks();
        // Then
        assertEquals(0, manager.getSubtasks().size());
    }

    //-----Remove All EPICS-----//

    @Test
    void shouldRemoveAllEpics() {
        // Given
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        // When
        manager.removeAllEpics();
        // Then
        assertEquals(0, manager.getEpics().size());
    }

    @Test
    void shouldRemoveAllEpicsWhenEmptyList() {
        // Given
        // When
        manager.removeAllEpics();
        // Then
        assertEquals(0, manager.getEpics().size());
    }

    //-----Remove TASK By ID-----//

    @Test
    void shouldRemoveTaskById() {
        // Given
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        // When
        boolean result = manager.removeTaskById(2);
        // Then
        assertEquals(2, manager.getTasks().size());
        assertNull(manager.getTaskById(2));
        assertTrue(result);
    }

    @Test
    void removeTaskByIdWhenEmptyList() {
        // Given
        // When
        boolean result = manager.removeTaskById(15);
        // Then
        assertNull(manager.getTaskById(15));
        assertFalse(result);
    }

    @Test
    void removeTaskByIdWhenIncorrectID() {
        // Given
        manager.createTask(task1);
        // When
        boolean result = manager.removeTaskById(50);
        // Then
        assertFalse(result);
    }

    //-----Remove SUBTASK By ID-----//

    @Test
    void shouldRemoveSubtaskByID() {
        // Given
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        // When
        boolean result = manager.removeSubtaskById(2);
        // Then
        assertTrue(result);
        assertNull(manager.getSubtaskById(2));
        assertEquals(1, manager.getSubtasks().size());
    }

    @Test
    void removeSubtaskByIDWhenEmptyList() {
        // Given
        // When
        boolean result = manager.removeSubtaskById(2);
        // Then
        assertFalse(result);
    }

    @Test
    void removeSubtaskByIdWhenIncorrectID() {
        // Given
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        // When
        boolean result = manager.removeSubtaskById(44);
        // Then
        assertFalse(result);
    }
    //-----Remove EPIC By ID-----//

    @Test
    void shouldRemoveEpicByID() {
        // Given
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        // When
        boolean result = manager.removeEpicForId(3);
        // Then
        assertTrue(result);
        assertEquals(2, manager.getEpics().size());
        assertNull(manager.getEpicById(3));
    }

    @Test
    void removeEpicByIdWhenEmptyList() {
        // Given
        // When
        boolean result = manager.removeEpicForId(12);
        // Then
        assertFalse(result);
    }

    @Test
    void removeEpicByIDWhenIncorrectID() {
        // Given
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        // When
        boolean result = manager.removeEpicForId(13);
        // Then
        assertEquals(3, manager.getEpics().size());
        assertFalse(result);
        assertNull(manager.getEpicById(13));
    }

    //-----Get History-----//

    @Test
    void shouldGetHistory() {
        // Given
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        manager.getTaskById(4);
        manager.getTaskById(5);
        manager.getEpicById(1);
        manager.getSubtaskById(3);
        manager.getSubtaskById(2);
        manager.getTaskById(6);
        // When
        List<Task> result = manager.getHistory();
        // Then
        assertNotNull(result);
        assertEquals(List.of(task1, task2, epic1, sub2, sub1, task3), result);
    }

    @Test
    void shouldGetSubtaskForEpic() {
        // Given
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);
        List<Subtask> check = new ArrayList<>(List.of(sub1, sub2, sub3));
        // When
        final List<Subtask> result = manager.getSubtasksForEpic(1);
        // Then
        assertEquals(3, result.size());
        assertEquals(check, result);
    }

    @Test
    void shouldGetPrioritizedTasks() {
        // Given
        task1.setStartTime(LocalDateTime.of(2022, 1, 1, 12, 0));
        task1.setDuration(Duration.ofMinutes(40));
        task2.setStartTime(LocalDateTime.of(2022, 1, 1, 15, 0));
        task2.setDuration(Duration.ofMinutes(35));
        sub1.setStartTime(LocalDateTime.of(2022, 1, 1, 18, 0));
        sub1.setDuration(Duration.ofMinutes(80));
        sub2.setStartTime(LocalDateTime.of(2022, 1, 1, 21, 0));
        sub2.setDuration(Duration.ofMinutes(15));
        List<Task> check = new ArrayList<>(List.of(task1, task2, sub1, sub2));
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        manager.createTask(task2);
        manager.createSubtask(sub2);
        manager.createTask(task1);
        // When
        List<Task> result = manager.getPrioritizedTasks();
        // Then
        assertEquals(check, result);
        assertEquals(task1, result.get(0));
    }

    @Test
    void getPrioritizedTasksWhenEmptyList() {
        // Given
        // When
        List<Task> result = manager.getPrioritizedTasks();
        // Then
        assertEquals(new ArrayList<Task>(), result);
    }

    @Test
    void getPrioritizedTasksWhenNoTimeOfSubtask() {
        // Given
        sub2.setStartTime(LocalDateTime.of(2023, 4, 13, 12, 0));
        sub2.setDuration(Duration.ofMinutes(25));
        manager.createEpic(epic1);
        manager.createTask(task2);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        List<Task> check = new ArrayList<>(List.of(sub2, task2, sub1));
        // When
        List<Task> result = manager.getPrioritizedTasks();
        // Then
        assertEquals(check.get(0), result.get(0));
        assertEquals(check, result);
    }

    @Test
    void shouldExceptTaskIntersectionExceptionWhenIntersection() {
        // Given
        task1.setStartTime(LocalDateTime.of(2022, 1, 1, 12, 0));
        task1.setDuration(Duration.ofMinutes(100));
        task2.setStartTime(LocalDateTime.of(2022, 1, 1, 13, 0));
        task2.setDuration(Duration.ofMinutes(15));
        // When
        manager.createTask(task1);
        // Then
        Assertions.assertThrows(TaskIntersectionException.class, () -> manager.createTask(task2));
    }

}