package school.service;

import java.util.ArrayList;
import java.util.List;

import classroom.Subjects;
import database.DBUtils;
import database.DAO.StudentDAO;
import database.DAO.GradeDAO;
import people.Student;

public class StudentService {

    // ===== READ =====

    public static List<Student> getStudents() {
        return DBUtils.runInTransaction(conn -> new StudentDAO().listStudents(conn));
    }

    public static Student getStudent(int id) {
        return DBUtils.runInTransaction(conn -> new StudentDAO().fetchStudent(conn, id));
    }

    public static Student getStudent(String name) {
        return DBUtils.runInTransaction(conn -> new StudentDAO().fetchStudentWithMarks(conn, name));
    }

    public static List<Student> getStudentsByClass(int classId) {
        return DBUtils.runInTransaction(conn -> new StudentDAO().listStudents(conn, classId));
    }

    // ===== WRITE =====

    public static boolean addStudent(Student student) {
        return DBUtils.runInTransaction(conn -> new StudentDAO().insertStudent(conn, student));
    }

    public static boolean updateStudent(int id, Student student) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO dao = new StudentDAO();
            Student existing = dao.fetchStudent(conn, id);
            if (existing.getID() == null || existing.getID() == 0) {
                return false;
            }
            // Apply updates from the request body
            if (student.getClassID() != null && student.getClassID() != 0) {
                existing.setClassID(student.getClassID());
            }
            return dao.updateStudent(conn, existing);
        });
    }

    public static boolean deleteStudent(int id) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO dao = new StudentDAO();
            Student student = dao.fetchStudent(conn, id);
            return dao.deleteStudent(conn, student);
        });
    }

    public static boolean insertOrUpdateMarks(String studentName, int subjectId, int obtainedMarks) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO dao = new StudentDAO();
            Student student = dao.fetchStudentWithMarks(conn, studentName);
            Subjects subject = new Subjects(subjectId);
            subject.setObtMarks(obtainedMarks);
            return dao.insertOrUpdateMarks(conn, student, subject);
        });
    }

    public static List<Subjects> getStudentSubjects(String studentName) {
        Student student = DBUtils.runInTransaction(conn -> new StudentDAO().fetchStudentWithMarks(conn, studentName));
        return student.getSubjects();
    }

    public static List<Subjects> getStudentSubjects(int studentId) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO dao = new StudentDAO();
            Student student = dao.fetchStudent(conn, studentId);
            if (student.getID() == null || student.getID() == 0) {
                return new ArrayList<>();
            }
            Student studentWithMarks = dao.fetchStudentWithMarks(conn, student.getName());
            return studentWithMarks.getSubjects();
        });
    }
}