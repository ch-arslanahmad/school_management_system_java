package school.api;

import java.util.List;

import classroom.Subjects;
import database.DBUtils;
import database.DAO.StudentDAO;
import people.Student;

public class StudentService {

    // ===== READ METHODS =====

    public static List<Student> getStudents() {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO studentDAO = new StudentDAO();
            return studentDAO.listStudents(conn);
        });
    }

    public static Student getStudent(String name) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO studentDAO = new StudentDAO();
            return studentDAO.fetchStudentWithMarks(conn, name);
        });
    }

    public static List<Student> getStudent(int classId) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO studentDAO = new StudentDAO();
            return studentDAO.listStudents(conn, classId);
        });
    }

    // ===== WRITE METHODS =====

    public static boolean addStudent(Student student) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO studentDAO = new StudentDAO();
            return studentDAO.insertStudent(student);
        });
    }

    public static boolean updateStudent(String oldName, String newName) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO studentDAO = new StudentDAO();
            return studentDAO.updateStudent(new Student(oldName), new Student(newName));
        });
    }

    public static boolean deleteStudent(String name) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO studentDAO = new StudentDAO();
            Student student = studentDAO.fetchStudent(conn, name);
            return studentDAO.deleteStudent(student);
        });
    }

    public static boolean insertOrUpdateMarks(String studentName, int subjectId, int obtainedMarks) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO studentDAO = new StudentDAO();
            Student student = studentDAO.fetchStudentWithMarks(conn, studentName);
            Subjects subject = new Subjects(subjectId);
            subject.setObtMarks(obtainedMarks);
            return studentDAO.insertOrUpdateMarks(student, subject);
        });
    }

}
