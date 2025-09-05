package database;

import java.io.File;
import java.sql.*;
import java.util.logging.*;

import display.LogHandler;

public class DBManager {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(DBManager.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "DBManager");
    }

    // Checking, does DB file exists?
    public boolean DBfileExists() {
        File DB = new File("database/people.db");
        if (DB.exists()) {
            String checkStructure = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'";
            try (Connection conn = Database.getConnection();
                    PreparedStatement rm = conn.prepareStatement(checkStructure)) {

                ResultSet rs = rm.executeQuery();
                if (rs.next()) {
                    logger.info("The file exists with a structure.");
                    return true;
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while checking DB: ", e);
            }
        }
        return false;
    }

    // to test one table if it has data
    public boolean testTable(String table) {
        String sqlQuery = "SELECT COUNT(*) FROM " + table;

        try (Connection conn = Database.getConnection(); Statement rm = conn.createStatement()) {
            boolean rs = rm.execute(sqlQuery);
            if (rs) {
                logger.info("the table " + table + " exist & is not empty. ");
                return true;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while validating Table " + table + " in DB: ", e);
        }
        return false;
    }

    // validate Database with Data
    public boolean DBvalidate() {
        if (DBfileExists()) {
            String[] tables = { "School", "Class", "Subjects", "Student", "Teacher", "Grade" };
            for (String t : tables) {
                if (!testTable(t)) {
                    return false;
                }
            }
            logger.fine("Data in Database File is available.");
            return true;
        }
        return false;

    }

    // to remove one table's data if it has data
    public boolean removeTable(String table) {
        String sqlQuery = "DELETE FROM " + table;

        try (Connection conn = Database.getConnection(); Statement rm = conn.createStatement()) {
            boolean rs = rm.execute(sqlQuery);
            if (rs) {
                logger.info("the table " + table + " exist & is not empty. ");
                return true;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while validating Table " + table + " in DB: ", e);
        }
        return false;
    }

    public boolean delDB() {
        if (!DBvalidate()) {
            return false;
        }
        String[] tables = { "Grade", "Student", "Teacher", "Subjects", "Class", "School",
                "sqlite_sequence" };

        try (Connection conn = Database.getConnection()) {
            // Disable foreign keys
            try (Statement rm = conn.createStatement()) {
                rm.execute("PRAGMA foreign_keys = OFF");
            }

            // Execute all delete statements of table
            for (String t : tables) {
                if (!removeTable(t)) {
                    logger.warning("Table" + t + " has a problem. ");
                    return false;
                }
            }

            // Re-enable foreign keys
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error clearing database data", e);
        }
        return false;
    }

}
