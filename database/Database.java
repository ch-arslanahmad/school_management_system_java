package database;
import java.io.File;
import java.sql.*;
import java.util.logging.*;
import java.util.Scanner;

public class Database {

    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(Database.class.getName());
    private static FileHandler fh;
    
    // STATIC block for **LOGGING**
    static {
    // 
    try {
        // so logging is not shown in console
        LogManager.getLogManager().reset();
        String a = "testlog.txt";
        File file = new File(a);
            // if file does not exist, create it
            if(!(file.exists())) {
                file.createNewFile();
            }
        fh = new FileHandler(a, true);
        logger.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
        logger.setLevel(Level.FINE);

        // checking if file exists
        if(file.exists()) {
            logger.info("Log File is created!");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }





    // DATABASE CONNECTION
    private static Connection conn;
    // setupConnection of DB
    static boolean setupConnection(Scanner input) {
        boolean isConnected = false;
        conn = null;
        try {
            // Load JDBC Driver
            Class.forName("org.sqlite.JDBC");
            // Connecting the database file
            conn = DriverManager.getConnection("jdbc:sqlite:people.db");
            // run only if connection is not null
            if(conn != null) {
                isConnected =  true;
                logger.info("Connection Established");                
            }
            // if connection not made
            else {
                logger.warning("Connection Problem");
            }

        } catch (Exception e) {
            System.out.print("Error establishing connection with database/sqlite: ");
            e.printStackTrace();

            logger.warning("Error establishing connection with database/sqlite");

        }
        finally {
            try {
               conn.close();             
            } catch (Exception e) {
                System.out.println("Connection not properly closed");
                e.printStackTrace();
            }
        }
        return isConnected;

    }

    static void closeCon() {
        try {
            conn.close();           
        } catch (Exception e) {
            System.out.println("Error while closing connection: ");
            e.printStackTrace();
        }
    }

    public static Connection getConn() {
        return conn;
    }
}
