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