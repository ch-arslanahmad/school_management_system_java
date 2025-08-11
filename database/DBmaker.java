package database;
import database.DAO.*;

import java.io.File;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.*;

public class DBmaker {
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



    
    // METHOD to create whole database
    void createDB(Scanner input) {
        DBmaker db = new DBmaker();
        ClassDAO classdb = new ClassDAO();
        Database.setupConnection(input);

        System.out.println("To Use this APP you must create a database.");
        System.out.println("First lets add a class.");
        // added classes
        classdb.insertClasses(input);

        // add subjects to the classes

    }
}