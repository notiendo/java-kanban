import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                sendSuccess(exchange, taskManager.getHistory());
            } else {
                sendBadRequest(exchange, "Неверный HTTP метод: " + exchange.getRequestMethod());
            }
        } catch (Exception e) {
            sendInternalError(exchange, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}