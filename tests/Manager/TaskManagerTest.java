package Manager;

import Exceptions.TaskIntersectionException;
import TaskType.Epic;
import TaskType.Status;
import TaskType.Subtask;
import TaskType.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    T manager;


    // -----Create Task-----//
    @Test
    void shouldCreateTask() {
        Task task01 = new Task(0, "TASK-1", "descrptn1", Status.NEW);

        final int taskId = manager.createTask(task01).getId();

        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task01, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task01, tasks.get(0), "Задачи не совпадают.");


    }

    @Test
    void CreateTaskWithIncorrectId() {
        // Given
        Task task01 = new Task(0, "TASK-1", "descrptn1", Status.NEW);
        final int taskIncorretcId = task01.getId() + 10;
        // When
        final Task savedTask = manager.createTask(task01);
        // Then
        assertNotEquals(taskIncorretcId, savedTask.getId(), "Неверная обработка ID");
    }

    //-----Create Subtask-----//
    @Test
    void shouldCreateSubtask() {
        // Given
        Epic epic1 = new Epic(0, "Epic-1", "description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Subtask-1", "description", Status.NEW, 1);
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
    void CreateSubTaskWithIncorrectId() {
        // Given
        Epic epic1 = new Epic(0, "Epic-1", "description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Subtask-1", "description", Status.NEW, 1);
        final int taskIncorretcId = sub1.getId() + 10;
        // When
        manager.createEpic(epic1);
        final Subtask savedSubtask = manager.createSubtask(sub1);
        // Then
        assertNotEquals(taskIncorretcId, savedSubtask.getId(), "Неверная обработка ID");
    }

    //-----Create Epic-----//
    @Test
    void shouldCreateEpic() {
        // Given
        Epic epic1 = new Epic(0, "Epic-1", "description", Status.NEW, new ArrayList<>());
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
    void CreateEpicWithIncorrectId() {
        // Given
        Epic epic1 = new Epic(0, "Epic-1", "description", Status.NEW, new ArrayList<>());

        final int epicIncorretcId = epic1.getId() + 10;
        // When

        final Epic savedEpic = manager.createEpic(epic1);
        // Then
        assertNotEquals(epicIncorretcId, savedEpic.getId(), "Неверная обработка ID");
    }

    //-----Update Task-----//
    @Test
    void shouldUpdateTaskWhenNormal() {
        // Given
        Task task01 = new Task(0, "TASK-1", "descrptn1", Status.NEW);
        final int taskID = manager.createTask(task01).getId();
        task01.setDescription("NEW DESCRIPTION");
        task01.setName("NEW_TASK-1");
        task01.setStatus(Status.DONE);
        // When
        boolean result = manager.updateTask(task01);
        // Then
        assertTrue(result, "Задача не обновилась");
        assertEquals(task01, manager.getTaskById(taskID), "Задачи не совпадают");
    }

    @Test
    void updateTaskWhenEmptyList() {
        // Given
        Task task01 = new Task(0, "TASK-1", "description1", Status.NEW);
        // When
        boolean result = manager.updateTask(task01);
        // Then
        assertFalse(result);
    }

    @Test
    void updateTaskWhenIncorrectId() {
        // Given
        Task task01 = new Task(0, "TASK-1", "description1", Status.NEW);
        manager.createTask(task01);
        task01.setStatus(Status.IN_PROGRESS);
        task01.setId(33);
        // When
        boolean result = manager.updateTask(task01);
        // Then
        assertFalse(result);
    }

    //-----Update SUBTASK-----//
    @Test
    void shouldUpdateSubtaskWhenNormal() {
        // Given
        Epic epic = new Epic(0, "Epic", "Description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "description", Status.NEW, 1);
        manager.createEpic(epic);
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
        Epic epic = new Epic(0, "Epic", "Description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "description", Status.NEW, 1);
        sub1.setStatus(Status.IN_PROGRESS);
        // When
        boolean result = manager.updateSubtask(sub1);
        // Then
        assertFalse(result);
    }

    @Test
    void updateSubtaskWhenIncorrectId() {
        // Given
        Epic epic = new Epic(0, "Epic", "Description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "description", Status.NEW, 1);
        manager.createEpic(epic);
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
        Epic epic = new Epic(0, "Epic", "Description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "description", Status.NEW, 1);
        manager.createEpic(epic);
        manager.createSubtask(sub1);
        epic.setDescription("Add NEW SUB");

        // When
        boolean result = manager.updateEpic(epic);
        // Then
        assertTrue(result);
    }

    @Test
    void updateEpicWhenEmptyList() {
        // Given
        Epic epic = new Epic(0, "Epic", "Description", Status.NEW, new ArrayList<>());
        epic.setDescription("List is empty");
        // When
        boolean result = manager.updateEpic(epic);
        // Then
        assertFalse(result);
    }

    @Test
    void updateEpicWhenIncorrectId() {
        // Given
        Epic epic = new Epic(0, "Epic", "Description", Status.NEW, new ArrayList<>());
        manager.createEpic(epic);
        epic.setDescription("List is empty");
        epic.setId(44);
        // When
        boolean result = manager.updateEpic(epic);
        // Then
        assertFalse(result);
    }

    //-----Get TASKS-----//
    @Test
    void shouldGetTasks() {
        // Given
        Task task1 = new Task(0, "Task-1", "Description-1", Status.NEW);
        Task task2 = new Task(0, "Task-2", "Description-2", Status.NEW);
        Task task3 = new Task(0, "Task-3", "Description-3", Status.NEW);
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
        Epic epic1 = new Epic(0, "Epic-1", "Description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "Description", Status.NEW, 1);
        Subtask sub2 = new Subtask(0, "Sub-2", "Description-2", Status.NEW, 1);
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
        Epic epic1 = new Epic(0, "Epic-1", "description1", Status.NEW, new ArrayList<>());
        Epic epic2 = new Epic(0, "Epic-2", "description2", Status.NEW, new ArrayList<>());
        Epic epic3 = new Epic(0, "Epic-3", "description3", Status.NEW, new ArrayList<>());
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
        Task task1 = new Task(0, "Task-1", "Description", Status.NEW);
        final int taskID = manager.createTask(task1).getId();
        // When
        Task savedTask = manager.getTaskById(taskID);
        // Then
        assertEquals(task1, savedTask);
    }

    @Test
    void getTaskByIdWhenEmptyList() {
        // Given
        Task task1 = new Task(0, "Task-1", "Description", Status.NEW);
        // When
        final Task result = manager.getTaskById(task1.getId());
        // Then
        assertNull(result);
    }

    @Test
    void getTaskByIdWhenIncorrectId() {
        // Given
        Task task1 = new Task(0, "Task-1", "Description", Status.NEW);
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
        Epic epic = new Epic(0, "Epic-1", "Description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Task-1", "Description", Status.NEW, 1);
        manager.createEpic(epic);
        final int subtaskID = manager.createSubtask(sub1).getId();
        // When
        Task savedSubtask = manager.getSubtaskById(subtaskID);
        // Then
        assertEquals(sub1, savedSubtask);
    }

    @Test
    void getSubtaskByIdWhenEmptyList() {
        // Given
        Subtask sub1 = new Subtask(0, "Task-1", "Description", Status.NEW, 1);

        // When
        Subtask result = manager.getSubtaskById(sub1.getId());
        // Then
        assertNull(result);

    }

    @Test
    void getSubtaskByIdWhenIncorrectId() {
        // Given
        Epic epic = new Epic(0, "Epic-1", "Description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Task-1", "Description", Status.NEW, 1);
        manager.createEpic(epic);
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
        Epic epic = new Epic(0, "Epic-1", "Description", Status.NEW, new ArrayList<>());
        final int epicID = manager.createEpic(epic).getId();
        // When
        Epic result = manager.getEpicById(epicID);
        // Then
        assertEquals(epic, result);
    }

    @Test
    void getEpicByIdWhenEmptyList() {
        // Given
        Epic epic = new Epic(0, "Epic-1", "Description", Status.NEW, new ArrayList<>());
        // When
        Epic result = manager.getEpicById(1);
        // Then
        assertNull(result);
    }

    @Test
    void getEpicByIdWhenIncorrectId() {
        // Given
        Epic epic = new Epic(0, "Epic-1", "Description", Status.NEW, new ArrayList<>());
        manager.createEpic(epic);
        // When
        Epic result = manager.getEpicById(99);
        // Then
        assertNull(result);
    }

    //-----Remove All TASKS-----//

    @Test
    void shouldRemoveAllTasks() {
        // Given
        Task task1 = new Task(0, "Task-1", "Description-1", Status.NEW);
        Task task2 = new Task(0, "Task-2", "Description-2", Status.NEW);
        Task task3 = new Task(0, "Task-3", "Description-3", Status.NEW);
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
        Epic epic1 = new Epic(0, "Epic-1", "Description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "Description", Status.NEW, 1);
        Subtask sub2 = new Subtask(0, "Sub-2", "Description-2", Status.NEW, 1);
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
        Epic epic1 = new Epic(0, "Epic-1", "description1", Status.NEW, new ArrayList<>());
        Epic epic2 = new Epic(0, "Epic-2", "description2", Status.NEW, new ArrayList<>());
        Epic epic3 = new Epic(0, "Epic-3", "description3", Status.NEW, new ArrayList<>());
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
        Task task1 = new Task(0, "Task-1", "Description-1", Status.NEW);
        Task task2 = new Task(0, "Task-2", "Description-2", Status.NEW);
        Task task3 = new Task(0, "Task-3", "Description-3", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        // When
       boolean result = manager.removeTaskById(2);
        // Then
        assertEquals(2,manager.getTasks().size());
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
        Task task1 = new Task(0, "Task-1", "Description-1", Status.NEW);
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
        Epic epic1 = new Epic(0, "Epic-1", "Description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "Description", Status.NEW, 1);
        Subtask sub2 = new Subtask(0, "Sub-2", "Description-2", Status.NEW, 1);
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        // When
        boolean result = manager.removeSubtaskById(2);

        // Then
        assertTrue(result);
        assertNull(manager.getSubtaskById(2));
        assertEquals(1,manager.getSubtasks().size());
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
        Epic epic1 = new Epic(0, "Epic-1", "Description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "Description", Status.NEW, 1);
        Subtask sub2 = new Subtask(0, "Sub-2", "Description-2", Status.NEW, 1);
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
        Epic epic1 = new Epic(0, "Epic-1", "description1", Status.NEW, new ArrayList<>());
        Epic epic2 = new Epic(0, "Epic-2", "description2", Status.NEW, new ArrayList<>());
        Epic epic3 = new Epic(0, "Epic-3", "description3", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "Description", Status.NEW, 1);
        Subtask sub2 = new Subtask(0, "Sub-2", "Description-2", Status.NEW, 1);

        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);

        // When
        boolean result = manager.removeEpicForId(3);
        // Then
        assertTrue(result);
        assertEquals(2,manager.getEpics().size());
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
        Epic epic1 = new Epic(0, "Epic-1", "description1", Status.NEW, new ArrayList<>());
        Epic epic2 = new Epic(0, "Epic-2", "description2", Status.NEW, new ArrayList<>());
        Epic epic3 = new Epic(0, "Epic-3", "description3", Status.NEW, new ArrayList<>());
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        // When
        boolean result = manager.removeEpicForId(13);
        // Then
        assertEquals(3,manager.getEpics().size());
        assertFalse(result);
        assertNull(manager.getEpicById(13));
    }

    //-----Get History-----//

    @Test
    void shouldGetHistory() {
        // Given
        Epic epic1 = new Epic(0, "Epic-1", "Description", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "Description", Status.NEW, 1);
        Subtask sub2 = new Subtask(0, "Sub-2", "Description-2", Status.NEW, 1);
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        Task task1 = new Task(0, "Task-1", "Description-1", Status.NEW);
        Task task2 = new Task(0, "Task-2", "Description-2", Status.NEW);
        Task task3 = new Task(0, "Task-3", "Description-3", Status.NEW);
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
        assertEquals(List.of(task1,task2,epic1,sub2,sub1,task3),result);

    }

    @Test
    void shouldGetSubtaskForEpic() {
        // Given
        Epic epic = new Epic(0, "EPIC", "AAAbbbYYY", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "sub-1", "something", Status.NEW, 1);
        Subtask sub2 = new Subtask(0, "Sub-2", "something", Status.NEW, 1);
        Subtask sub3 = new Subtask(0,"Sub3","something",Status.NEW,1);
        manager.createEpic(epic);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);
        List<Subtask> check =new ArrayList<>(List.of(sub1,sub2,sub3));
        // When
       final List<Subtask> result = manager.getSubtasksForEpic(1);
        // Then
        assertEquals(3,result.size());
        assertEquals(check,result);
    }

    @Test
    void shouldGetPrioritizedTasks() {
        // Given
            Task task1 = new Task(0,"task1","--",Status.NEW);
        Task task2 = new Task(0,"task2","--",Status.NEW);
        Epic epic = new Epic(0,"epic-1","--",Status.NEW,new ArrayList<Integer>());
        Subtask sub1 = new Subtask(0,"sub1","---",Status.NEW,1);
        Subtask sub2 = new Subtask(0,"sub2","---",Status.NEW,1);
        task1.setStartTime(LocalDateTime.of(2022,1,1,12,0));
        task1.setDuration(Duration.ofMinutes(40));
        task2.setStartTime(LocalDateTime.of(2022,1,1,15,0));
        task2.setDuration(Duration.ofMinutes(35));
        sub1.setStartTime(LocalDateTime.of(2022,1,1,18,0));
        sub1.setDuration(Duration.ofMinutes(80));
        sub2.setStartTime(LocalDateTime.of(2022,1,1,21,0));
        sub2.setDuration(Duration.ofMinutes(15));
        List<Task> check = new ArrayList<Task>(List.of(task1,task2,sub1,sub2));
        manager.createEpic(epic);
        manager.createSubtask(sub1);
        manager.createTask(task2);
        manager.createSubtask(sub2);
        manager.createTask(task1);
        // When
        List<Task> result = manager.getPrioritizedTasks();
        // Then
        assertEquals(check,result);
        assertEquals(task1,result.get(0));
    }
    @Test
    void getPrioritizedTasksWhenEmptyList() {
        // Given

        // When
       List<Task> result = manager.getPrioritizedTasks();
        // Then
        assertEquals(new ArrayList<Task>(),result);
    }
    @Test
    void getPrioritizedTasksWhenNoTimeOfSubtask() {
        // Given
        Task task2 = new Task(0,"task2","--",Status.NEW);
        Epic epic = new Epic(0,"epic-1","--",Status.NEW,new ArrayList<Integer>());
        Subtask sub1 = new Subtask(0,"sub1","---",Status.NEW,1);
        Subtask sub2 = new Subtask(0,"sub2","---",Status.NEW,1);
        sub2.setStartTime(LocalDateTime.of(2023,4,13,12,0));
        sub2.setDuration(Duration.ofMinutes(25));
        manager.createEpic(epic);
        manager.createTask(task2);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        List<Task> check = new ArrayList<>(List.of(sub2,task2,sub1));
        // When
        List<Task> result = manager.getPrioritizedTasks();
        // Then
        assertEquals(check.get(0),result.get(0));
        assertEquals(check,result);
    }
    @Test
    void shouldExceptTaskIntersectionExceptionWhenIntersection() {
        // Given
        Task task01 = new Task(0, "Wake up", "ТАСК 1", Status.NEW);
        Task task02 = new Task(0, "Read news", "ТАСК 2", Status.NEW);
        task01.setStartTime(LocalDateTime.of(2022,1,1,12,0));
        task01.setDuration(Duration.ofMinutes(100));
        task02.setStartTime(LocalDateTime.of(2022,1,1,13,0));
       task02.setDuration(Duration.ofMinutes(15));
        // When
        manager.createTask(task01);
        // Then
        Assertions.assertThrows(TaskIntersectionException.class, () -> manager.createTask(task02));
    }

}