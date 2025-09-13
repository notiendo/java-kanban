import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
    }

    @Test
    void shouldPreserveTaskEqualityById() {
        Task task1 = new Task("A", "A", Status.NEW);
        Task task2 = new Task("B", "B", Status.IN_PROGRESS);
        task2.setId(task1.getId());

        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
    }

    @Test
    void shouldNotAllowEpicAsOwnSubtask() {
        Epic epic = new Epic("Epic", "Epic");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Subtask", Status.NEW, epic.getId());
        subtask.setEpicId(subtask.getId());

        assertThrows(IllegalArgumentException.class, () -> manager.createSubtask(subtask));
    }

    @Test
    void shouldRemoveTaskFromHistoryWhenDeleted() {
        Task task = manager.createTask(new Task("Task", "Desc", Status.NEW));
        manager.getTaskById(task.getId());

        manager.deleteTaskById(task.getId());

        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldRemoveEpicAndSubtasksFromHistoryWhenDeleted() {
        Epic epic = manager.createEpic(new Epic("Epic", "Desc"));
        Subtask subtask = manager.createSubtask(new Subtask("Subtask", "Desc", Status.NEW, epic.getId()));

        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        manager.deleteEpicById(epic.getId());

        assertTrue(manager.getHistory().isEmpty());
    }
}