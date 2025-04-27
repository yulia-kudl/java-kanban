package ru.yandex.kanban.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.Epic;
import ru.yandex.kanban.SubTask;
import ru.yandex.kanban.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        TaskHandler.EndPoint endPoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endPoint) {
            case GET_TASKS:
                handleGetEpics(exchange);
                break;
            case GET_TASK_BY_ID:
                handleGetEpicByID(exchange);
                break;
            case CREATE_UPDATE_TASK:
                handleCreateOrUpdateEpic(exchange);
                break;

            case DELETE_TASK:
                handleDeleteEpic(exchange);
                break;

            case EPIC_SUBTASKS:
                handleGetEpicSubtasks(exchange);
                break;

            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        try {
            List<SubTask> subtasks = taskManager.getSubTasksByEpic(id);
            String taskJson = gson.toJson(subtasks);
            sendText(exchange, taskJson, 200);
        } catch (NoSuchElementException e) {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        taskManager.deleteEpic(id);
        sendText(exchange, "epic deleted", 200);

    }

    private void handleCreateOrUpdateEpic(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);
        if (epic.getId() == 0) {
            taskManager.createEpic(epic);
            sendText(exchange, "", 201);

        } else {
            taskManager.updateEpic(epic);
            sendText(exchange, "", 201);
        }
    }

    private void handleGetEpicByID(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        try {
            Task task = taskManager.getEpicTaskById(id);
            String taskJson = gson.toJson(task);
            sendText(exchange, taskJson, 200);
        } catch (NoSuchElementException exception) {
            sendNotFound(exchange);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGetEpics(HttpExchange exchange) {
        List<Epic> epics = taskManager.getEpics();
        try {
            String tasksJson = gson.toJson(epics);
            sendText(exchange, tasksJson, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
