// BETA VERSION FINALIZED - ClassDAO
package database.DAO;

// package imports
import display.LogHandler;
import people.Student;
import classroom.*;
import database.DBUtils;

// imports
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

// public class
public class ClassDAO {

    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(ClassDAO.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "ClassDAO");
    }

    String classNameDAO;

    ClassRoom room = new ClassRoom();

    // method to get ID from Class (-1 Error Code)
    public int getIDfromClass(Connection conn, String name) {
        int classID;

        String classIDSQL = "SELECT ClassID FROM Class where ClassName = ?;";
        // No reasonable ID will reach this amount, hence the reason of this value
        classID = -1;
        try (PreparedStatement rm = conn.prepareStatement(classIDSQL); ResultSet rs = rm.executeQuery();) {

            rm.setString(1, name);

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
    public boolean ClassExists(Connection conn, String name) {
        // SQL query to check
        String check = "SELECT COUNT(*) AS count FROM Class WHERE ClassName = ?;";

        // prepared statement in try block
        try (PreparedStatement rm = conn.prepareStatement(check); ResultSet rs = rm.executeQuery()) {

            // adding value to query
            rm.setString(1, name);

            /**
             * Explains the choice between executeUpdate() and executeQuery() for JDBC
             * operations:
             * 
             * 1. We use {@link java.sql.Statement#executeUpdate(String)} when we are
             * modifying
             * the database (inserting, updating, deleting), or when we want to execute a
             * query that does not return a ResultSet. It returns the number of affected
             * rows.
             * 
             * 2. We use {@link java.sql.Statement#executeQuery(String)} when we want to
             * retrieve
             * data from the database. It returns the result in a
             * {@link java.sql.ResultSet},
             * which can be iterated to access the retrieved values.
             */

            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    logger.info("Match found, Class Exists.");
                    return true;
                } else {
                    logger.warning("No match found, Class Does Not Exist.");
                    return false;
                }
            }
        }

        catch (Exception e) {
            logger.log(Level.WARNING, "Error finding Class Existance: ", e);
        }
        return false;
    }

    // method to get Validated ID from Class (-1 Error Code)
    public int getValidClassID(Connection conn, String name) {
        // getID only if class exists
        if (!ClassExists(conn, name)) {
            logger.info("Class Does Not exist");
            return -1;
        } else {
            // return the ID from class
            return getIDfromClass(conn, name);
        }

    }

    // to insert a class
    public boolean insertClass(String name) {

        DBUtils.runInTransaction(conn -> {

            String classSQL = "INSERT INTO Class (ClassName) VALUES (?)";
            try (PreparedStatement rm = conn.prepareStatement(classSQL);) {
                // set values in the query
                rm.setString(1, name);

                // execute query
                int rs = rm.executeUpdate();

                return rs > 0; // return true if at least one row is affected, otherwise false
            }
        });
        return false;
    }

    // to insert a class with fees
    public boolean insertWithClassFees(String className, int tuition, int stationary, int exam) {

        DBUtils.runInTransaction(conn -> {

            if (ClassExists(conn, className)) {
                logger.warning("Class already exists.");
                return false;
            }

            String inputFees = "INSERT INTO Class(ClassName, Tuition_Fee,Stationary_Fee,Paper_Fee) VALUES(?,?,?,?)";

            try (PreparedStatement rm = conn.prepareStatement(inputFees)) {
                rm.setString(1, className);
                rm.setInt(2, tuition);
                rm.setInt(3, stationary);
                rm.setInt(4, exam);

                int rs = rm.executeUpdate();
                return rs > 0 || rs == 1; // return true if 1 row is affected.
            }
        });
        return false;
    }

    public boolean deleteClass(String name) {

        return DBUtils.runInTransaction(conn -> {
            if (!(ClassExists(conn, name))) {
                logger.warning("No Match found");
                return false;
            }
            String deleteClassSQL = "DELETE FROM Class WHERE ClassName = ?";
            try (PreparedStatement rm = conn.prepareStatement(deleteClassSQL)) {
                // set values in the query
                rm.setString(1, name);

                // execute query
                int rs = rm.executeUpdate();

                return rs > 0 || rs == 1;
            }
        });
    }

    // update class row (classname)
    public boolean updateClass(String name, String updateName) {

        return DBUtils.runInTransaction(conn -> {
            if (!ClassExists(conn, name)) {
                logger.warning("Class Doesnt exist.");
                return false;
            }

            if (ClassExists(conn, updateName)) {
                logger.warning("Updated Name: " + updateName + "' name already exists.");
                return false;
            }
            String updateClass = "UPDATE Class SET ClassName = ? WHERE ClassName = ?";
            try (PreparedStatement rm = conn.prepareStatement(updateClass)) {
                rm.setString(1, updateName);
                rm.setString(2, name);

                int rs = rm.executeUpdate();
                return rs > 0 || rs == 1; // return true if 1 row is affected.

            }
        });
    }

    // list all Class+ -- FIX THIS - THIS WILL REQUIRE SOMETHING MORE DETAILED AS IT
    // MAY BE BEST TO LIST:
    // - list all classes
    // - list all subjects of the classes
    // - list all students of the class
    public List<ClassRoom> listAll(Connection conn) {
        List<ClassRoom> rooms = new ArrayList<>();

        // Query to list all Class
        String listAllSQL = "SELECT Class.ClassName, Subjects.SubjectName, Student.StudentName "
                + "FROM Class " + "LEFT JOIN Student ON Student.ClassID = Class.ClassID "
                + "LEFT JOIN Subjects ON Subjects.ClassID = Class.ClassID";
        // try-block
        try (
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
    public List<ClassRoom> listClass(Connection conn) {
        List<ClassRoom> classroom = new ArrayList<>();
        String listClassSQL = "SELECT ClassName FROM Class";
        try (
                PreparedStatement rm = conn.prepareStatement(listClassSQL);
                ResultSet rs = rm.executeQuery()) {
            if (!rs.isBeforeFirst()) {
                System.out.println("No Data is available.");
                return new ArrayList<>();
            } else {
                while (rs.next()) {
                    classroom.add(new ClassRoom(rs.getString("ClassName")));
                }
                return classroom;
            }
        }

        catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list all Classes", e);
        }

        return new ArrayList<>();
    }

    public boolean updateClassFees(Connection conn, String className, int tuition, int stationary, int exam) {

        if (!ClassExists(conn, className)) {
            logger.warning("Class does not exist.");
            return false;
        }

        String inputFees = "UPDATE Class SET Tuition_Fee = ?, Stationary_Fee = ?, Paper_Fee = ? WHERE ClassName = ?";
        try (PreparedStatement rm = conn.prepareStatement(inputFees)) {
            rm.setInt(1, tuition);
            rm.setInt(2, stationary);
            rm.setInt(3, exam);
            rm.setString(4, className);

            /**
             * <p>
             * The executeUpdate() method is used for SQL statements that modify the
             * database (like INSERT, UPDATE, DELETE).
             * </p>
             */

            int rs = rm.executeUpdate();
            return rs > 0 || rs == 1; // true, if atleast 1 row is affected, otherwise false

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error updating Class fees: ", e);
        }
        return false;
    }

    // GET FEE OF A CLASS
    public ClassRoom getClassFees(Connection conn, String Class) {
        String feeSQL = "SELECT Tuition_Fee, Stationary_Fee, Paper_Fee FROM Class WHERE ClassName = ?";
        try (
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
