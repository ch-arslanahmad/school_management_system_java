// BETA VERSION FINALIZED - SchoolDAO
package database.DAO;

// package imports
import display.LogHandler;
import school.School;
import database.DBUtils;

// imports
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class SchoolDAO {

    private static final Logger logger = Logger.getLogger(SchoolDAO.class.getName());

    static {
        LogHandler.createLog(logger, "SchoolDAO");
    }

    public School fetchSchool(Connection conn) {
        School school = new School();

        String schoolSQL = "SELECT * FROM School WHERE id = 1;";

        try (PreparedStatement rm = conn.prepareStatement(schoolSQL)) {
            try (ResultSet rs = rm.executeQuery()) {
                if (rs.next()) {
                    school.setId(rs.getInt("id"));
                    school.setName(rs.getString("Name"));
                    school.setPrincipal(rs.getString("Principal"));
                } else {
                    logger.warning("School not found.");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching School.", e);
        }

        return school;
    }

    public boolean schoolExists(Connection conn) {
        String check = "SELECT 1 FROM School WHERE id = 1;";

        try (PreparedStatement rm = conn.prepareStatement(check)) {
            try (ResultSet rs = rm.executeQuery()) {
                if (rs.next()) {
                    logger.info("School exists.");
                    return true;
                } else {
                    logger.warning("School does not exist.");
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking School existence.", e);
        }
        return false;
    }

    public boolean insertSchool(School school) {
        return DBUtils.runInTransaction(conn -> {
            if (schoolExists(conn)) {
                logger.warning("School already exists. Use update instead.");
                return false;
            }

            String schoolSQL = "INSERT INTO School (id, Name, Principal, location) VALUES (1,?,?,?)";
            try (PreparedStatement rm = conn.prepareStatement(schoolSQL)) {
                rm.setString(1, school.getName());
                rm.setString(2, school.getPrincipal());
                rm.setString(3, school.getlocation());

                int rs = rm.executeUpdate();
                logger.info("Inserted School.");
                return rs > 0;
            }
        });
    }

    public boolean deleteSchool(School school) {
        return DBUtils.runInTransaction(conn -> {
            String deleteSchoolSQL = "DELETE FROM School WHERE id = 1";
            try (PreparedStatement rm = conn.prepareStatement(deleteSchoolSQL)) {
                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    public boolean updateSchool(School oldSchool, School newSchool) {
        return DBUtils.runInTransaction(conn -> {
            if (!schoolExists(conn)) {
                logger.warning("School does not exist.");
                return false;
            }

            StringBuilder sql = new StringBuilder("UPDATE School SET ");

            List<Object> parameters = new ArrayList<>();

            if (newSchool.getName() != null) {
                sql.append("Name = ?,");
                parameters.add(newSchool.getName());
            }
            if (newSchool.getPrincipal() != null) {
                sql.append(" Principal = ?,");
                parameters.add(newSchool.getPrincipal());
            }
            if (newSchool.getlocation() != null) {
                sql.append(" location = ?,");
                parameters.add(newSchool.getlocation());
            }

            sql.append(" WHERE id = 1");

            try (PreparedStatement rm = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < parameters.size(); i++) {
                    rm.setObject(i + 1, parameters.get(i));
                }

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    public List<School> listSchool(Connection conn) {
        List<School> schools = new ArrayList<>();
        String listSchoolSQL = "SELECT * FROM School";

        try (PreparedStatement rm = conn.prepareStatement(listSchoolSQL);
                ResultSet rs = rm.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No Data is available.");
                return new ArrayList<>();
            }

            while (rs.next()) {
                School school = new School();
                school.setId(rs.getInt("id"));
                school.setName(rs.getString("Name"));
                school.setPrincipal(rs.getString("Principal"));
                schools.add(school);
            }
            return schools;

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list School", e);
        }
        return new ArrayList<>();
    }

}
