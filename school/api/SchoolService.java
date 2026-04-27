package school.api;

import school.School;
import database.DBUtils;
import database.DAO.SchoolDAO;

public class SchoolService {

    public static School getSchool() {
        return DBUtils.runInTransaction(conn -> new SchoolDAO().fetchSchool(conn));
    }

    public static boolean addSchool(School school) {
        return new SchoolDAO().insertSchool(school);
    }

    public static boolean updateSchool(School school) {
        return new SchoolDAO().updateSchool(new School(), school);
    }

    public static boolean deleteSchool() {
        return new SchoolDAO().deleteSchool(new School());
    }
}