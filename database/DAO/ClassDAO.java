// imports
import java.io.File;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.*;

import school_management_system_java.classroom.*;
import school_management_system_java.classroom.database.DBmaker;

// public class
public class ClassDAO {

    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(ClassDAO.class.getName());
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


    // see if class exists
    public boolean Classexists(Scanner input) {

        // get class name for comparison
        System.out.println("Enter class name: ");
        String classname = input.nextLine();

        // SQL query to check
        String check = "SELECT COUNT(*) FROM Class WHERE ClassName = ?";

        // prepared statement
        PreparedStatement rm = Database.getConn().prepareStatement(check);

        // adding value to query
        rm.setString(1, classname);

/*
1. We use executeQuery() not executeUpdate() because we are not adding, removing anything we are simply executing the query.
2.  executeQuery returns value in ResultSet Object hence that value is also stored ResultSet variable, 'rs'.
*/

        ResultSet rs = rm.executeQuery();

/*
ResultSet (rs) returns a table even if its only 1 column as matching.
It does not automatically point to the matched column. To do that you do rs.next().
        - res.next() will return false if rs is empty due to no match.
        - return true if it is not empty
I used rs.getInt("ClassID"), it is self-explanatory. It gets the classid of the match. Useful if user wants to add data to the matched class.

NOTE: THINK OF MAKING A METHOD TO SEE THE ID FROM NAME LIKE getIDClass() or smthing.
 */

        if(rs.next()) {
            System.out.println("Match found");
            int classid = rs.getInt("ClassID");
            return true;
        }
        else {
            System.out.println("No match found");
        }

        return false;

    }

    // to insert 1 class
    public boolean insertClass(Scanner input) {
        try {
            ClassRoom room = new ClassRoom();
            // input class name
            room.addClass(input);
            // SQL query
            String classSQL = "INSERT INTO Class (ClassName) VALUES (?)";
            PreparedStatement rm = Database.getConn().prepareStatement(classSQL);

            // set values in the query
            rm.setString(1, room.getClassroom());

            // execute query
            rm.executeUpdate();

            // confirmation
            System.out.println("Class Added");
            return true;       
        } catch (Exception e) {
        }
        return false;
    }


    // to insert many classes
    public boolean insertClasses(Scanner input) {
        // adding Class
        while (true) {      
            try {
            boolean classAdded = insertClass(input);
                if(classAdded) {
                    logger.info("Class Successfully entered");
                }
                else {
                    System.out.println("Class must be entered to continue.");
                    continue;
                }
                System.out.println("Press 0 to stop.");
                int c;
                c = input.nextInt();
                if(c == 0) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Error Adding Class Data");
                e.printStackTrace();
            }
        }
    } 
    
}
