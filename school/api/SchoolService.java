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
        return new SchoolDAO().insertSchool(school);
    }

    public static boolean updateSchool(School school) {
        return new SchoolDAO().updateSchool(school);
    }

    public static boolean deleteSchool() {
        School school = new School();
        return new SchoolDAO().deleteSchool(school);
    }
}