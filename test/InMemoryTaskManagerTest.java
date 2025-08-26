import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        subtask.setEpicId(subtask.getId()); // Пытаемся сделать эпиком себя

        assertThrows(IllegalArgumentException.class, () -> manager.createSubtask(subtask));
    }
}