package database.DAO;

import school.School;

import java.io.File;
// imports
import java.sql.*;
import java.util.logging.*;

import database.Database;

public class SchoolDAO {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(SchoolDAO.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**
    static {
        //
        try {
            /*
             * // so logging is not shown in console LogManager.getLogManager().reset();
             */
            String a = "log/SchoolDAOlog.txt";
            File file = new File(a);
            // if file does not exist, create it
            if (!(file.exists())) {
                file.createNewFile();
            }
            fh = new FileHandler(a, 1024 * 1024, 1, true); // path, size, n of files, append or not
            fh.setLevel(Level.FINE);
            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());

            // checking if file exists
            if (file.exists()) {
                logger.info("Log File is created!");
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                closeLog();
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeLog() {
        logger.info("Log File is closed.");
        fh.flush();
        fh.close();
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
