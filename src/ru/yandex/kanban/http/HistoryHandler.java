package ru.yandex.kanban.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equals("GET") &&
                exchange.getRequestURI().getPath().split("/").length == 2) {
            List<Task> history = taskManager.getHistoryForTaskManager();
            String taskJson = gson.toJson(history);
            sendText(exchange, taskJson, 200);
        } else
            sendNotFound(exchange);
    }
}
