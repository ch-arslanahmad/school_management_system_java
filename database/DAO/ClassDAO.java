// BETA VERSION FINALIZED - ClassDAO
package database.DAO;

// package imports
import database.*;
import display.ConsoleDisplay;
import people.Student;
import classroom.*;
// imports
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

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
             * // so logging is not shown in console LogManager.getLogManager().reset();
             */
            String a = "log/ClassDAO.txt";
            File file = new File(a);
            // if file does not exist, create it
            if (!(file.exists())) {
                file.createNewFile();
            }
            fh = new FileHandler(a, 1024 * 1024, 1, true); // path, size, n of files, append or not
            fh.setLevel(Level.ALL);

            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());

            // checking if file exists
            if (file.exists()) {
                logger.info("Log File is created!");
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                closeLog();
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // USE THIS METHOD IN EVERY FILE WHICH HAS LOG
    public static void closeLog() {
        logger.info("Log File is closed.");
        fh.flush();
        fh.close();
    }

    String classNameDAO;
    int classID;

    ClassRoom room = new ClassRoom();

    // method to get ID from Class (-1 Error Code)
    public int getIDfromClass(String name) {
        String classIDSQL = "SELECT ClassID FROM Class where ClassName = ?;";
        // No reasonable ID will reach this amount, hence the reason of this value
        classID = -1;
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(classIDSQL)) {

            rm.setString(1, name);

            ResultSet rs = rm.executeQuery();

            if (rs.next()) {
                // fetches and stores the ClassID in a variable from the matched row
                classID = rs.getInt("ClassID");
                logger.info(name + " ID is: " + classID);
            } else {
                logger.warning("Unable to get ClassID.");
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
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(check)) {

            // adding value to query
            rm.setString(1, name);

            /*
             * 1. We use executeUpdate() because we are not adding, removing anything we are
             * simply executing the query to extract some value. 2. executeQuery returns
             * value in ResultSet Object hence that value is also stored ResultSet variable,
             * 'rs'.
             */

            try (ResultSet rs = rm.executeQuery()) {

                /*
                 * ResultSet (rs) returns a table with one row. It does not automatically point
                 * to the matched column. To do that you do rs.next(). - res.next() returns row.
                 * - true if more than 1 - false if 0
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

    // to insert a class
    public boolean insertClass(String name) {
        // SQL query
        String classSQL = "INSERT INTO Class (ClassName) VALUES (?)";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(classSQL);) {
            // set values in the query
            rm.setString(1, name);

            // execute query
            int rs = rm.executeUpdate();

            System.out.println(rs);
            if (rs > 0 || rs == 1) {
                // confirmation
                logger.info("Class Added");
                conn.commit(); // commit
                return true;
            } else {
                logger.warning("Class Not Added.");
                conn.rollback(); // rollback
                return false;
            }

        } catch (Exception e) {
            // writing text with errors
            logger.log(Level.WARNING, "Error while Inserting Class: ", e);
        }
        return false;
    }

    public boolean deleteClass(String name) {
        if (!(Classexists(name))) {
            logger.warning("No Match found");
            return false;
        }
        String deleteClassSQL = "DELETE FROM Class WHERE ClassName = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(deleteClassSQL)) {
            // set values in the query
            rm.setString(1, name);

            // execute query
            int rs = rm.executeUpdate();

            if (rs > 0 || rs == 1) {
                // confirmation
                logger.info("Class Deleted");
                conn.commit(); // commit
                return true;
            } else {
                logger.warning("Class unable to delete.");
                conn.rollback();
                return false; // rollback
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while deleting Class: ", e);
        }

        return false;

    }

    // update class row (classname)
    public boolean updateClass(String name, String updateName) {

        if (!Classexists(name)) {
            logger.warning("Class Doesnt exist.");
            return false;
        }

        if (updateName == null || updateName.isEmpty()) {
            logger.warning("New ClassName cannot be empty.");
            return false;
        }

        if (Classexists(updateName)) {
            logger.warning("'" + updateName + "' name already exists.");

        }

        String updateClass = "UPDATE Class SET ClassName = ? WHERE ClassName = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(updateClass)) {
            conn.setAutoCommit(false); // start the transaction
            rm.setString(1, updateName);
            rm.setString(2, name);

            int rs = rm.executeUpdate();
            if (rs > 0) {
                conn.commit(); // commit if true
                logger.info("Committed Successfully");
                return true;
            } else {
                conn.rollback(); // rollback if error
                logger.info("Committed Successfully");
                return false;
            }
            // for the most part, as autocommit is disabled.
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while updating Class: ", e);
        }
        return false;
    }

    ConsoleDisplay display = new ConsoleDisplay();

    // list all Class+ -- FIX THIS - THIS WILL REQUIRE SOMETHING MORE DETAILED AS IT
    // MAY BE BEST TO LIST:
    // - list all classes
    // - list all subjects of the classes
    // - list all students of the class
    public List<ClassRoom> listAll() {
        List<ClassRoom> rooms = new ArrayList<>();

        // Query to list all Class
        String listAllSQL = "SELECT Class.ClassName, Subjects.SubjectName, Student.StudentName "
                + "FROM Class " + "LEFT JOIN Student ON Student.ClassID = Class.ClassID "
                + "LEFT JOIN Subjects ON Subjects.ClassID = Class.ClassID";
        // try-block
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(listAllSQL)) {

            // variable to count total rows printed
            // inner try-block to fetch and display each row
            try (ResultSet rs = rm.executeQuery()) {
                // System.out.printf("%-20s | %-20s | %-20s\n", "Class", "Subject", "Student");

                // loop to display every row
                while (rs.next()) {
                    ClassRoom room = new ClassRoom(rs.getString("ClassName"),
                            new Subjects(rs.getString("SubjectName")),
                            new Student(rs.getString("StudentName")));
                    rooms.add(room);
                    // display each row
                    // display.displayf(rs.getString("ClassName"), rs.getString("SubjectName"),
                    // rs.getString("StudentName"));
                }
                return rooms;
            } catch (SQLException e) {
                logger.log(Level.WARNING,
                        "Error while executing Query to List classes with details: ", e);
            }

        } catch (SQLException e) {

            logger.log(Level.WARNING, "Error while listing Classes: ", e);
        }

        return new ArrayList<>();

    }

    // list All Classes
    public List<ClassRoom> listClass() {
        List<ClassRoom> classroom = new ArrayList<>();
        String listClassSQL = "SELECT ClassName FROM Class";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(listClassSQL)) {
            try (ResultSet rs = rm.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No Data is available.");
                    return new ArrayList<>();
                } else {
                    while (rs.next()) {
                        classroom.add(new ClassRoom(rs.getString("ClassName")));
                    }
                    return classroom;
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Unable to return Classes: ", e);
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list all Classes", e);
        }

        return new ArrayList<>();
    }

    // GET FEE OF A CLASS
    public ClassRoom getClassFees(String Class) {
        String feeSQL = "SELECT Tuition_Fee, Stationary_Fee, Paper_Fee FROM Class WHERE ClassName = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(feeSQL)) {
            rm.setString(1, Class);
            try (ResultSet rs = rm.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No Data is available.");
                }
                while (rs.next()) {
                    return new ClassRoom(rs.getInt("Tuition_Fee"), rs.getInt("Stationary_Fee"),
                            rs.getInt("Paper_Fee"));
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error while returning Fees of a Class: ", e);
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while get Fees of a Class: ", e);
        }
        return new ClassRoom();
    }

}
