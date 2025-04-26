package ru.yandex.kanban.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.kanban.IntersectionException;
import ru.yandex.kanban.SubTask;
import ru.yandex.kanban.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        TaskHandler.EndPoint endPoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endPoint) {
            case GET_TASKS:
                handleGetSubTasks(exchange);
                break;
            case GET_TASK_BY_ID:
                handleGetSubTaskByID(exchange);
                break;
            case CREATE_UPDATE_TASK:
                handleCreateOrUpdateSubTask(exchange);
                break;

            case DELETE_TASK:
                handleDeleteSubTask(exchange);
                break;
            default:
                sendNotFound(exchange);
        }


    }

    private void handleDeleteSubTask(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        taskManager.deleteSubTask(id);
        sendText(exchange, "task deleted", 200 );

    }

    private void handleCreateOrUpdateSubTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        SubTask subtask = gson.fromJson(body, SubTask.class);
        try {
            if (subtask.getId() == 0) {
                taskManager.createSubTask(subtask);
                sendText(exchange, "", 201);

            } else {
                taskManager.updateSubTask(subtask);
                sendText(exchange, "", 201);
            }
        }
        catch (IntersectionException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleGetSubTaskByID(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange);
        try {
            Task task = taskManager.getSubTaskById(id);
            String taskJson = gson.toJson(task);
            sendText(exchange, taskJson, 200);
        }
        catch ( NoSuchElementException exception) {
            sendNotFound(exchange);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGetSubTasks(HttpExchange exchange) {
        List<SubTask> tasks = taskManager.getSubtasks();
        try {
            String tasksJson = gson.toJson(tasks);
            sendText(exchange, tasksJson, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
