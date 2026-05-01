package web.routes;

import io.javalin.Javalin;
import people.Student;
import classroom.Subjects;
import school.service.StudentService;
import web.common.ApiResponse;
import web.common.MarkRequest;

import java.util.List;

public class StudentRoutes {

    public static void register(Javalin app) {

        // ===== READ ENDPOINTS =====

        // GET /api/students - List all students
        app.get("/api/students", ctx -> {
            List<Student> students = StudentService.getStudents();
            ctx.json(ApiResponse.success(students));
        });

        // GET /api/students/{identifier} - Get student by ID or name
        app.get("/api/students/{identifier}", ctx -> {
            String identifier = ctx.pathParam("identifier");
            Student student;
            
            try {
                int id = Integer.parseInt(identifier);
                student = StudentService.getStudent(id);
            } catch (NumberFormatException e) {
                student = StudentService.getStudent(identifier);
            }
            
            if (student.getID() == null || student.getID() == 0) {
                ctx.status(404).json(ApiResponse.error("Student not found"));
                return;
            }
            ctx.json(ApiResponse.success(student));
        });

        // GET /api/students/class/{classId} - Get students by class
        app.get("/api/students/class/{classId}", ctx -> {
            int classId = Integer.parseInt(ctx.pathParam("classId"));
            List<Student> students = StudentService.getStudentsByClass(classId);
            ctx.json(ApiResponse.success(students));
        });

        // GET /api/students/{identifier}/subjects - Get student subjects with marks (by ID or name)
        app.get("/api/students/{identifier}/subjects", ctx -> {
            String identifier = ctx.pathParam("identifier");
            List<Subjects> subjects;
            
            // Try to parse as ID first
            try {
                int id = Integer.parseInt(identifier);
                subjects = StudentService.getStudentSubjects(id);
                if (subjects.isEmpty()) {
                    ctx.status(404).json(ApiResponse.error("Student not found"));
                    return;
                }
            } catch (NumberFormatException e) {
                // It's a name, not an ID
                subjects = StudentService.getStudentSubjects(identifier);
                if (subjects.isEmpty()) {
                    ctx.status(404).json(ApiResponse.error("Student not found"));
                    return;
                }
            }
            
            ctx.json(ApiResponse.success(subjects));
        });

        // ===== WRITE ENDPOINTS =====

        // POST /api/students - Create student
        app.post("/api/students", ctx -> {
            Student student = ctx.bodyAsClass(Student.class);
            boolean success = StudentService.addStudent(student);
            if (success) {
                ctx.status(201).json(ApiResponse.success("Student created", student));
            } else {
                ctx.status(400).json(ApiResponse.error("Failed to create student"));
            }
        });

        // PUT /api/students/{id} - Update student (name and/or class)
        app.put("/api/students/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Student student = ctx.bodyAsClass(Student.class);
            boolean success = StudentService.updateStudent(id, student);
            if (success) {
                ctx.json(ApiResponse.success("Student updated"));
            } else {
                ctx.status(404).json(ApiResponse.error("Student not found or update failed"));
            }
        });

        // DELETE /api/students/{id} - Delete student
        app.delete("/api/students/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean success = StudentService.deleteStudent(id);
            if (success) {
                ctx.json(ApiResponse.success("Student deleted"));
            } else {
                ctx.status(404).json(ApiResponse.error("Student not found or delete failed"));
            }
        });

        // POST /api/students/{name}/marks - Add/update marks
        app.post("/api/students/{name}/marks", ctx -> {
            String name = ctx.pathParam("name");
            MarkRequest request = ctx.bodyAsClass(MarkRequest.class);
            boolean success = StudentService.insertOrUpdateMarks(name, request.getSubjectId(), request.getObtainedMarks());
            if (success) {
                ctx.json(ApiResponse.success("Marks updated"));
            } else {
                ctx.status(400).json(ApiResponse.error("Failed to update marks"));
            }
        });
    }
}
