import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager, Gson gson) {
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
            Task task = taskManager.getTaskById(idOpt.get());
            if (task != null) {
                sendSuccess(exchange, task);
            } else {
                sendNotFound(exchange, "Задача с id=" + idOpt.get() + " не найдена");
            }
        } else {
            sendSuccess(exchange, taskManager.getAllTasks());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Task task = gson.fromJson(body, Task.class);

        try {
            if (task.getId() == 0) {
                Task createdTask = taskManager.createTask(task);
                sendCreated(exchange, createdTask);
            } else {
                taskManager.updateTask(task);
                sendSuccess(exchange, task);
            }
        } catch (IllegalArgumentException e) {
            sendHasOverlaps(exchange, e.getMessage());
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(path);

        if (idOpt.isPresent()) {
            Task task = taskManager.getTaskById(idOpt.get());
            if (task != null) {
                taskManager.deleteTaskById(idOpt.get());
                sendSuccess(exchange, "Задача удалена");
            } else {
                sendNotFound(exchange, "Задача с id=" + idOpt.get() + " не найдена");
            }
        } else {
            taskManager.deleteAllTasks();
            sendSuccess(exchange, "Все задачи удалены");
        }
    }
}