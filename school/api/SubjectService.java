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

    // ===== WRITE =====

    public static boolean addSubject(Subjects subject) {
        return new SubjectDAO().insertSubject(subject);
    }

    public static boolean addSubject(String className, String subjectName) {
        return new SubjectDAO().insertSubject(className, subjectName);
    }

    public static boolean deleteSubject(int id) {
        SubjectDAO dao = new SubjectDAO();
        Subjects subj = new Subjects();
        subj.setID(id);
        return dao.deleteSubject(subj);
    }

    public static boolean updateSubject(String oldName, String newName) {
        return new SubjectDAO().updateSubject(oldName, newName);
    }
}