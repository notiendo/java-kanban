import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerPrioritizedTest extends HttpTaskManagerTest {

    @Test
    void testGetPrioritizedTasks() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task 1", "Desc", Status.NEW,
                Duration.ofMinutes(30), now.plusHours(2));
        Task task2 = new Task("Task 2", "Desc", Status.NEW,
                Duration.ofMinutes(30), now.plusHours(1));
        Task task3 = new Task("Task 3", "Desc", Status.NEW);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] prioritized = gson.fromJson(response.body(), Task[].class);
        assertNotNull(prioritized);
        assertEquals(3, prioritized.length);

        assertEquals("Task 2", prioritized[0].getName());
        assertEquals("Task 1", prioritized[1].getName());
        assertEquals("Task 3", prioritized[2].getName());
    }

    @Test
    void testGetEmptyPrioritizedList() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] prioritized = gson.fromJson(response.body(), Task[].class);
        assertNotNull(prioritized);
        assertEquals(0, prioritized.length);
    }

    @Test
    void testPrioritizedWithMixedTypes() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        Task task = new Task("Task", "Desc", Status.NEW,
                Duration.ofHours(1), now.plusHours(3));

        Epic epic = new Epic("Epic", "Desc");
        Epic createdEpic = manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, createdEpic.getId(),
                Duration.ofHours(1), now.plusHours(1));

        manager.createTask(task);
        manager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] prioritized = gson.fromJson(response.body(), Task[].class);
        assertNotNull(prioritized);
        assertEquals(2, prioritized.length);

        assertEquals("Subtask", prioritized[0].getName());
        assertEquals("Task", prioritized[1].getName());
    }

    @Test
    void testPrioritizedOrderWithSameStartTime() throws Exception {
        LocalDateTime sameTime = LocalDateTime.now();

        Task task1 = new Task("Task 1", "Desc", Status.NEW,
                Duration.ofMinutes(30), sameTime);
        Task task2 = new Task("Task 2", "Desc", Status.NEW,
                Duration.ofMinutes(30), sameTime.plusHours(1));

        Task created2 = manager.createTask(task2);
        Task created1 = manager.createTask(task1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] prioritized = gson.fromJson(response.body(), Task[].class);

        assertEquals(created1.getId(), prioritized[0].getId());
        assertEquals(created2.getId(), prioritized[1].getId());
    }
}