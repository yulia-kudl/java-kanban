package ru.yandex.kanban.http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubTaskHandlerTest {

    HttpServer httpServer;
    Gson gson;
    TaskManager manager = new InMemoryTaskManager();
    HttpResponse<String> response;
    HttpRequest request;
    SubTask task;
    Epic epic;
    HttpClient client;
    URI url;

    public SubTaskHandlerTest() throws IOException {

    }

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubTasks();
        httpServer = HttpTaskServer.startServer(manager);
        gson = BaseHttpHandler.gson;
        manager.createEpic(new Epic("epic", TaskStatus.NEW, "desc"));
        task = new SubTask("Test 2", TaskStatus.NEW, "Testing task 2", 1,
                Duration.parse("PT30M"), LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.stopServer(httpServer);

    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        // создаём задачу

        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {

        task.setDescription("new description");
        task.setId(2);
        String taskJson = gson.toJson(task);

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        List<SubTask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("new description", tasksFromManager.getFirst().getDescription(),
                "Некорректное описание задачи");
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {

        List<SubTask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        url = URI.create("http://localhost:8080/subtasks/2");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");

    }

    @Test
    public void GetSubTaskTest() throws IOException, InterruptedException {
        List<SubTask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        url = URI.create("http://localhost:8080/subtasks/2");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String json = response.body();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        assertEquals(task.getName(), jsonObject.get("name").getAsString(), "неверное имя");
        assertEquals(task.getDescription(), jsonObject.get("description").getAsString(), "неверное описание");
        assertEquals(task.getStatus(), TaskStatus.valueOf(jsonObject.get("status").getAsString()), "неверное имя");

    }

    @Test
    public void GetSubTasksTest() throws IOException, InterruptedException {
        List<SubTask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        url = URI.create("http://localhost:8080/subtasks/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String json = response.body();
        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        assertEquals(task.getName(), jsonObject.get("name").getAsString(), "неверное имя");
        assertEquals(task.getDescription(), jsonObject.get("description").getAsString(), "неверное описание");
        assertEquals(task.getStatus(), TaskStatus.valueOf(jsonObject.get("status").getAsString()), "неверное имя");

    }

    @Test
    public void GetSubTaskNegativeTest() throws IOException, InterruptedException {
        List<SubTask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        url = URI.create("http://localhost:8080/subtasks/5");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

    }

    @Test
    public void CreateSubTaskNegativeTest() throws IOException, InterruptedException {
        List<SubTask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");

        SubTask task2 = new SubTask("Test overlap", TaskStatus.NEW, "Testing overlap", 1,
                Duration.parse("PT30M"), LocalDateTime.now());
        String taskJson = gson.toJson(task2);
        url = URI.create("http://localhost:8080/subtasks/");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

    }

    @Test
    public void UpdateSubTaskNegativeTest() throws IOException, InterruptedException {
        List<SubTask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");

        SubTask task2 = new SubTask("Test overlap", TaskStatus.NEW, "Testing overlap", 1,
                Duration.parse("PT30M"), LocalDateTime.now().minusHours(1));
        String taskJson = gson.toJson(task2);
        url = URI.create("http://localhost:8080/subtasks/");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        SubTask task3 = task2.copyTask();
        task3.setDuration(Duration.ofMinutes(5000000));
        taskJson = gson.toJson(task3);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());


    }


}