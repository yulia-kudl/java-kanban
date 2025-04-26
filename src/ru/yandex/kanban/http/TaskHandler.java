package ru.yandex.kanban.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.IntersectionException;
import ru.yandex.kanban.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        EndPoint endPoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endPoint) {
            case GET_TASKS:
                handleGetTasks(exchange);
                break;
            case GET_TASK_BY_ID:
                handleGetTaskByID(exchange);
                break;
            case CREATE_UPDATE_TASK:
                handleCreateOrUpdateTask(exchange);
                break;

            case DELETE_TASK:
                handleDeleteTask(exchange);
                break;
            default:
                sendNotFound(exchange);
        }


    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        taskManager.deleteTask(id);
        sendText(exchange, "task deleted", 200);

    }


    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        try {
            if (task.getId() == 0) {
                taskManager.createTask(task);
                sendText(exchange, "", 201);

            } else {
                taskManager.updateTask(task);
                sendText(exchange, "", 201);
            }
        }
        catch (IntersectionException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleGetTaskByID(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        try {
            Task task = taskManager.getTaskById(id);
            String taskJson = gson.toJson(task);
            sendText(exchange, taskJson, 200);
        }
        catch ( NoSuchElementException exception) {
            sendNotFound(exchange);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void handleGetTasks(HttpExchange exchange) {
        List<Task> tasks = taskManager.getTasks();
        try {
            String tasksJson = gson.toJson(tasks);
            sendText(exchange, tasksJson, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
