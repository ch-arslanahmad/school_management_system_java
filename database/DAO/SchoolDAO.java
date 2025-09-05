package database.DAO;

import school.School;

// imports
import java.sql.*;
import java.util.logging.*;

import database.Database;
import display.LogHandler;

public class SchoolDAO {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(SchoolDAO.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "SchoolDAO");
    }

    public School getSchoolInfo() {
        String sql = "SELECT * FROM School";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(sql)) {
            ResultSet rs = rm.executeQuery();

            if (rs.next()) {
                return new School(rs.getString("Name"), rs.getString("Principal"),
                        rs.getString("location"));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error fetching School info: ", e);
        }
        return null;
    }

    // In DB there is a check that doesnt allow more than one row in School info
    // table, hence why updating is the best option, rather than adding, deleting
    // rows, simply updating would be the best option

    public boolean updateSchool(String name, String principal, String location) {
        String sql = "UPDATE School SET Name = ?, Principal = ?, location = ? WHERE id = 1";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(sql)) {

            rm.setString(1, name);
            rm.setString(2, principal);
            rm.setString(3, location);

            int rs = rm.executeUpdate();
            if (rs > 0) {
                logger.info("Updated Successful.");
                conn.commit();
                return true;
            } else {
                logger.info("School-Info Updated Unsuccessful.");
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while updating School Info: ", e);
        }
        return false;
    }

}
