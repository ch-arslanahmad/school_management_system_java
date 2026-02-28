package database;

import java.io.File;
import java.sql.*;
import java.util.logging.*;

import display.LogHandler;

public class DBValidator {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(DBValidator.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "DBValidator");
    }

    // Checking, does DB file exists?
    public boolean DBfileExists() {
        File DB = new File("storage/people.db");
        if (DB.exists()) {
            String checkStructure = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'";
            try (Connection conn = Database.getConnection();
                    Statement rm = conn.createStatement()) {

                ResultSet rs = rm.executeQuery(checkStructure);
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

        try (Connection conn = Database.getConnection(); Statement rm = conn.createStatement();) {
            ResultSet rs = rm.executeQuery(sqlQuery);
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    logger.info("the table " + table + " exist & has data.");
                    return true;
                }
                logger.info("the table " + table + " exists but is empty.");
                return false;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while validating Table " + table + " in DB: ", e);
        }
        return false;
    }

    public boolean tableExists(String table) {
        String sqlQuery = "SELECT 1 FROM " + table + " LIMIT 1";

        try (Connection conn = Database.getConnection(); Statement rm = conn.createStatement();) {
            rm.executeQuery(sqlQuery);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // validate Database with Data
    public boolean DBvalidate() {
        if (DBfileExists()) {
            String[] tables = { "School", "Class", "Subjects", "Student", "Teacher", "StudentMarks" };
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

    // to remove one table's data
    public boolean removeTable(String table) {
        String sqlQuery = "DELETE FROM " + table;

        try (Connection conn = Database.getConnection(); Statement rm = conn.createStatement()) {
            int rowsAffected = rm.executeUpdate(sqlQuery);
            logger.info("Deleted " + rowsAffected + " rows from table " + table);
            return true;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while deleting Table " + table + " in DB: ", e);
        }
        return false;
    }

    public boolean delDB() {
        if (!DBvalidate()) {
            return false;
        }
        String[] tables = { "StudentMarks", "Student", "Teacher", "Subjects", "Class", "School",
                "sqlite_sequence" };

        try (Connection conn = Database.getConnection()) {
            try (Statement rm = conn.createStatement()) {
                rm.execute("PRAGMA foreign_keys = OFF");
            }

            for (String t : tables) {
                String sqlQuery = "DELETE FROM " + t;
                try (Statement rm = conn.createStatement()) {
                    int rows = rm.executeUpdate(sqlQuery);
                    logger.info("Deleted " + rows + " rows from table " + t);
                }
            }

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error clearing database data", e);
        }
        return false;
    }

}
