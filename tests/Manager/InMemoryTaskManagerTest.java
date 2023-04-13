package Manager;

import TaskType.Epic;
import TaskType.Status;
import TaskType.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.security.spec.ECPoint;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();

    }

    @Test
    void shouldHaveStatusNewWithoutSubs() {
        // given
        Epic epic01 = new Epic(0, "Epic-1", "Epic without subs", Status.NEW, new ArrayList<>());
        // when
        manager.createEpic(epic01);
        manager.updateEpicStatus(epic01.getId());
        // then
        Assertions.assertEquals(Status.NEW, manager.epics.get(epic01.getId()).getStatus());
    }

    @Test
    void shouldHaveStatusNewWithSubs() {
        // given
        Epic epic01 = new Epic(0, "Epic-1", "Epic without subs", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "First subtask for Epic-1", Status.NEW, 1);
        Subtask sub2 = new Subtask(0, "Sub-2", "Second subtask for Epic-1", Status.NEW, 1);
        Subtask sub3 = new Subtask(0, "Sub-3", "Third subtask for Epic-1", Status.NEW, 1);

        // when
        manager.createEpic(epic01);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);

        // then
        Assertions.assertEquals(Status.NEW, manager.epics.get(epic01.getId()).getStatus());
    }

    @Test
    public void shouldHaveStatusDone() {
        // given
        Epic epic01 = new Epic(0, "Epic-1", "Epic without subs", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "First subtask for Epic-1", Status.NEW, 1);
        Subtask sub2 = new Subtask(0, "Sub-2", "Second subtask for Epic-1", Status.NEW, 1);
        Subtask sub3 = new Subtask(0, "Sub-3", "Third subtask for Epic-1", Status.NEW, 1);

        // when
        manager.createEpic(epic01);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);
        manager.subtasks.get(sub1.getId()).setStatus(Status.DONE);
        manager.subtasks.get(sub2.getId()).setStatus(Status.DONE);
        manager.subtasks.get(sub3.getId()).setStatus(Status.DONE);
        manager.updateEpicStatus(epic01.getId());

        // then
        Assertions.assertEquals(Status.DONE, manager.epics.get(epic01.getId()).getStatus());
    }

    @Test
    public void shouldHaveStatusInProgressWhenSubsAreNewAndDone() {
        // Given
        Epic epic01 = new Epic(0, "Epic-1", "Epic without subs", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "First subtask for Epic-1", Status.NEW, 1);
        Subtask sub2 = new Subtask(0, "Sub-2", "Second subtask for Epic-1", Status.NEW, 1);
        manager.createEpic(epic01);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.subtasks.get(sub1.getId()).setStatus(Status.DONE);
        // When
        manager.updateEpicStatus(epic01.getId());
        // Then
        Assertions.assertEquals(Status.IN_PROGRESS, manager.epics.get(epic01.getId()).getStatus());
    }

    @Test
    public void shouldHaveStatusInProgressWhenSubsAreIn_Progress() {
        // Given
        Epic epic01 = new Epic(0, "Epic-1", "Epic without subs", Status.NEW, new ArrayList<>());
        Subtask sub1 = new Subtask(0, "Sub-1", "First subtask for Epic-1", Status.NEW, 1);
        Subtask sub2 = new Subtask(0, "Sub-2", "Second subtask for Epic-1", Status.NEW, 1);
        // When
        manager.createEpic(epic01);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.subtasks.get(sub1.getId()).setStatus(Status.IN_PROGRESS);
        manager.subtasks.get(sub2.getId()).setStatus(Status.IN_PROGRESS);
        manager.updateEpicStatus(epic01.getId());
        // Then
        Assertions.assertEquals(Status.IN_PROGRESS, manager.epics.get(epic01.getId()).getStatus());

    }
}