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

    public static ClassRoom getClass(int id) {
        return DBUtils.runInTransaction(conn -> new ClassDAO().fetchClass(conn, id));
    }

    // ===== WRITE =====

    public static boolean addClass(ClassRoom cls) {
        return new ClassDAO().insertClass(cls);
    }

    public static boolean updateClass(int id, String name) {
        return DBUtils.runInTransaction(conn -> {
            ClassDAO dao = new ClassDAO();
            ClassRoom existing = dao.fetchClass(conn, id);
            if (existing.isEmpty()) {
                return false;
            }
            existing.setName(name);
            return dao.updateClass(existing);
        });
    }

    public static boolean updateClass(int id, int tuitionFee, int stationaryFee, int paperFee) {
        return DBUtils.runInTransaction(conn -> {
            ClassDAO dao = new ClassDAO();
            ClassRoom existing = dao.fetchClass(conn, id);
            if (existing.isEmpty()) {
                return false;
            }
            existing.setTuitionFee(tuitionFee);
            existing.setStationaryFee(stationaryFee);
            existing.setPaperFee(paperFee);
            return dao.updateClass(existing);
        });
    }

    public static boolean deleteClass(int id) {
        ClassDAO dao = new ClassDAO();
        ClassRoom cls = new ClassRoom();
        cls.setID(id);
        return dao.deleteClass(cls);
    }
}