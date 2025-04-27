package ru.yandex.kanban.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class SafeHandler implements HttpHandler {
    private final HttpHandler wrapped;

    public SafeHandler(HttpHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            wrapped.handle(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            String response = "Internal Server Error";
            exchange.sendResponseHeaders(500, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
