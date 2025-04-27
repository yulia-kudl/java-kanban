package ru.yandex.kanban.http;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.kanban.Managers;
import ru.yandex.kanban.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;


    public static HttpServer startServer(TaskManager manager) throws IOException {
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0); // связываем сервер с сетевым портом
        BaseHttpHandler.taskManager = manager;
        BaseHttpHandler.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();

        httpServer.createContext("/tasks", new SafeHandler(new TaskHandler())); // связываем путь и обработчик
        httpServer.createContext("/subtasks", new SafeHandler(new SubTaskHandler())); // связываем путь и обработчик
        httpServer.createContext("/epics", new SafeHandler(new EpicHandler())); // связываем путь и обработчик
        httpServer.createContext("/history", new SafeHandler(new HistoryHandler())); // связываем путь и обработчик
        httpServer.createContext("/prioritized", new SafeHandler(new PrioritizedHandler())); // связываем путь и обработчик
        httpServer.start(); // запускаем сервер
        return httpServer;
    }

    public static void stopServer(HttpServer h) {
        h.stop(2);

    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpServer httpServer = HttpTaskServer.startServer(taskManager);

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }


}
