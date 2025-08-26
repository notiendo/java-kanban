import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Description", Status.NEW);
        task1.setId(1);
        task2 = new Task("Task 2", "Description", Status.IN_PROGRESS);
        task2.setId(2);
        task3 = new Task("Task 3", "Description", Status.DONE);
        task3.setId(3);
    }

    @Test
    public void shouldAddTaskToHistory() {
        Task task = new Task("Test", "Test", Status.NEW);
        task.setId(1);

        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));
    }

    @Test
    public void shouldNotDuplicateTasksInHistory() {
        Task task = new Task("Test", "Test", Status.NEW);
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void shouldLimitHistorySize() {
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Task " + i, "Desc", Status.NEW);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size());
        assertEquals(6, history.get(0).getId());
        assertEquals(15, history.get(9).getId());
    }

    @Test
    public void shouldAddTasksToHistory() {
        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    public void shouldRemoveDuplicatesWhenAddingSameTask() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    public void shouldRemoveTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertFalse(history.contains(task2));
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    public void shouldMaintainOrderAfterRemoval() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task3, history.get(1));
        assertEquals(task1, history.get(2));
    }
}