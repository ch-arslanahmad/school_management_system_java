// BETA VERSION FINALIZED - SchoolDAO
package database.DAO;

// package imports
import display.LogHandler;
import school.School;

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

    public boolean insertSchool(Connection conn, School school) {
        try {
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
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error inserting school", e);
            return false;
        }
    }

    public boolean deleteSchool(Connection conn, School school) {
        try {
            String deleteSchoolSQL = "DELETE FROM School WHERE id = 1";
            try (PreparedStatement rm = conn.prepareStatement(deleteSchoolSQL)) {
                int rs = rm.executeUpdate();
                return rs > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error deleting school", e);
            return false;
        }
    }

    public boolean updateSchool(Connection conn, School school) {
        try {
            if (!schoolExists(conn)) {
                logger.warning("School does not exist.");
                return false;
            }

            StringBuilder sql = new StringBuilder("UPDATE School SET ");
            List<Object> params = new ArrayList<>();

            if (school.getName() != null && !school.getName().isEmpty()) {
                sql.append("Name = ?,");
                params.add(school.getName());
            }
            if (school.getPrincipal() != null && !school.getPrincipal().isEmpty()) {
                sql.append("Principal = ?,");
                params.add(school.getPrincipal());
            }
            if (school.getlocation() != null && !school.getlocation().isEmpty()) {
                sql.append("location = ?,");
                params.add(school.getlocation());
            }

            if (params.isEmpty()) {
                logger.warning("No fields to update.");
                return false;
            }

            sql.setLength(sql.length() - 1); // removing trailing ','
            sql.append(" WHERE id = 1");

            try (PreparedStatement rm = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    rm.setObject(i + 1, params.get(i));
                }
                int updated = rm.executeUpdate();
                return updated > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error updating school", e);
            return false;
        }
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
                school.setLocation(rs.getString("location"));
                schools.add(school);
            }
            return schools;

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list School", e);
        }
        return new ArrayList<>();
    }

}
