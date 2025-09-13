import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        Task task = new Task("Test Task", "Test Description", Status.NEW);
        Task created = taskManager.createTask(task);

        Optional<Task> retrieved = taskManager.getTaskById(created.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(created, retrieved.get());
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("Test Task", "Test Description", Status.NEW);
        Task created = taskManager.createTask(task);

        created.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(created);

        Optional<Task> updated = taskManager.getTaskById(created.getId());
        assertTrue(updated.isPresent());
        assertEquals(Status.IN_PROGRESS, updated.get().getStatus());
    }

    @Test
    void shouldDeleteTask() {
        Task task = new Task("Test Task", "Test Description", Status.NEW);
        Task created = taskManager.createTask(task);

        taskManager.deleteTaskById(created.getId());

        Optional<Task> deleted = taskManager.getTaskById(created.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void shouldCreateEpicWithSubtasks() {
        Epic epic = new Epic("Test Epic", "Test Description");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.NEW, createdEpic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.DONE, createdEpic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(createdEpic.getId());
        assertEquals(2, subtasks.size());
        assertEquals(Status.IN_PROGRESS, createdEpic.getStatus());
    }

    @Test
    void shouldCalculateEpicTime() {
        Epic epic = new Epic("Test Epic", "Test Description");
        Epic createdEpic = taskManager.createEpic(epic);

        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Status.NEW, createdEpic.getId(),
                duration, startTime);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Status.DONE, createdEpic.getId(),
                Duration.ofHours(2), startTime.plusHours(2));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(startTime, createdEpic.getStartTime());
        assertEquals(startTime.plusHours(4), createdEpic.getEndTime());
        assertEquals(Duration.ofHours(3), createdEpic.getDuration());
    }

    @Test
    void shouldGetPrioritizedTasks() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task 1", "Description", Status.NEW,
                Duration.ofHours(1), now.plusHours(2));
        Task task2 = new Task("Task 2", "Description", Status.NEW,
                Duration.ofHours(1), now.plusHours(1));
        Task task3 = new Task("Task 3", "Description", Status.NEW,
                Duration.ofHours(1), now);

        taskManager.createTask(task3);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(3, prioritized.size());
        assertEquals(now, prioritized.get(0).getStartTime());
        assertEquals(now.plusHours(2), prioritized.get(2).getStartTime());
    }

    @Test
    void shouldDetectTimeOverlap() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task 1", "Description", Status.NEW,
                Duration.ofHours(2), now);
        Task task2 = new Task("Task 2", "Description", Status.NEW,
                Duration.ofHours(2), now.plusHours(1)); // Пересекается с task1

        taskManager.createTask(task1);

        assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void shouldNotIncludeTasksWithoutStartTimeInPrioritized() {
        Task taskWithTime = new Task("With Time", "Description", Status.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        Task taskWithoutTime = new Task("Without Time", "Description", Status.NEW);

        taskManager.createTask(taskWithTime);
        taskManager.createTask(taskWithoutTime);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(1, prioritized.size());
        assertEquals(taskWithTime.getId(), prioritized.get(0).getId());
    }

    @Test
    void shouldHandleEpicStatusScenarios() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1", "Description"));
        taskManager.createSubtask(new Subtask("Sub 1", "Desc", Status.NEW, epic1.getId()));
        taskManager.createSubtask(new Subtask("Sub 2", "Desc", Status.NEW, epic1.getId()));
        assertEquals(Status.NEW, epic1.getStatus());

        Epic epic2 = taskManager.createEpic(new Epic("Epic 2", "Description"));
        taskManager.createSubtask(new Subtask("Sub 1", "Desc", Status.DONE, epic2.getId()));
        taskManager.createSubtask(new Subtask("Sub 2", "Desc", Status.DONE, epic2.getId()));
        assertEquals(Status.DONE, epic2.getStatus());

        Epic epic3 = taskManager.createEpic(new Epic("Epic 3", "Description"));
        taskManager.createSubtask(new Subtask("Sub 1", "Desc", Status.NEW, epic3.getId()));
        taskManager.createSubtask(new Subtask("Sub 2", "Desc", Status.DONE, epic3.getId()));
        assertEquals(Status.IN_PROGRESS, epic3.getStatus());

        Epic epic4 = taskManager.createEpic(new Epic("Epic 4", "Description"));
        taskManager.createSubtask(new Subtask("Sub 1", "Desc", Status.IN_PROGRESS, epic4.getId()));
        taskManager.createSubtask(new Subtask("Sub 2", "Desc", Status.IN_PROGRESS, epic4.getId()));
        assertEquals(Status.IN_PROGRESS, epic4.getStatus());
    }
}