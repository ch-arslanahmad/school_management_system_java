package database.DAO;

import school.School;

import java.io.File;
// imports
import java.sql.*;
import java.util.logging.*;

import database.Database;

public class SchoolDAO {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(StudentDAO.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**
    static {
        //
        try {
            /*
             * // so logging is not shown in console LogManager.getLogManager().reset();
             */
            String a = "log/StudentDAOlog.txt";
            File file = new File(a);
            // if file does not exist, create it
            if (!(file.exists())) {
                file.createNewFile();
            }
            fh = new FileHandler(a, true);
            fh.setLevel(Level.FINE);
            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());

            // checking if file exists
            if (file.exists()) {
                logger.info("Log File is created!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertSchool(School school) throws SQLException {
        String sql = "INSERT INTO School (Name, Principal) VALUES (?, ?)";
        try (Connection conn = Database.getConn();
                PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setString(1, school.getName());
            rm.setString(2, school.getPrincipal());
            rm.executeUpdate();
        }
    }

    public School getSchoolInfo() throws SQLException {
        String sql = "SELECT Name, Principal FROM School";
        try (Connection conn = Database.getConn();
                PreparedStatement rm = conn.prepareStatement(sql)) {
            ResultSet rs = rm.executeQuery();

            if (rs.next()) {
                return new School(rs.getString("Name"), rs.getString("Principal"));
            }
        }
        return null;
    }

    public void updateSchool(School school) throws SQLException {
        String sql = "UPDATE School SET Name = ?, Principal = ? WHERE id = 1";
        try (Connection conn = Database.getConn();
                PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setString(1, school.getName());
            rm.setString(2, school.getPrincipal());
            rm.setInt(3, school.getId());
            rm.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating School");
            throw e;
        }
    }

    public void deleteSchool(String schoolName) throws SQLException {
        String sql = "DELETE FROM School WHERE Name = ?";
        try (Connection conn = Database.getConn();
                PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setString(1, schoolName);
            int rs = rm.executeUpdate();

            if (rs > 0) {
                logger.info("Deleted" + schoolName);
            }

        } catch (SQLException e) {
            System.err.println("Error while deleting School.");
            throw e;
        }
    }

    /*
     * public List<Subjects> studentReport() throws SQLException { String
     * studentReport = ""; try (Connection conn = Database.getConn();
     * PreparedStatement rm = conn.prepareStatement(studentReport)) {
     * 
     * } catch (Exception e) { logger.log(Level.WARNING,
     * "Error making fetching Student Report Details", e); throw e; } }
     */

}
