package school.api;

import java.util.List;

import classroom.Subjects;
import database.DBUtils;
import database.DAO.SubjectDAO;

public class SubjectService {

    // ===== READ =====

    public static List<Subjects> getSubjects() {
        return DBUtils.runInTransaction(conn -> new SubjectDAO().listSubjects(conn));
    }

    public static List<Subjects> getSubjectsByClass(String className) {
        return DBUtils.runInTransaction(conn -> new SubjectDAO().listSubjects(conn, className));
    }

    public static Subjects getSubject(String name) {
        return DBUtils.runInTransaction(conn -> new SubjectDAO().fetchSubject(conn, name));
    }

    public static List<Subjects> getSubjectsByClass(int classId) {
        return DBUtils.runInTransaction(conn -> new SubjectDAO().listSubjectsByClass(conn, classId));
    }

    public static List<Subjects> getSubjectsByTeacher(int teacherId) {
        return DBUtils.runInTransaction(conn -> new SubjectDAO().listSubjectsByTeacher(conn, teacherId));
    }

    // ===== WRITE =====

    public static boolean addSubject(Subjects subject) {
        return new SubjectDAO().insertSubject(subject);
    }

    public static boolean addSubject(String className, String subjectName) {
        return new SubjectDAO().insertSubject(className, subjectName);
    }

    public static boolean updateSubject(int id, String newName) {
        return DBUtils.runInTransaction(conn -> {
            SubjectDAO dao = new SubjectDAO();
            Subjects existing = dao.fetchSubject(conn, newName);
            if (existing.getID() == null || existing.getID() == 0) {
                // fetch by ID if name not found
                existing = new Subjects();
                existing.setID(id);
            }
            existing.setName(newName);
            return dao.updateSubject(existing);
        });
    }

    public static boolean deleteSubject(int id) {
        SubjectDAO dao = new SubjectDAO();
        Subjects subj = new Subjects();
        subj.setID(id);
        return dao.deleteSubject(subj);
    }
}