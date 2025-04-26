package ru.yandex.kanban.http;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.kanban.Managers;
import ru.yandex.kanban.Task;
import ru.yandex.kanban.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class TasksListTypeToken extends TypeToken<List<Task>> {

}

class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        jsonWriter.value(duration == null ? null : duration.toMinutes());

    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        return Duration.ofMinutes(Integer.parseInt(jsonReader.nextString()));

    }
}

class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime == null ? null : localDateTime.format(dateTimeFormatter));

    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), dateTimeFormatter);

    }
}

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

    public static void StopServer(HttpServer h) {
        h.stop(2);

    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpServer httpServer = HttpTaskServer.startServer(taskManager);

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }


}
