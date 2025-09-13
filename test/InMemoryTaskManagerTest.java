import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void testPrioritizedTasks() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofHours(1), now.plusHours(2));
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofHours(1), now.plusHours(1));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertEquals("Task2", prioritized.get(0).getName());
    }

    @Test
    public void testTaskWithoutTimeNotInPrioritized() {
        Task task = new Task("Task", "Desc", Status.NEW);
        taskManager.createTask(task);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertTrue(prioritized.isEmpty());
    }
}