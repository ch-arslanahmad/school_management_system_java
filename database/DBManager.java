package database;
import java.io.File;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.*;

public class DBManager {
    private static final Logger logger = Logger.getLogger(testing.class.getName());
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


    Connection conn;
    // setupConnection of DB
    boolean setupConnection(Scanner input) {
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


    // choice

    void choose(Scanner input) {
        System.out.print(
            "\n========== School Management System ==========\n" +
            "1. Add New Class\n" +
            "2. Add New Subject (linked to a Class)\n" +
            "3. Add New Teacher (linked to a Subject)\n" +
            "4. Add New Student (linked to a Class)\n" +
            "5. Exit\n" +
            "===============================================\n" +
            "Enter your choice: "
        );
        int choice =  input.nextInt();



    }



}
