package database.DAO;

// package imports
import database.*;
import display.ConsoleDisplay;

// imports
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import classroom.Subjects;

import java.sql.*;

public class SubjectDAO {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(SubjectDAO.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**
    static {
        //
        try {
            /*
             * // so logging is not shown in console LogManager.getLogManager().reset();
             */
            String a = "log/SubjectDAOlog.txt";
            File file = new File(a);
            // if file does not exist, create it
            if (!(file.exists())) {
                file.createNewFile();
            }
            fh = new FileHandler(a, true);
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

    // declaring global object of ClassDAO
    ClassDAO check = new ClassDAO();

    // NOTE for future reference: accept the method only if valid ClassName is
    // provided

    int subjectID;

    // method to get ID from Subject
    public int getIDfromSubject(String name) {
        String subjectIDSQL = "SELECT SubjectID FROM Subjects where SubjectName = ?;";
        subjectID = -1;
        try (PreparedStatement rm = Database.getConn().prepareStatement(subjectIDSQL)) {

            rm.setString(1, name);

            ResultSet rs = rm.executeQuery();

            if (rs.next()) {
                // fetches and stores the SubjectID in a variable from the matched row
                subjectID = rs.getInt("SubjectID");
                logger.info(name + " ID is: " + subjectID);
            } else {
                logger.info("Unable to get SubjectID.");
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while fetching SubjectID.", e);
        }

        return subjectID;

    }

    // method to get Valid SubjectID

    public int getValidSubjectID(String name) {
        if (!subjectExists(name)) {
            logger.warning("Subject doesnt exist.");
            return -1;
        } else {
            logger.warning("Status of Fetched ID: ");
            return getClassIdBySubject(name);
        }
    }

    // method to get ClassID of Subject

    public int getClassIdBySubject(String name) {
        if (!subjectExists(name)) {
            logger.info("Subject does not exist.");
        } else {
            // SQL Query
            String classIdSQL = "SELECT ClassID FROM Subjects WHERE SubjectName = ?";

            // try-catch block
            try (PreparedStatement rm = Database.getConn().prepareStatement(classIdSQL)) {

                // inserting value in Query
                rm.setString(1, name);

                ResultSet rs = rm.executeQuery();

                // fetching SubjectID
                if (rs.next()) {
                    return rs.getInt("SubjectID");
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while fetching ClassID of subject", e);
            }
        }
        // automated error code
        return -1;
    }

    // see if subject exists
    public boolean subjectExists(String name) {
        // SQL query to check
        String ExistSQL = "SELECT COUNT(*) AS count FROM Subjects WHERE SubjectName = ?;";

        // prepared statement in try block
        try (PreparedStatement rm = Database.getConn().prepareStatement(ExistSQL)) {

            // adding value to query
            rm.setString(1, name);

            /*
             * 1. We use executeUpdate() because we are not adding, removing anything we are
             * simply executing the query. 2. executeQuery returns value in ResultSet Object
             * hence that value is also stored ResultSet variable, 'rs'.
             */

            ResultSet rs = rm.executeQuery();

            /*
             * ResultSet (rs) returns a table with one row. It does not automatically point
             * to the matched column. To do that you do rs.next(). - res.next() returns row.
             * - true if more than 1 - false if 0
             */

            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    logger.info("Match found");
                    return true;
                } else {
                    logger.warning("No match found");
                    return false;
                }
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error finding Subject Existance: ", e);
        }
        return false;
    }

    // Insert 1 Subject in DB
    public boolean insertSubject(String ClassName, String name) {
        int classID = check.getValidClassID(ClassName);
        // STOP if return ERROR code (-1)
        if (classID == -1) {
            logger.info("Incorrect ID");
        } else {
            // assign a variable to the classID.

            // SQL Query
            String subjectSQL = "INSERT INTO Subjects (SubjectName, ClassID) VALUES (?,?)";
            try (PreparedStatement rm = Database.getConn().prepareStatement(subjectSQL)) {
                // set values in the query
                rm.setString(1, name);
                rm.setInt(2, classID);

                // execute query
                int rs = rm.executeUpdate();

                if (rs > 0) {
                    // confirmation
                    logger.info("Subject Added");
                    return true;
                } else {
                    logger.config("Subject Not Added.");
                    return false;
                }
            } catch (Exception e) {
                // writing text with errors
                logger.log(Level.WARNING, "Error while Inserting Subject: ", e);
            }
        }
        return false;
    }

    public boolean updateSubject(String ClassName, String name, String updateName) {
        if (check.getValidClassID(ClassName) == -1) {
            logger.config("Class doesn't exist.");
        } else {
            if (!subjectExists(name)) {
                logger.config("Subject doesn't exist.");
            } else {
                // Query to update Subject
                String updateSubjSQL = "UPDATE Subjects SET SubjectName = ? WHERE SubjectName = ? AND ClassID = ?";
                try (PreparedStatement rm = Database.getConn().prepareStatement(updateSubjSQL)) {
                    rm.setString(1, updateName);
                    rm.setString(2, name);
                    rm.setInt(3, check.getValidClassID(ClassName));

                    int rs = rm.executeUpdate();
                    if (rs > 0) {
                        return true;
                    } else {
                        logger.warning("Unable to update SubjectName. ");
                        return false;
                    }

                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error while updating subject: ", e);
                }
            }
        }
        return false;

    }

    public boolean deleteSubject(String ClassName, String name) {
        if (!(subjectExists(name))) {
            System.out.println("No Match found");
        } else {
            String delSubjectSQL = "DELETE FROM Subjects WHERE SubjectName = ? AND ClassID = ?";
            try (PreparedStatement rm = Database.getConn().prepareStatement(delSubjectSQL)) {

                // set values in the query
                rm.setString(1, name);
                rm.setInt(2, getClassIdBySubject(name));

                // execute query
                int rs = rm.executeUpdate();

                if (rs > 0) {
                    // confirmation
                    logger.info("Subject Deleted");
                    return true;
                } else {
                    logger.config("Subject unable to delete.");
                    return false;
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while deleting Subject: ", e);
            }
        }

        return false;
    }

    ConsoleDisplay display = new ConsoleDisplay();

    // list all subjects
    public List<Subjects> listSubjects() {
        List<Subjects> subjects = new ArrayList<>();
        // Query to list all Subjects
        String listSubjectSQL = "SELECT Subjects.SubjectName, Class.ClassName " + "FROM Subjects "
                + "LEFT JOIN Class ON Subjects.ClassID = Class.ClassID";
        // try-block
        try (Connection conn = Database.getConn();
                PreparedStatement rm = Database.getConn().prepareStatement(listSubjectSQL)) {
            // variable to count total rows printed
            int count = 0;
            // inner try-block to fetch and display each row
            try (ResultSet rs = rm.executeQuery()) {
                // loop to display every row
                while (rs.next()) {
                    subjects.add(
                            new Subjects(rs.getString("SubjectName"), rs.getString("ClassName")));
                    count++;
                }
                logger.log(Level.FINE, count + " Subjects added");
                return subjects;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while executing Query to List Subjects: ", e);
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while listing Subjects: ", e);
        }

        return null;

    }

}
