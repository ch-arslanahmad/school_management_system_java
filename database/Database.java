package database;

import java.io.File;
import java.sql.*;
import java.util.logging.*;

import display.LogHandler;

public class Database {

    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(Database.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "Database");
    }

    // DATABASE CONNECTION

    // setupConnection of DB
    public static Connection getConnection() {
        String path = "storage/people.db";
        // Ensure storage directory exists so SQLite can create the DB file if missing
        try {
            File storageDir = new File("storage");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
        } catch (SecurityException se) {
            logger.log(Level.WARNING, "Unable to create storage directory", se);
        }

        Connection conn = null;
        try {
            // Load JDBC Driver
            Class.forName("org.sqlite.JDBC");
            // Connecting the database file
            conn = DriverManager.getConnection("jdbc:sqlite:" + path);
            conn.setAutoCommit(false); // universal auto commit - disabled
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error establishing connection with database/sqlite", e);
        }

        return conn;
    }

}
