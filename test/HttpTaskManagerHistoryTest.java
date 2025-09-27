import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerHistoryTest extends HttpTaskManagerTest {

    @Test
    void testGetHistory() throws Exception {
        Task task = new Task("Task", "Description", Status.NEW);
        Epic epic = new Epic("Epic", "Description");

        Task createdTask = manager.createTask(task);
        Epic createdEpic = manager.createEpic(epic);

        manager.getTaskById(createdTask.getId());
        manager.getEpicById(createdEpic.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertNotNull(history);
        assertEquals(2, history.length);
    }

    @Test
    void testGetEmptyHistory() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertNotNull(history);
        assertEquals(0, history.length);
    }

    @Test
    void testHistoryOrder() throws Exception {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        Task task2 = new Task("Task 2", "Desc", Status.NEW);
        Task task3 = new Task("Task 3", "Desc", Status.NEW);

        Task created1 = manager.createTask(task1);
        Task created2 = manager.createTask(task2);
        Task created3 = manager.createTask(task3);

        manager.getTaskById(created1.getId());
        manager.getTaskById(created2.getId());
        manager.getTaskById(created3.getId());

        manager.getTaskById(created1.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] history = gson.fromJson(response.body(), Task[].class);

        assertEquals(created2.getId(), history[0].getId());
        assertEquals(created3.getId(), history[1].getId());
        assertEquals(created1.getId(), history[2].getId());
    }

    @Test
    void testHistoryAfterDeletion() throws Exception {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        Task task2 = new Task("Task 2", "Desc", Status.NEW);

        Task created1 = manager.createTask(task1);
        Task created2 = manager.createTask(task2);

        manager.getTaskById(created1.getId());
        manager.getTaskById(created2.getId());

        // Удаляем первую задачу
        manager.deleteTaskById(created1.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(1, history.length);
        assertEquals(created2.getId(), history[0].getId());
    }
}