package web.routes;

import io.javalin.Javalin;
import people.Teacher;
import school.service.TeacherService;
import web.common.ApiResponse;

import java.util.List;

public class TeacherRoutes {

    public static void register(Javalin app) {

        // ===== READ ENDPOINTS =====

        // GET /api/teachers - List all teachers
        app.get("/api/teachers", ctx -> {
            List<Teacher> teachers = TeacherService.getTeachers();
            ctx.json(ApiResponse.success(teachers));
        });

        // GET /api/teachers/{identifier} - Get teacher by ID or name
        app.get("/api/teachers/{identifier}", ctx -> {
            String identifier = ctx.pathParam("identifier");
            Teacher teacher;
            
            try {
                int id = Integer.parseInt(identifier);
                teacher = TeacherService.getTeacher(id);
            } catch (NumberFormatException e) {
                teacher = TeacherService.getTeacher(identifier);
            }
            
            if (teacher.getID() == null || teacher.getID() == 0) {
                ctx.status(404).json(ApiResponse.error("Teacher not found"));
                return;
            }
            ctx.json(ApiResponse.success(teacher));
        });

        // GET /api/teachers/class/{classId} - Get teachers by class
        app.get("/api/teachers/class/{classId}", ctx -> {
            int classId = Integer.parseInt(ctx.pathParam("classId"));
            List<Teacher> teachers = TeacherService.getTeachersByClass(classId);
            ctx.json(ApiResponse.success(teachers));
        });

        // GET /api/teachers/subject/{subjectId} - Get teachers by subject
        app.get("/api/teachers/subject/{subjectId}", ctx -> {
            int subjectId = Integer.parseInt(ctx.pathParam("subjectId"));
            List<Teacher> teachers = TeacherService.getTeachersBySubject(subjectId);
            ctx.json(ApiResponse.success(teachers));
        });

        // ===== WRITE ENDPOINTS =====

        // POST /api/teachers - Create teacher
        app.post("/api/teachers", ctx -> {
            Teacher teacher = ctx.bodyAsClass(Teacher.class);
            boolean success = TeacherService.addTeacher(teacher);
            if (success) {
                ctx.status(201).json(ApiResponse.success("Teacher created", teacher));
            } else {
                ctx.status(400).json(ApiResponse.error("Failed to create teacher"));
            }
        });

        // PUT /api/teachers/{id} - Update teacher name
        app.put("/api/teachers/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            TeacherUpdateRequest request = ctx.bodyAsClass(TeacherUpdateRequest.class);
            boolean success = TeacherService.updateTeacher(id, request.getName(), request.getSubjectId());
            if (success) {
                ctx.json(ApiResponse.success("Teacher updated"));
            } else {
                ctx.status(404).json(ApiResponse.error("Teacher not found or update failed"));
            }
        });

        // POST /api/teachers/{id}/subject - Assign subject to teacher
        app.post("/api/teachers/{id}/subject", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            SubjectAssignRequest request = ctx.bodyAsClass(SubjectAssignRequest.class);
            boolean success = TeacherService.assignSubject(id, request.getSubjectId());
            if (success) {
                ctx.json(ApiResponse.success("Subject assigned to teacher"));
            } else {
                ctx.status(404).json(ApiResponse.error("Teacher not found or assignment failed"));
            }
        });

        // DELETE /api/teachers/{id} - Delete teacher
        app.delete("/api/teachers/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean success = TeacherService.deleteTeacher(id);
            if (success) {
                ctx.json(ApiResponse.success("Teacher deleted"));
            } else {
                ctx.status(404).json(ApiResponse.error("Teacher not found or delete failed"));
            }
        });
    }

    // ===== REQUEST DTOs =====

    private static class TeacherUpdateRequest {
        private String name;
        private int subjectId;

        public String getName() { return name; }
        public int getSubjectId() { return subjectId; }
    }

    private static class SubjectAssignRequest {
        private int subjectId;

        public int getSubjectId() { return subjectId; }
    }
}
