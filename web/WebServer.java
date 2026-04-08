package web;

import io.javalin.Javalin;

public class WebServer {
    public static void start() {
        Javalin app = Javalin.create().start(7070);

        // app.get("/api/ping", ctx -> ctx.result("pong"));
    }
}