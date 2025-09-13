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

    @Test
    void shouldHandleRemovalFromBeginning() {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("Task 2", "Desc", Status.IN_PROGRESS);
        task2.setId(2);

        Task task3 = new Task("Task 3", "Desc", Status.DONE);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    void shouldHandleRemovalFromEnd() {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("Task 2", "Desc", Status.IN_PROGRESS);
        task2.setId(2);

        Task task3 = new Task("Task 3", "Desc", Status.DONE);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(3);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void shouldHandleEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldHandleRemovalOfNonExistentTask() {
        Task task = new Task("Test", "Test", Status.NEW);
        task.setId(1);

        historyManager.add(task);
        historyManager.remove(999);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void shouldMaintainHistoryAfterMultipleOperations() {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Desc", Status.IN_PROGRESS);
        task2.setId(2);
        Task task3 = new Task("Task 3", "Desc", Status.DONE);
        task3.setId(3);
        Task task4 = new Task("Task 4", "Desc", Status.NEW);
        task4.setId(4);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());

        historyManager.remove(2);
        history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));

        historyManager.add(task4);
        history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
        assertEquals(task4, history.get(2));

        historyManager.add(task1);
        history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task3, history.get(0));
        assertEquals(task4, history.get(1));
        assertEquals(task1, history.get(2));

        historyManager.remove(3);
        history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task4, history.get(0));
        assertEquals(task1, history.get(1));

        historyManager.remove(1);
        history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task4, history.get(0));
    }

    @Test
    void shouldHandleTasksWithSameIdButDifferentContent() {
        Task task1 = new Task("Original", "Description", Status.NEW);
        task1.setId(1);

        Task task2 = new Task("Modified", "Different Description", Status.IN_PROGRESS);
        task2.setId(1);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals("Modified", history.get(0).getName());
        assertEquals("Different Description", history.get(0).getDescription());
        assertEquals(Status.IN_PROGRESS, history.get(0).getStatus());
    }

    @Test
    void shouldHandleMixedTaskTypes() {
        Task task = new Task("Regular Task", "Description", Status.NEW);
        task.setId(1);

        Epic epic = new Epic("Epic Task", "Epic Description");
        epic.setId(2);

        Subtask subtask = new Subtask("Subtask", "Subtask Description", Status.NEW, 2);
        subtask.setId(3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task, history.get(0));
        assertEquals(epic, history.get(1));
        assertEquals(subtask, history.get(2));
    }

    @Test
    void shouldNotBreakWhenRemovingFromEmptyHistory() {
        assertDoesNotThrow(() -> historyManager.remove(1));
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldHandleConsecutiveRemovals() {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Desc", Status.IN_PROGRESS);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);
        historyManager.remove(2);

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldMaintainCorrectOrderAfterComplexOperations() {

        Task[] tasks = new Task[5];
        for (int i = 0; i < 5; i++) {
            tasks[i] = new Task("Task " + (i + 1), "Description", Status.NEW);
            tasks[i].setId(i + 1);
        }

        for (Task task : tasks) {
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        for (int i = 0; i < 5; i++) {
            assertEquals(i + 1, history.get(i).getId());
        }

        historyManager.remove(3);
        history = historyManager.getHistory();
        assertEquals(4, history.size());
        assertEquals(1, history.get(0).getId());
        assertEquals(2, history.get(1).getId());
        assertEquals(4, history.get(2).getId());
        assertEquals(5, history.get(3).getId());

        historyManager.add(tasks[1]);
        history = historyManager.getHistory();
        assertEquals(4, history.size());
        assertEquals(1, history.get(0).getId());
        assertEquals(4, history.get(1).getId());
        assertEquals(5, history.get(2).getId());
        assertEquals(2, history.get(3).getId());

        historyManager.remove(1);
        history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(4, history.get(0).getId());
        assertEquals(5, history.get(1).getId());
        assertEquals(2, history.get(2).getId());

        Task task6 = new Task("Task 6", "Description", Status.NEW);
        task6.setId(6);
        historyManager.add(task6);
        history = historyManager.getHistory();
        assertEquals(4, history.size());
        assertEquals(4, history.get(0).getId());
        assertEquals(5, history.get(1).getId());
        assertEquals(2, history.get(2).getId());
        assertEquals(6, history.get(3).getId());
    }
}