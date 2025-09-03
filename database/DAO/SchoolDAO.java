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

    public boolean insertSchool(String name, String principal, String location) {
        String sql = "INSERT INTO School (Name, Principal, location) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setString(1, name);
            rm.setString(2, principal);
            rm.setString(3, location);
            int rs = rm.executeUpdate();

            if (rs > 0) {
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "ERROR inserting SchoolInfo: ", e);
        }
        return false;
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
        return new School();
    }

    public void updateSchool(School school) {
        String sql = "UPDATE School SET Name = ?, Principal = ?, location = ? WHERE id = 1";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(sql)) {

            rm.setString(1, school.getName());
            rm.setString(2, school.getPrincipal());
            rm.setString(3, school.getlocation());

            int rs = rm.executeUpdate();
            if (rs > 0) {
                logger.info("Updated Successful.");
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while updating School Info: ", e);
        }
    }

    public void deleteSchool(String schoolName) {
        String sql = "DELETE FROM School WHERE Name = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setString(1, schoolName);
            int rs = rm.executeUpdate();

            if (rs > 0) {
                logger.info("Deleted" + schoolName);
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while deleting School Info: ", e);

        }
    }

    /*
     * public List<Subjects> studentReport() { String studentReport = ""; try
     * (Connection conn = Database.getConnection(); PreparedStatement rm =
     * conn.prepareStatement(studentReport)) {
     * 
     * } catch (Exception e) { logger.log(Level.WARNING,
     * "Error making fetching Student Report Details", e); throw e; } }
     */

}
