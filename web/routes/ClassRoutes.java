package web.routes;

import io.javalin.Javalin;
import classroom.ClassRoom;
import classroom.Subjects;
import school.service.ClassService;
import school.service.GradeService;
import web.common.ApiResponse;

import java.util.List;

public class ClassRoutes {

    public static void register(Javalin app) {

        // ===== READ ENDPOINTS =====

        // GET /api/classes - List all classes with fees
        app.get("/api/classes", ctx -> {
            List<ClassRoom> classes = ClassService.getClasses();
            ctx.json(ApiResponse.success(classes));
        });

        // GET /api/classes/{identifier} - Get class by ID or name
        app.get("/api/classes/{identifier}", ctx -> {
            String identifier = ctx.pathParam("identifier");
            ClassRoom classRoom;
            
            try {
                int id = Integer.parseInt(identifier);
                classRoom = ClassService.getClass(id);
            } catch (NumberFormatException e) {
                classRoom = ClassService.getClass(identifier);
            }
            
            if (classRoom.isEmpty()) {
                ctx.status(404).json(ApiResponse.error("Class not found"));
                return;
            }
            ctx.json(ApiResponse.success(classRoom));
        });

        // get grades for class
        app.get("/api/classes/{id}/grades", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ClassRoom classRoom = ClassService.getClass(id);
            
            List<Subjects> grades = GradeService.getClassGrades(id);

            if (classRoom.isEmpty()) {
                ctx.status(404).json(ApiResponse.error("Class not found"));
                return;
            }
            ctx.json(ApiResponse.success(grades));
        });

        // ===== WRITE ENDPOINTS =====

        // POST /api/classes - Create class
        app.post("/api/classes", ctx -> {
            ClassRoom classRoom = ctx.bodyAsClass(ClassRoom.class);
            boolean success = ClassService.addClass(classRoom);
            if (success) {
                ctx.status(201).json(ApiResponse.success("Class created", classRoom));
            } else {
                ctx.status(400).json(ApiResponse.error("Failed to create class"));
            }
        });

        // PUT /api/classes/{id} - Update class name
        app.put("/api/classes/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ClassUpdateRequest request = ctx.bodyAsClass(ClassUpdateRequest.class);
            boolean success = ClassService.updateClass(id, request.getName());
            if (success) {
                ctx.json(ApiResponse.success("Class name updated"));
            } else {
                ctx.status(404).json(ApiResponse.error("Class not found or update failed"));
            }
        });

        // PUT /api/classes/{id}/fees - Update class fees
        app.put("/api/classes/{id}/fees", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            FeeUpdateRequest request = ctx.bodyAsClass(FeeUpdateRequest.class);
            boolean success = ClassService.updateClass(id, request.getTuitionFee(), request.getStationaryFee(), request.getPaperFee());
            if (success) {
                ctx.json(ApiResponse.success("Class fees updated"));
            } else {
                ctx.status(404).json(ApiResponse.error("Class not found or update failed"));
            }
        });

        // DELETE /api/classes/{id} - Delete class
        app.delete("/api/classes/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean success = ClassService.deleteClass(id);
            if (success) {
                ctx.json(ApiResponse.success("Class deleted"));
            } else {
                ctx.status(404).json(ApiResponse.error("Class not found or delete failed"));
            }
        });
    }

    // ===== REQUEST DTOs =====

    private static class ClassUpdateRequest {
        private String name;

        public String getName() { return name; }
    }

    private static class FeeUpdateRequest {
        private int tuitionFee;
        private int stationaryFee;
        private int paperFee;

        public int getTuitionFee() { return tuitionFee; }
        public int getStationaryFee() { return stationaryFee; }
        public int getPaperFee() { return paperFee; }
    }
}
