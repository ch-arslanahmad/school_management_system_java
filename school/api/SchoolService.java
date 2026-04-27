package school.api;

import school.School;
import database.DBUtils;
import database.DAO.SchoolDAO;

public class SchoolService {

    // ===== READ =====

    public static School getSchool() {
        return DBUtils.runInTransaction(conn -> new SchoolDAO().fetchSchool(conn));
    }

    // ===== WRITE =====

    public static boolean addSchool(School school) {
        return DBUtils.runInTransaction(conn -> new SchoolDAO().insertSchool(school));
    }

    public static boolean updateSchool(School school) {
        return DBUtils.runInTransaction(conn -> new SchoolDAO().updateSchool(school));
    }

    public static boolean deleteSchool() {
        return DBUtils.runInTransaction(conn -> {
            SchoolDAO dao = new SchoolDAO();
            School school = dao.fetchSchool(conn);
            if (school == null || school.getId() == 0) {
                return false;
            }
            return dao.deleteSchool(school);
        });
    }
}