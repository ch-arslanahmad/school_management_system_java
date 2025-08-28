package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.logging.*;

public class Database {

    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(Database.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**
    static {
        //
        try {
            /*
             * // so logging is not shown in console LogManager.getLogManager().reset();
             */
            String a = "log/Database.txt";
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeLog() {
        fh.flush();
        fh.close();
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
