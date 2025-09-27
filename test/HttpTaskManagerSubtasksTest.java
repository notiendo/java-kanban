import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerSubtasksTest extends HttpTaskManagerTest {

    @Test
    void testGetAllSubtasks() throws Exception {

        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Desc 1", Status.NEW, createdEpic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Desc 2", Status.IN_PROGRESS, createdEpic.getId());

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertNotNull(subtasks);
        assertEquals(2, subtasks.length);
    }

    @Test
    void testGetSubtaskById() throws Exception {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = manager.createEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Description", Status.NEW, createdEpic.getId());
        Subtask created = manager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + created.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(responseSubtask);
        assertEquals("Test Subtask", responseSubtask.getName());
    }

    @Test
    void testGetSubtaskByIdNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testCreateSubtask() throws Exception {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = manager.createEpic(epic);

        Subtask subtask = new Subtask("New Subtask", "Description", Status.NEW, createdEpic.getId());

        String subtaskJson = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals("New Subtask", subtasks.get(0).getName());
    }

    @Test
    void testCreateSubtaskWithTimeOverlap() throws Exception {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = manager.createEpic(epic);

        LocalDateTime now = LocalDateTime.now();
        Subtask subtask1 = new Subtask("Subtask 1", "Desc", Status.NEW, createdEpic.getId(),
                Duration.ofHours(1), now);
        Subtask subtask2 = new Subtask("Subtask 2", "Desc", Status.NEW, createdEpic.getId(),
                Duration.ofHours(1), now.plusMinutes(30)); // Пересекается

        manager.createSubtask(subtask1);

        String subtaskJson = gson.toJson(subtask2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    void testUpdateSubtask() throws Exception {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = manager.createEpic(epic);

        Subtask subtask = new Subtask("Original", "Desc", Status.NEW, createdEpic.getId());
        Subtask created = manager.createSubtask(subtask);
        created.setName("Updated");

        String subtaskJson = gson.toJson(created);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Subtask updated = manager.getSubtaskById(created.getId());
        assertEquals("Updated", updated.getName());
    }

    @Test
    void testDeleteSubtask() throws Exception {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = manager.createEpic(epic);

        Subtask subtask = new Subtask("To Delete", "Desc", Status.NEW, createdEpic.getId());
        Subtask created = manager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + created.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getSubtaskById(created.getId()));
    }

    @Test
    void testDeleteAllSubtasks() throws Exception {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Desc", Status.NEW, createdEpic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Desc", Status.NEW, createdEpic.getId());

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }
}