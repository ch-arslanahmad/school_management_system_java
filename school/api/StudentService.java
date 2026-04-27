package school.api;

import java.util.List;

import classroom.Subjects;
import database.DBUtils;
import database.DAO.StudentDAO;
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
        return DBUtils.runInTransaction(conn -> new StudentDAO().insertStudent(student));
    }

    public static boolean updateStudent(int id, String newName) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO dao = new StudentDAO();
            Student existing = dao.fetchStudent(conn, id);
            if (existing.getID() == null || existing.getID() == 0) {
                return false;
            }
            existing.setName(newName);
            return dao.updateStudent(existing);
        });
    }

    public static boolean deleteStudent(int id) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO dao = new StudentDAO();
            Student student = dao.fetchStudent(conn, id);
            return dao.deleteStudent(student);
        });
    }

    public static boolean insertOrUpdateMarks(String studentName, int subjectId, int obtainedMarks) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO dao = new StudentDAO();
            Student student = dao.fetchStudentWithMarks(conn, studentName);
            Subjects subject = new Subjects(subjectId);
            subject.setObtMarks(obtainedMarks);
            return dao.insertOrUpdateMarks(student, subject);
        });
    }
}