package web.routes;

import io.javalin.Javalin;
import classroom.Subjects;
import school.service.SubjectService;
import web.common.ApiResponse;

import java.util.List;

public class SubjectRoutes {

    public static void register(Javalin app) {

        // ===== READ ENDPOINTS =====

        // GET /api/subjects - List all subjects
        app.get("/api/subjects", ctx -> {
            List<Subjects> subjects = SubjectService.getSubjects();
            ctx.json(ApiResponse.success(subjects));
        });

        // GET /api/subjects/{identifier} - Get subject by ID or name
        app.get("/api/subjects/{identifier}", ctx -> {
            String identifier = ctx.pathParam("identifier");
            Subjects subject;
            
            try {
                int id = Integer.parseInt(identifier);
                subject = SubjectService.getSubject(id);
            } catch (NumberFormatException e) {
                subject = SubjectService.getSubject(identifier);
            }
            
            if (subject.getID() == null || subject.getID() == 0) {
                ctx.status(404).json(ApiResponse.error("Subject not found"));
                return;
            }
            ctx.json(ApiResponse.success(subject));
        });

        // GET /api/subjects/class/{classId} - Get subjects by class ID
        app.get("/api/subjects/class/{classId}", ctx -> {
            int classId = Integer.parseInt(ctx.pathParam("classId"));
            List<Subjects> subjects = SubjectService.getSubjectsByClass(classId);
            ctx.json(ApiResponse.success(subjects));
        });

        // GET /api/subjects/teacher/{teacherId} - Get subjects by teacher
        app.get("/api/subjects/teacher/{teacherId}", ctx -> {
            int teacherId = Integer.parseInt(ctx.pathParam("teacherId"));
            List<Subjects> subjects = SubjectService.getSubjectsByTeacher(teacherId);
            ctx.json(ApiResponse.success(subjects));
        });

        // ===== WRITE ENDPOINTS =====

        // POST /api/subjects - Create subject
        app.post("/api/subjects", ctx -> {
            SubjectCreateRequest request = ctx.bodyAsClass(SubjectCreateRequest.class);
            boolean success = SubjectService.addSubject(request.getClassName(), request.getSubjectName());
            if (success) {
                ctx.status(201).json(ApiResponse.success("Subject created"));
            } else {
                ctx.status(400).json(ApiResponse.error("Failed to create subject"));
            }
        });

        // PUT /api/subjects/{id} - Update subject name
        app.put("/api/subjects/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            SubjectUpdateRequest request = ctx.bodyAsClass(SubjectUpdateRequest.class);
            boolean success = SubjectService.updateSubject(id, request.getName());
            if (success) {
                ctx.json(ApiResponse.success("Subject updated"));
            } else {
                ctx.status(404).json(ApiResponse.error("Subject not found or update failed"));
            }
        });

        // DELETE /api/subjects/{id} - Delete subject
        app.delete("/api/subjects/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean success = SubjectService.deleteSubject(id);
            if (success) {
                ctx.json(ApiResponse.success("Subject deleted"));
            } else {
                ctx.status(404).json(ApiResponse.error("Subject not found or delete failed"));
            }
        });
    }

    // ===== REQUEST DTOs =====

    private static class SubjectCreateRequest {
        private String className;
        private String subjectName;

        public String getClassName() { return className; }
        public String getSubjectName() { return subjectName; }
    }

    private static class SubjectUpdateRequest {
        private String name;

        public String getName() { return name; }
    }
}
