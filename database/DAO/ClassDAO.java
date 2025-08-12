// BETA VERSION FINALIZED - ClassDAO
package database.DAO;

// package imports
import database.*;
import classroom.*;
// imports
import java.io.File;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.*;

import javax.security.auth.Subject;

// public class
public class ClassDAO {

    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(ClassDAO.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**
    static {
        //
        try {
            /*
             * // so logging is not shown in console
             * LogManager.getLogManager().reset();
             */
            String a = "ClassDAO.txt";
            File file = new File(a);
            // if file does not exist, create it
            if (!(file.exists())) {
                file.createNewFile();
            }
            fh = new FileHandler(a, true);
            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
            logger.setLevel(Level.FINE);

            // checking if file exists
            if (file.exists()) {
                logger.info("Log File is created!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String classNameDAO;
    int classID;

    ClassRoom room = new ClassRoom();

    // method to get ID from Class (-1 Error Code)
    public int getIDfromClass(String name) {
        String classIDSQL = "SELECT ClassID FROM Class where ClassName = ?;";
        // No reasonable ID will reach this ammount, hence the reason of this value
        classID = -1;
        try (PreparedStatement rm = Database.getConn().prepareStatement(classIDSQL)) {

            rm.setString(1, name);

            ResultSet rs = rm.executeQuery();

            if (rs.next()) {
                // fetches and stores the ClassID in a variable from the matched row
                classID = rs.getInt("ClassID");
                logger.info(name + " ID is: " + classID);
            } else {
                logger.config("Unable to get ClassID.");
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while fetching ClassID.", e);
        }

        return classID;

    }

    // see if class exists
    public boolean Classexists(String name) {
        // SQL query to check
        String check = "SELECT COUNT(*) AS count FROM Class WHERE ClassName = ?;";

        // prepared statement in try block
        try (PreparedStatement rm = Database.getConn().prepareStatement(check)) {

            // adding value to query
            rm.setString(1, name);

            /*
             * 1. We use executeUpdate() because we are not adding, removing anything we are
             * simply executing the query to extract some value.
             * 2. executeQuery returns value in ResultSet Object hence that value is also
             * stored ResultSet variable, 'rs'.
             */

            try (ResultSet rs = rm.executeQuery()) {

                /*
                 * ResultSet (rs) returns a table with one row.
                 * It does not automatically point to the matched column. To do that you do
                 * rs.next().
                 * - res.next() returns row.
                 * - true if more than 1
                 * - false if 0
                 * 
                 * 
                 * NOTE: THINK OF MAKING A METHOD TO SEE THE ID FROM NAME LIKE getIDClass() or
                 * smthing.
                 */

                if (rs.next()) {
                    int count = rs.getInt("count");
                    if (count > 0) {
                        System.out.println("Match found");
                        return true;
                    } else {
                        System.out.println("No match found");
                        return false;
                    }
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error Executing ClassExists Query");
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error finding Class Existance: ", e);
        }
        return false;
    }

    // method to get Validated ID from Class (-1 Error Code)
    public int getValidClassID(String name) {
        // getID only if class exists
        if (!Classexists(name)) {
            logger.info("Class Does Not exist");
            return -1;
        } else {
            // return the ID from class
            return getIDfromClass(name);
        }

    }

    // to insert 1 class
    public boolean insertClass(String name) {
        String classSQL = "INSERT INTO Class (ClassName) VALUES (?)";
        try (PreparedStatement rm = Database.getConn().prepareStatement(classSQL);) {
            // SQL query

            // set values in the query
            rm.setString(1, name);

            // execute query
            int rs = rm.executeUpdate();

            if (rs > 0) {
                // confirmation
                logger.info("Class Added");
                return true;
            } else {
                logger.config("Class Not Added.");
                return false;
            }

        } catch (Exception e) {
            // writing text with errors
            logger.log(Level.WARNING, "Error while Inserting Class: ", e);
        }
        return false;
    }

    // to insert many classes
    public boolean insertClasses(Scanner input) {
        // adding Classes
        while (true) {
            try {
                boolean classAdded = insertClass(room.setClass(input));
                if (classAdded) {
                    logger.finest("Class Successfully entered");
                } else {
                    System.out.println("Class must be entered to continue.");
                }

                // to stop the loop (user enters 0)
                // used string as input as using any other type may cause newline buffer problem
                System.out.println("Press 0 to stop.");
                String c;
                c = input.nextLine();

                if (c.equals("0")) {
                    return true;
                }
            } catch (Exception e) {
                logger.warning("Error Adding Class Data");
                e.printStackTrace();
            }
        }
    }

    public boolean deleteClass(String name) {
        if (!(Classexists(name))) {
            System.out.println("No Match found");
        } else {
            String deleteClassSQL = "DELETE FROM Class WHERE ClassName = ?";
            try (PreparedStatement rm = Database.getConn().prepareStatement(deleteClassSQL)) {

                // set values in the query
                rm.setString(1, name);

                // execute query
                int rs = rm.executeUpdate();

                if (rs > 0) {
                    // confirmation
                    logger.info("Class Deleted");
                    return true;
                } else {
                    logger.config("Class unable to delete.");
                    return false;
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while deleting Class: ", e);
            }
        }

        return false;

    }

    // list all Class -- FIX THIS - THIS WILL REQUIRE SOMETHING MORE DETAILED AS IT
    // MAY BE BEST TO LIST:
    // - list all classes
    // - list all subjects of the classes
    // - list all students of the class
    public boolean listClass() {
        // Query to list all Class
        String listSubjectSQL = "SELECT Class.ClassName, Subjects.SubjectName, Student.StudentName "
                + "FROM Class "
                + "JOIN Student ON Student.ClassID = Class.ClassID "
                + "JOIN Subjects ON Subject.ClassID = Class.ClassID";
        // try-block
        try (PreparedStatement rm = Database.getConn().prepareStatement(listSubjectSQL)) {
            // variable to count total rows printed
            int count = 0;
            // inner try-block to fetch and display each row
            try (ResultSet rs = rm.executeQuery()) {
                // loop to display every row
                while (rs.next()) {
                    // display each row
                    displayf(rs.getString("ClassName"), rs.getString("SubjectName"), rs.getString("StudentName"));
                    count++;
                }
                // return true if classes are displayed
                return count > 0;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while executing Query to List classes with details: ", e);
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while listing Classes: ", e);
        }

        return false;

    }

}
