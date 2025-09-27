import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final Gson gson;
    protected final TaskManager taskManager;

    protected BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendJson(HttpExchange exchange, Object object, int statusCode) throws IOException {
        String json = gson.toJson(object);
        sendText(exchange, json, statusCode);
    }

    protected void sendSuccess(HttpExchange exchange, Object object) throws IOException {
        sendJson(exchange, object, 200);
    }

    protected void sendCreated(HttpExchange exchange, Object object) throws IOException {
        sendJson(exchange, object, 201);
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, "{\"message\":\"" + message + "\"}", 404);
    }

    protected void sendHasOverlaps(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, "{\"message\":\"" + message + "\"}", 406);
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, "{\"message\":\"" + message + "\"}", 400);
    }

    protected void sendInternalError(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, "{\"message\":\"" + message + "\"}", 500);
    }

    protected String readBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    protected Optional<Integer> extractIdFromPath(String path) {
        try {
            String[] pathParts = path.split("/");
            if (pathParts.length > 0) {
                return Optional.of(Integer.parseInt(pathParts[pathParts.length - 1]));
            }
        } catch (NumberFormatException e) {
            System.err.println("Некорректный формат ID в пути: " + path);
        }
        return Optional.empty();
    }
}