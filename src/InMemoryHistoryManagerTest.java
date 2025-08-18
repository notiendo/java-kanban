import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
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
        assertEquals(6, history.get(0).getId()); // Самый старый
        assertEquals(15, history.get(9).getId()); // Самый новый
    }
}