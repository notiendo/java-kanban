import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
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
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Subtask", Status.NEW, epic.getId());
        subtask.setEpicId(subtask.getId());

        assertThrows(IllegalArgumentException.class, () -> taskManager.createSubtask(subtask));
    }

    @Test
    void shouldRemoveTaskFromHistoryWhenDeleted() {
        Task task = taskManager.createTask(new Task("Task", "Desc", Status.NEW));
        taskManager.getTaskById(task.getId());

        taskManager.deleteTaskById(task.getId());

        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldRemoveEpicAndSubtasksFromHistoryWhenDeleted() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Desc"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Desc", Status.NEW, epic.getId()));

        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());

        taskManager.deleteEpicById(epic.getId());

        assertTrue(taskManager.getHistory().isEmpty());
    }
}