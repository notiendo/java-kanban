import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendBadRequest(exchange, "Неверный HTTP метод: " + method);
            }
        } catch (Exception e) {
            sendInternalError(exchange, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(path);

        if (idOpt.isPresent()) {
            Subtask subtask = taskManager.getSubtaskById(idOpt.get());
            if (subtask != null) {
                sendSuccess(exchange, subtask);
            } else {
                sendNotFound(exchange, "Подзадача с id=" + idOpt.get() + " не найдена");
            }
        } else {
            sendSuccess(exchange, taskManager.getAllSubtasks());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        try {
            if (subtask.getId() == 0) {
                Subtask createdSubtask = taskManager.createSubtask(subtask);
                sendCreated(exchange, createdSubtask);
            } else {
                taskManager.updateSubtask(subtask);
                sendSuccess(exchange, subtask);
            }
        } catch (IllegalArgumentException e) {
            sendHasOverlaps(exchange, e.getMessage());
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(path);

        if (idOpt.isPresent()) {
            Subtask subtask = taskManager.getSubtaskById(idOpt.get());
            if (subtask != null) {
                taskManager.deleteSubtaskById(idOpt.get());
                sendSuccess(exchange, "Подзадача удалена");
            } else {
                sendNotFound(exchange, "Подзадача с id=" + idOpt.get() + " не найдена");
            }
        } else {
            taskManager.deleteAllSubtasks();
            sendSuccess(exchange, "Все подзадачи удалены");
        }
    }
}