package web.routes;

import io.javalin.Javalin;
import school.School;
import school.service.SchoolService;
import web.common.ApiResponse;

public class SchoolRoutes {

    public static void register(Javalin app) {

        // ===== READ ENDPOINTS =====

        // GET /api/school - Get school info
        app.get("/api/school", ctx -> {
            School school = SchoolService.getSchool();
            if (school == null || school.getId() == 0) {
                ctx.status(404).json(ApiResponse.error("School not found"));
                return;
            }
            ctx.json(ApiResponse.success(school));
        });

        // ===== WRITE ENDPOINTS =====

        // POST /api/school - Create school
        app.post("/api/school", ctx -> {
            School school = ctx.bodyAsClass(School.class);
            boolean success = SchoolService.addSchool(school);
            if (success) {
                ctx.status(201).json(ApiResponse.success("School created", school));
            } else {
                ctx.status(400).json(ApiResponse.error("Failed to create school"));
            }
        });

        // PUT /api/school - Update school
        app.put("/api/school", ctx -> {
            School school = ctx.bodyAsClass(School.class);
            boolean success = SchoolService.updateSchool(school);
            if (success) {
                ctx.json(ApiResponse.success("School updated"));
            } else {
                ctx.status(404).json(ApiResponse.error("School not found or update failed"));
            }
        });

        // DELETE /api/school - Delete school
        app.delete("/api/school", ctx -> {
            boolean success = SchoolService.deleteSchool();
            if (success) {
                ctx.json(ApiResponse.success("School deleted"));
            } else {
                ctx.status(404).json(ApiResponse.error("School not found or delete failed"));
            }
        });
    }
}
