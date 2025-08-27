import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task("Test", "Test", Status.NEW);
        task.setId(1);

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void shouldNotAddNullTask() {
        historyManager.add(null);

        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveDuplicatesWhenAddingSameTask() {
        Task task = new Task("Test", "Test", Status.NEW);
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task = new Task("Test", "Test", Status.NEW);
        task.setId(1);

        historyManager.add(task);
        historyManager.remove(1);

        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void shouldMaintainOrderOfAddition() {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("Task 2", "Desc", Status.IN_PROGRESS);
        task2.setId(2);

        Task task3 = new Task("Task 3", "Desc", Status.DONE);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }

    @Test
    void shouldHandleLargeNumberOfTasks() {

        for (int i = 1; i <= 100; i++) {
            Task task = new Task("Task " + i, "Description", Status.NEW);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(100, history.size());

        for (int i = 0; i < 100; i++) {
            assertEquals(i + 1, history.get(i).getId());
        }
    }

    @Test
    void shouldMoveTaskToEndWhenReadded() {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("Task 2", "Desc", Status.IN_PROGRESS);
        task2.setId(2);

        Task task3 = new Task("Task 3", "Desc", Status.DONE);
        task3.setId(3);

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

    @Test
    void shouldHandleRemovalFromMiddle() {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("Task 2", "Desc", Status.IN_PROGRESS);
        task2.setId(2);

        Task task3 = new Task("Task 3", "Desc", Status.DONE);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }
}