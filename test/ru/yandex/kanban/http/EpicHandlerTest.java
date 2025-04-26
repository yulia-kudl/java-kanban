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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicHandlerTest {

    HttpServer httpServer;
    Gson gson;
    TaskManager manager = new InMemoryTaskManager();
    HttpResponse<String> response;
    HttpRequest request;
    Epic epic;
    HttpClient client;
    URI url;

    public EpicHandlerTest() throws IOException {

    }

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubTasks();
        httpServer = HttpTaskServer.startServer(manager);
        gson = BaseHttpHandler.gson;
        epic = new Epic("epic", TaskStatus.NEW, "Testing epic");
        // конвертируем её в JSON
        String taskJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

    }

    @AfterEach
    public void shutDown() {
        HttpTaskServer.StopServer(httpServer);

    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу

        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("epic", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {

        epic.setDescription("new description");
        epic.setId(1);
        String taskJson = gson.toJson(epic);

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("new description", tasksFromManager.getFirst().getDescription(),
                "Некорректное описание задачи");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {

        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        url = URI.create("http://localhost:8080/epics/1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");

    }

    @Test
    public void GetEpicTest() throws IOException, InterruptedException {
        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        url = URI.create("http://localhost:8080/epics/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String json = response.body();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        assertEquals(epic.getName(), jsonObject.get("name").getAsString(), "неверное имя");
        assertEquals(epic.getDescription(), jsonObject.get("description").getAsString(), "неверное описание");
        assertEquals(epic.getStatus(), TaskStatus.valueOf(jsonObject.get("status").getAsString()), "неверное имя");

    }

    @Test
    public void GetEpicssTest() throws IOException, InterruptedException {
        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        url = URI.create("http://localhost:8080/epics/");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String json = response.body();
        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        assertEquals(epic.getName(), jsonObject.get("name").getAsString(), "неверное имя");
        assertEquals(epic.getDescription(), jsonObject.get("description").getAsString(), "неверное описание");
        assertEquals(epic.getStatus(), TaskStatus.valueOf(jsonObject.get("status").getAsString()), "неверное имя");
        assertEquals(jsonObject.getAsJsonArray("epicSubTasks").size(), epic.getEpicSubTasks().size(),
                "неверное количество сабтасков");

    }

    @Test
    public void GetEpicNegativeTest() throws IOException, InterruptedException {
        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        url = URI.create("http://localhost:8080/epics/5");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

    }


    @Test
    public void GetEpicSubtasks() throws IOException, InterruptedException {
        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");

        manager.createSubTask(new SubTask("name", TaskStatus.NEW, "desc", 1, Duration.ofMinutes(3)));

        url = URI.create("http://localhost:8080/epics/1/subtasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String json = response.body();
        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        assertEquals(1, jsonArray.size(), "неверный размер массива подзадач");
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        assertEquals("name", jsonObject.get("name").getAsString(), "неверное имя");
        assertEquals("desc", jsonObject.get("description").getAsString(), "неверное описание");


    }

}