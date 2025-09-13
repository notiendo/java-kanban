import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = Files.createTempFile("tasks", ".csv").toFile();
            tempFile.deleteOnExit();
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp file", e);
        }
    }

    @Test
    void shouldSaveAndLoadTasks() throws IOException {
        Task task = new Task("Test Task", "Description", Status.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Optional<Task> loadedTask = loadedManager.getTaskById(task.getId());

        assertTrue(loadedTask.isPresent());
        assertEquals(task.getName(), loadedTask.get().getName());
        assertEquals(task.getDuration(), loadedTask.get().getDuration());
    }

    @Test
    void shouldSaveAndLoadEpicWithSubtasks() {
        Epic epic = new Epic("Test Epic", "Description");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, createdEpic.getId(),
                Duration.ofHours(2), LocalDateTime.now());
        taskManager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Optional<Epic> loadedEpic = loadedManager.getEpicById(createdEpic.getId());

        assertTrue(loadedEpic.isPresent());
        assertEquals(1, loadedEpic.get().getSubtaskIds().size());
    }

    @Test
    void shouldHandleEmptyFile() throws IOException {
        Files.write(tempFile.toPath(), new byte[0]);

        assertDoesNotThrow(() -> {
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
            assertTrue(loadedManager.getAllTasks().isEmpty());
        });
    }

    @Test
    void shouldThrowExceptionWhenFileNotFound() {
        File nonExistentFile = new File("nonexistent.csv");

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(nonExistentFile);
        });
    }
}