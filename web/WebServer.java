package web;

import io.javalin.Javalin;
import web.routes.*;

public class WebServer {
    public static void start() {
        Javalin app = Javalin.create().start(7070);

        app.get("/", ctx -> ctx.result("hello to school!"));


        app.get("/api/ping", ctx -> ctx.result("pong"));

        // Register all route groups
        StudentRoutes.register(app);
        TeacherRoutes.register(app);
        SubjectRoutes.register(app);
        ClassRoutes.register(app);
        SchoolRoutes.register(app);
    }
}