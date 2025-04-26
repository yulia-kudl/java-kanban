package ru.yandex.kanban.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.TaskManager;

import java.io.IOException;
import java.io.OutputStream;


public class BaseHttpHandler implements HttpHandler {
    protected static TaskManager taskManager;
    protected static Gson gson;

    enum EndPoint {GET_TASKS, GET_TASK_BY_ID, CREATE_UPDATE_TASK, DELETE_TASK, EPIC_SUBTASKS, UNKNOWN}

    public BaseHttpHandler() {

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }

    protected void sendText(HttpExchange httpExchange, String text, int respCode) throws IOException {
        httpExchange.sendResponseHeaders(respCode, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    protected void sendNotFound(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(404, -1);
    }

    protected void sendHasInteractions(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(406, -1);

    }

    EndPoint getEndpoint(String path, String requestMethod) {
        String[] pathParts = path.split("/");

        if (pathParts.length == 2) {
            if (requestMethod.equals("GET")) {
                return EndPoint.GET_TASKS;   //  GET /TASKS
            }
            if (requestMethod.equals("POST")) {    //POST /TASKS
                return EndPoint.CREATE_UPDATE_TASK;
            }
        }

        if (pathParts.length == 3 && pathParts[2].matches("\\d+")) {
            if (requestMethod.equals("GET")) {
                return EndPoint.GET_TASK_BY_ID;   // GET /tasks/{id}
            }
            if (requestMethod.equals("DELETE")) {   // DELETE /tasks/{id}
                return EndPoint.DELETE_TASK;
            }
        }

        if (pathParts.length == 4 && pathParts[2].matches("\\d+") && requestMethod.equals("GET")
                && pathParts[1].equals("epics")) {
            return EndPoint.EPIC_SUBTASKS;
        }

        return EndPoint.UNKNOWN;
    }

    int getTaskId(HttpExchange exchange) {
        return Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
    }


}
