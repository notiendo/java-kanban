import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void shouldCreateAndGetTask() {
        Task task = new Task("Test", "Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        Task created = taskManager.createTask(task);

        assertNotNull(created.getId());
        assertEquals(task.getName(), created.getName());
    }

    @Test
    void shouldDetectTimeOverlap() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), now);
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(30), now.plusMinutes(30));

        taskManager.createTask(task1);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createTask(task2);
        });
    }

    @Test
    void shouldReturnPrioritizedTasks() {
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = time1.plusHours(1);

        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(30), time2);
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(45), time1);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertEquals("Task2", prioritized.get(0).getName());
        assertEquals("Task1", prioritized.get(1).getName());
    }

    @Test
    void shouldCalculateEpicStatusAllNew() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask1 = taskManager.createSubtask(
                new Subtask("Sub1", "Desc", Status.NEW, epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(
                new Subtask("Sub2", "Desc", Status.NEW, epic.getId()));

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusAllDone() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask1 = taskManager.createSubtask(
                new Subtask("Sub1", "Desc", Status.DONE, epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(
                new Subtask("Sub2", "Desc", Status.DONE, epic.getId()));

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusMixed() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask1 = taskManager.createSubtask(
                new Subtask("Sub1", "Desc", Status.NEW, epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(
                new Subtask("Sub2", "Desc", Status.DONE, epic.getId()));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldCalculateEpicStatusInProgress() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask1 = taskManager.createSubtask(
                new Subtask("Sub1", "Desc", Status.IN_PROGRESS, epic.getId()));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldHandleEmptyHistory() {
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldRemoveFromHistoryWhenDeleted() {
        Task task = taskManager.createTask(new Task("Task", "Desc", Status.NEW));
        taskManager.getTaskById(task.getId());
        taskManager.deleteTaskById(task.getId());

        assertTrue(taskManager.getHistory().isEmpty());
    }
}