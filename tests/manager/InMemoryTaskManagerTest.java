package manager;


import taskType.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();

    }

    @Test
    void shouldHaveStatusNewWithoutSubs() {
        // Given
        // When
        manager.createEpic(epic1);
        manager.updateEpicStatus(epic1.getId());
        // Then
        assertEquals(Status.NEW, manager.epics.get(epic1.getId()).getStatus());
    }

    @Test
    void shouldHaveStatusNewWithSubs() {
        // Given

        // When
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);
        // Then
        assertEquals(Status.NEW, manager.epics.get(epic1.getId()).getStatus());
    }

    @Test
    public void shouldHaveStatusDone() {
        // Given
        // When
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);
        manager.subtasks.get(sub1.getId()).setStatus(Status.DONE);
        manager.subtasks.get(sub2.getId()).setStatus(Status.DONE);
        manager.subtasks.get(sub3.getId()).setStatus(Status.DONE);
        manager.updateEpicStatus(epic1.getId());
        // Then
        assertEquals(Status.DONE, manager.epics.get(epic1.getId()).getStatus());
    }

    @Test
    public void shouldHaveStatusInProgressWhenSubsAreNewAndDone() {
        // Given
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.subtasks.get(sub1.getId()).setStatus(Status.DONE);
        // When
        manager.updateEpicStatus(epic1.getId());
        // Then
        assertEquals(Status.IN_PROGRESS, manager.epics.get(epic1.getId()).getStatus());
    }

    @Test
    public void shouldHaveStatusInProgressWhenSubsAreIn_Progress() {
        // Given
        manager.createEpic(epic1);
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.subtasks.get(sub1.getId()).setStatus(Status.IN_PROGRESS);
        manager.subtasks.get(sub2.getId()).setStatus(Status.IN_PROGRESS);
        // When
        manager.updateEpicStatus(epic1.getId());
        // Then
        assertEquals(Status.IN_PROGRESS, manager.epics.get(epic1.getId()).getStatus());

    }
}