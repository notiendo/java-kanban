import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (path.endsWith("/subtasks")) {
                handleGetEpicSubtasks(exchange, path);
                return;
            }

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
            Epic epic = taskManager.getEpicById(idOpt.get());
            if (epic != null) {
                sendSuccess(exchange, epic);
            } else {
                sendNotFound(exchange, "Эпик с id=" + idOpt.get() + " не найдена");
            }
        } else {
            sendSuccess(exchange, taskManager.getAllEpics());
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, String path) throws IOException {
        String[] parts = path.split("/");
        if (parts.length >= 3) {
            try {
                int epicId = Integer.parseInt(parts[2]);
                Epic epic = taskManager.getEpicById(epicId);
                if (epic != null) {
                    sendSuccess(exchange, taskManager.getSubtasksByEpicId(epicId));
                } else {
                    sendNotFound(exchange, "Эпик с id=" + epicId + " не найден");
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange, "Неверный формат id эпика");
            }
        } else {
            sendBadRequest(exchange, "Неверный путь запроса");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Epic epic = gson.fromJson(body, Epic.class);

        if (epic.getId() == 0) {
            Epic createdEpic = taskManager.createEpic(epic);
            sendCreated(exchange, createdEpic);
        } else {
            taskManager.updateEpic(epic);
            sendSuccess(exchange, epic);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(path);

        if (idOpt.isPresent()) {
            Epic epic = taskManager.getEpicById(idOpt.get());
            if (epic != null) {
                taskManager.deleteEpicById(idOpt.get());
                sendSuccess(exchange, "Эпик удален");
            } else {
                sendNotFound(exchange, "Эпик с id=" + idOpt.get() + " не найден");
            }
        } else {
            taskManager.deleteAllEpics();
            sendSuccess(exchange, "Все эпики удалены");
        }
    }
}