package ru.yandex.kanban.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equals("GET") &&
                exchange.getRequestURI().getPath().split("/").length == 2) {
            List<Task> tasksPrioritized = taskManager.getPrioritizedTasks();
            String taskJson = gson.toJson(tasksPrioritized);
            sendText(exchange, taskJson, 200);

        } else
            sendNotFound(exchange);
    }
}
