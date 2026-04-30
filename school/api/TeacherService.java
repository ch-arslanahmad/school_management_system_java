package school.api;

import java.util.List;

import classroom.Subjects;
import database.DBUtils;
import database.DAO.TeacherDAO;
import people.Teacher;

public class TeacherService {

    // ===== READ =====

    public static List<Teacher> getTeachers() {
        return DBUtils.runInTransaction(conn -> new TeacherDAO().listTeachers(conn));
    }

    public static List<Teacher> getTeachersByClass(int classId) {
        return DBUtils.runInTransaction(conn -> new TeacherDAO().listTeachersByClass(conn, classId));
    }

    public static List<Teacher> getTeachersBySubject(int subjectId) {
        return DBUtils.runInTransaction(conn -> new TeacherDAO().listTeachersBySubject(conn, subjectId));
    }

    public static Teacher getTeacher(String name) {
        return DBUtils.runInTransaction(conn -> new TeacherDAO().fetchTeacher(conn, name));
    }

    public static Teacher getTeacher(int id) {
        return DBUtils.runInTransaction(conn -> new TeacherDAO().fetchTeacher(conn, id));
    }

    // ===== WRITE =====

    public static boolean addTeacher(Teacher teacher) {
        return DBUtils.runInTransaction(conn -> new TeacherDAO().insertTeacher(conn, teacher));
    }

    public static boolean addTeacher(String name, int subjectId) {
        Teacher teacher = new Teacher(name);
        Subjects subject = new Subjects();
        subject.setID(subjectId);
        teacher.setSubject(subject);
        return addTeacher(teacher);
    }

    public static boolean updateTeacher(int id, String newName) {
        return DBUtils.runInTransaction(conn -> {
            TeacherDAO dao = new TeacherDAO();
            Teacher existing = dao.fetchTeacher(conn, id);
            if (existing.getID() == null || existing.getID() == 0) {
                return false;
            }
            existing.setName(newName);
            return dao.updateTeacher(conn, existing);
        });
    }

    public static boolean updateTeacher(int id, String newName, int subjectId) {
        return DBUtils.runInTransaction(conn -> {
            TeacherDAO dao = new TeacherDAO();
            Teacher existing = dao.fetchTeacher(conn, id);
            if (existing.getID() == null || existing.getID() == 0) {
                return false;
            }
            existing.setName(newName);
            Subjects subject = new Subjects();
            subject.setID(subjectId);
            existing.setSubject(subject);
            return dao.updateTeacher(conn, existing);
        });
    }

    public static boolean assignSubject(int teacherId, int subjectId) {
        return DBUtils.runInTransaction(conn -> {
            TeacherDAO dao = new TeacherDAO();
            Teacher existing = dao.fetchTeacher(conn, teacherId);
            if (existing.getID() == null || existing.getID() == 0) {
                return false;
            }
            Subjects subject = new Subjects();
            subject.setID(subjectId);
            existing.setSubject(subject);
            return dao.updateTeacher(conn, existing);
        });
    }

    public static boolean deleteTeacher(int id) {
        return DBUtils.runInTransaction(conn -> {
            TeacherDAO dao = new TeacherDAO();
            Teacher teacher = dao.fetchTeacher(conn, id);
            return dao.deleteTeacher(conn, teacher);
        });
    }
}