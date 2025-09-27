import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerEpicsTest extends HttpTaskManagerTest {

    @Test
    void testGetAllEpics() throws Exception {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertNotNull(epics);
        assertEquals(2, epics.length);
    }

    @Test
    void testGetEpicById() throws Exception {
        Epic epic = new Epic("Test Epic", "Description");
        Epic created = manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + created.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(responseEpic);
        assertEquals("Test Epic", responseEpic.getName());
    }

    @Test
    void testGetEpicByIdNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testGetEpicSubtasks() throws Exception {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Desc 1", Status.NEW, createdEpic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Desc 2", Status.DONE, createdEpic.getId());

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + createdEpic.getId() + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertNotNull(subtasks);
        assertEquals(2, subtasks.length);
    }

    @Test
    void testGetEpicSubtasksNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testCreateEpic() throws Exception {
        Epic epic = new Epic("New Epic", "Description");

        String epicJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size());
        assertEquals("New Epic", epics.get(0).getName());
    }

    @Test
    void testUpdateEpic() throws Exception {
        Epic epic = new Epic("Original", "Desc");
        Epic created = manager.createEpic(epic);
        created.setName("Updated");

        String epicJson = gson.toJson(created);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic updated = manager.getEpicById(created.getId());
        assertEquals("Updated", updated.getName());
    }

    @Test
    void testDeleteEpic() throws Exception {
        Epic epic = new Epic("To Delete", "Desc");
        Epic created = manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + created.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getEpicById(created.getId()));
    }

    @Test
    void testDeleteEpicWithSubtasks() throws Exception {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, createdEpic.getId());
        manager.createSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + createdEpic.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getEpicById(createdEpic.getId()));
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void testDeleteAllEpics() throws Exception {
        Epic epic1 = new Epic("Epic 1", "Desc");
        Epic epic2 = new Epic("Epic 2", "Desc");

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }
}