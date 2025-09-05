package database.DAO;

// package imports
import database.*;
import display.LogHandler;

// imports
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import classroom.ClassRoom;
import classroom.Subjects;

import java.sql.*;

public class SubjectDAO {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(SubjectDAO.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "SubjectDAO");
    }

    // declaring global object of ClassDAO
    ClassDAO check = new ClassDAO();

    // NOTE for future reference: accept the method only if valid ClassName is
    // provided

    int subjectID;

    // method to get ID from Subject
    public int fetchSubjectID(String name) {
        if (!subjectExists(name)) {
            return -1;
        }
        String subjectIDSQL = "SELECT SubjectID FROM Subjects where SubjectName = ?;";
        subjectID = -1;
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(subjectIDSQL)) {

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
            try (Connection conn = Database.getConnection();
                    PreparedStatement rm = conn.prepareStatement(classIdSQL)) {

                // inserting value in Query
                rm.setString(1, name);

                ResultSet rs = rm.executeQuery();

                // fetching ClassID
                if (rs.next()) {
                    return rs.getInt("ClassID");
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
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(ExistSQL)) {

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

    // Insert a Subject in DB
    public boolean insertSubject(String ClassName, String name, int Marks) {
        int classID = check.getValidClassID(ClassName);
        // STOP if return ERROR code (-1)
        if (classID == -1) {
            logger.info("Incorrect ID");
            return false;
        }

        // SQL Query
        String subjectSQL = "INSERT INTO Subjects (SubjectName, ClassID, Marks) VALUES (?,?,?)";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(subjectSQL)) {
            // set values in the query
            rm.setString(1, name);
            rm.setInt(2, classID);
            rm.setInt(3, Marks);

            // execute query
            int rs = rm.executeUpdate();

            if (rs > 0) {
                // confirmation
                logger.info("Subject Added");
                conn.commit(); // commmit if true
                return true;
            } else {
                logger.config("Subject Not Added.");
                conn.rollback(); // rollback if error
                return false;
            }
        } catch (Exception e) {
            // writing text with errors
            logger.log(Level.WARNING, "Error while Inserting Subject: ", e);
        }

        return false;
    }

    public boolean updateSubject(String ClassName, String name, String updateName) {
        if (check.getValidClassID(ClassName) == -1) {
            logger.config("Class doesn't exist.");
            return false;
        }
        if (!subjectExists(name)) {
            logger.config("Subject doesn't exist.");
            return false;
        }
        // Query to update Subject
        String updateSubjSQL = "UPDATE Subjects SET SubjectName = ? WHERE SubjectName = ? AND ClassID = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(updateSubjSQL)) {
            rm.setString(1, updateName);
            rm.setString(2, name);
            rm.setInt(3, check.getValidClassID(ClassName));

            int rs = rm.executeUpdate();
            if (rs > 0) {
                conn.commit(); // commit (if true)
                return true;
            } else {
                logger.warning("Unable to update SubjectName. ");
                conn.rollback(); // rollback (if error)
                return false;
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while updating subject: ", e);
        }

        return false;

    }

    public boolean deleteSubject(String ClassName, String name) {
        if (!(subjectExists(name))) {
            System.out.println("No Match found");
            return false;
        }

        String delSubjectSQL = "DELETE FROM Subjects WHERE SubjectName = ? AND ClassID = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(delSubjectSQL)) {

            // set values in the query
            rm.setString(1, name);
            rm.setInt(2, getClassIdBySubject(name));

            // execute query
            int rs = rm.executeUpdate();

            if (rs > 0) {
                // confirmation
                logger.info("Subject Deleted");
                conn.commit(); // commit if true
                return true;
            } else {
                logger.config("Subject unable to delete.");
                conn.rollback(); // rollback if error
                return false;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while deleting Subject: ", e);
        }
        return false;
    }

    // list all subjects
    public List<Subjects> listSubjects() {
        List<Subjects> subjects = new ArrayList<>();
        // Query to list all Subjects
        String listSubjectSQL = "SELECT Subjects.SubjectName, Class.ClassName " + "FROM Subjects "
                + "LEFT JOIN Class ON Subjects.ClassID = Class.ClassID";
        // try-block
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(listSubjectSQL)) {
            // variable to count total rows printed
            int count = 0;
            // inner try-block to fetch and display each row
            try (ResultSet rs = rm.executeQuery()) {
                // loop to display every row
                while (rs.next()) {
                    subjects.add(new Subjects(rs.getString("SubjectName"),
                            new ClassRoom(rs.getString("ClassName"))));
                    count++;
                }
                logger.log(Level.FINE, count + " Subjects Listed");
                return subjects; // return the List Array
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while executing Query to List Subjects: ", e);
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while listing Subjects: ", e);
        }

        return null;

    }

    // list all subjects in a class
    // list all subjects
    public List<Subjects> listClassSubjects(String className) {
        List<Subjects> subjects = new ArrayList<>();
        // Query to list all Subjects
        String listSubjectSQL = "SELECT Subjects.SubjectName, Class.ClassName " + "FROM Subjects "
                + "LEFT JOIN Class ON Subjects.ClassID = Class.ClassID WHERE ClassName = ?";
        // try-block
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(listSubjectSQL)) {
            rm.setString(1, className);
            int count = 0;
            // inner try-block to fetch and display each row
            try (ResultSet rs = rm.executeQuery()) {
                // loop to display every row
                while (rs.next()) {
                    subjects.add(new Subjects(rs.getString("SubjectName"),
                            new ClassRoom(rs.getString("ClassName"))));
                    count++;
                }
                logger.log(Level.FINE, count + " Subjects Listed");
                return subjects; // return the List Array
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while executing Query to List Subjects: ", e);
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while listing Subjects: ", e);
        }

        return null;

    }

    public List<Subjects> listClassSubjectswithMarks(String className) {
        List<Subjects> subjects = new ArrayList<>();
        // Query to list all Subjects
        String listSubjectSQL = "SELECT Subjects.SubjectName, Subjects.Marks, Class.ClassName FROM Subjects LEFT JOIN Class ON Subjects.ClassID = Class.ClassID WHERE ClassName = ?";
        // try-block
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(listSubjectSQL)) {
            rm.setString(1, className);
            int count = 0;
            // inner try-block to fetch and display each row
            try (ResultSet rs = rm.executeQuery()) {
                // loop to display every row
                while (rs.next()) {
                    subjects.add(new Subjects(rs.getString("SubjectName"), rs.getInt("Marks"),
                            new ClassRoom(rs.getString("ClassName"))));
                    count++;
                }
                logger.log(Level.FINE, count + " Subjects Listed");
                return subjects; // return the List Array
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while executing Query to List Subjects: ", e);
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while listing Subjects: ", e);
        }

        return null;

    }

    public int fetchSubjectTotalMarks(String subjectName) {
        String getMarksSQL = "SELECT Subjects.Marks FROM Subjects WHERE SubjectName = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(getMarksSQL)) {
            rm.setString(1, subjectName);
            ResultSet rs = rm.executeQuery();
            if (rs.next()) {
                return rs.getInt("Marks");
            }
        } catch (Exception e) {
            System.out.println("Error fetching Subject Marks");
        }
        return -1; // error code
    }
}
