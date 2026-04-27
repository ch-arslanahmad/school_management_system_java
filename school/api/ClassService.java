package school.api;

import java.util.List;

import classroom.ClassRoom;
import database.DBUtils;
import database.DAO.ClassDAO;

public class ClassService {

    // ===== READ =====

    public static List<ClassRoom> getClasses() {
        return DBUtils.runInTransaction(conn -> new ClassDAO().listClass(conn));
    }

    public static ClassRoom getClass(String name) {
        return DBUtils.runInTransaction(conn -> new ClassDAO().fetchClass(conn, name));
    }

    public static ClassRoom getClassById(int id) {
        return DBUtils.runInTransaction(conn -> new ClassDAO().fetchClass(conn, id));
    }

    public static boolean exists(String name) {
        return DBUtils.runInTransaction(conn -> new ClassDAO().ClassExists(conn, name));
    }

    // ===== WRITE =====

    public static boolean addClass(ClassRoom classRoom) {
        return new ClassDAO().insertClass(classRoom);
    }

    public static boolean deleteClass(int id) {
        ClassDAO dao = new ClassDAO();
        ClassRoom cls = new ClassRoom();
        cls.setID(id);
        return dao.deleteClass(cls);
    }

    public static boolean updateClass(int oldId, ClassRoom newClass) {
        ClassDAO dao = new ClassDAO();
        ClassRoom oldClass = new ClassRoom();
        oldClass.setID(oldId);
        return dao.updateClass(oldClass, newClass);
    }
}