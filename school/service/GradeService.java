package school.service;

import java.util.List;

import classroom.Subjects;
import database.DBUtils;
import database.DAO.GradeDAO;

public class GradeService {

    // ===== READ =====

    public static List<Subjects> getStudentReport(int studentId) {
        return DBUtils.runInTransaction(conn -> new GradeDAO().fetchStudentReport(conn, studentId));
    }

    public static List<Subjects> getStudentReport(String studentName) {
        return DBUtils.runInTransaction(conn -> new GradeDAO().fetchStudentReport(conn, studentName));
    }

    public static List<Subjects> getAllReports() {
        return DBUtils.runInTransaction(conn -> new GradeDAO().fetchAllStudentReports(conn));
    }

    public static List<Subjects> getClassGrades(int classId) {
        return DBUtils.runInTransaction(conn -> new GradeDAO().fetchClassGrades(conn, classId));
    }
}
