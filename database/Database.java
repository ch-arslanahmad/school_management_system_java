package database;

import java.io.File;
import java.io.FileNotFoundException;
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
        String path = "database/people.db";
        try {
            File dbFile = new File("database/people.db");
            if (!dbFile.exists()) {
                throw new FileNotFoundException("File Not found: "); // stop if file is not found
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }
        Connection conn = null;
        try {
            // Load JDBC Driver
            Class.forName("org.sqlite.JDBC");
            // Connecting the database file
            conn = DriverManager.getConnection("jdbc:sqlite:" + path);
            conn.setAutoCommit(false); // universal auto commit - disabled
        } catch (Exception e) {
            System.out.print("Error establishing connection with database/sqlite: ");
            e.printStackTrace();

            logger.warning("Error establishing connection with database/sqlite");
        }

        return conn;
    }

}
