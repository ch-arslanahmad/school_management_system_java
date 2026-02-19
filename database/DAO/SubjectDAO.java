package database.DAO;

// package imports
import display.LogHandler;

// imports
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import classroom.ClassRoom;
import classroom.Subjects;
import database.DBUtils;

import java.sql.*;

public class SubjectDAO {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(SubjectDAO.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "SubjectDAO");
    }

    // NOTE for future reference: accept the method only if valid ClassName is
    // provided

    // method to get ID from Subject
    public int fetchSubjectID(Connection conn, String name) {
        int subjectID;
        if (!subjectExists(conn, name)) {
            return -1;
        }
        String subjectIDSQL = "SELECT SubjectID FROM Subjects where SubjectName = ?;";
        subjectID = -1;
        try (PreparedStatement rm = conn.prepareStatement(subjectIDSQL)) {

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

    public int getValidSubjectID(Connection conn, String name) {
        if (!subjectExists(conn, name)) {
            logger.warning("Subject doesnt exist.");
            return -1;
        } else {
            logger.warning("Status of Fetched ID: ");
            return getClassIdBySubject(conn, name);
        }
    }

    // method to get ClassID of Subject

    public int getClassIdBySubject(Connection conn, String name) {

        // SQL Query
        String classIdSQL = "SELECT ClassID FROM Subjects WHERE SubjectName = ?";

        // try-catch block
        try (PreparedStatement rm = conn.prepareStatement(classIdSQL)) {
            if (!subjectExists(conn, name)) {
                logger.info("Subject does not exist.");
                return -1;
            }
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
        // automated error code
        return -1;

    }

    // see if subject exists
    public boolean subjectExists(Connection conn, String name) {
        // SQL query to check
        String ExistSQL = "SELECT COUNT(*) AS count FROM Subjects WHERE SubjectName = ?;";

        // prepared statement in try block
        try (PreparedStatement rm = conn.prepareStatement(ExistSQL)) {

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
        ClassDAO check = new ClassDAO();

        return DBUtils.runInTransaction(conn -> {

            int classID = check.getValidClassID(conn, ClassName);
            // STOP if return ERROR code (-1)
            if (classID == -1) {
                logger.info("Incorrect ID");
                return false;
            }

            // SQL Query
            String subjectSQL = "INSERT INTO Subjects (SubjectName, ClassID, Marks) VALUES (?,?,?)";
            try (
                    PreparedStatement rm = conn.prepareStatement(subjectSQL)) {
                // set values in the query
                rm.setString(1, name);
                rm.setInt(2, classID);
                rm.setInt(3, Marks);

                // execute query
                int rs = rm.executeUpdate();

                return rs > 0;
            }
        });

    }

    public boolean updateSubject(String ClassName, String name, String updateName) {
        ClassDAO check = new ClassDAO();

        return DBUtils.runInTransaction(conn -> {
            if (check.getValidClassID(conn, ClassName) == -1) {
                logger.config("Class doesn't exist.");
                return false;
            }
            if (!subjectExists(conn, name)) {
                logger.config("Subject doesn't exist.");
                return false;
            }
            // Query to update Subject
            String updateSubjSQL = "UPDATE Subjects SET SubjectName = ? WHERE SubjectName = ? AND ClassID = ?";
            try (PreparedStatement rm = conn.prepareStatement(updateSubjSQL)) {
                rm.setString(1, updateName);
                rm.setString(2, name);
                rm.setInt(3, check.getValidClassID(conn, ClassName));

                int rs = rm.executeUpdate();
                return rs > 0 || rs == 1; // return true if 1 row is affected.
            }
        });

    }

    public boolean deleteSubject(String ClassName, String name) {
        return DBUtils.runInTransaction(conn -> {
            if (!(subjectExists(conn, name))) {
                logger.warning("No Match found, Subject does not exist.");
                return false;
            }

            String delSubjectSQL = "DELETE FROM Subjects WHERE SubjectName = ? AND ClassID = ?";
            try (PreparedStatement rm = conn.prepareStatement(delSubjectSQL)) {

                // set values in the query
                rm.setString(1, name);
                rm.setInt(2, getClassIdBySubject(conn, name));

                // execute query
                int rs = rm.executeUpdate();

                return rs > 0 || rs == 1; // return true if 1 row is affected.
            }
        });
    }

    // list all subjects
    public List<Subjects> listSubjects(Connection conn) {
        List<Subjects> subjects = new ArrayList<>();

        String listSubjectSQL = "SELECT Subjects.SubjectName, Class.ClassName " + "FROM Subjects "
                + "LEFT JOIN Class ON Subjects.ClassID = Class.ClassID";

        try (PreparedStatement rm = conn.prepareStatement(listSubjectSQL); ResultSet rs = rm.executeQuery()) {

            int count = 0;

            // loop to display every row
            while (rs.next()) {
                subjects.add(new Subjects(rs.getString("SubjectName"),
                        new ClassRoom(rs.getString("ClassName"))));
                count++;
            }
            logger.log(Level.FINE, count + " Subjects Listed");
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while listing Subjects: ", e);
        }
        return subjects; // return empty list
    }

    // list all subjects in a class
    public List<Subjects> listClassSubjects(Connection conn, String className) {
        List<Subjects> subjects = new ArrayList<>();
        String listSubjectSQL = "SELECT Subjects.SubjectName, Class.ClassName " + "FROM Subjects "
                + "LEFT JOIN Class ON Subjects.ClassID = Class.ClassID WHERE ClassName = ?";

        try (PreparedStatement rm = conn.prepareStatement(listSubjectSQL); ResultSet rs = rm.executeQuery()) {
            rm.setString(1, className);
            int count = 0;
            // inner try-block to fetch and display each row
            // loop to display every row
            while (rs.next()) {
                subjects.add(new Subjects(rs.getString("SubjectName"),
                        new ClassRoom(rs.getString("ClassName"))));
                count++;
            }
            logger.log(Level.FINE, count + " Subjects Listed");
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while listing Subjects: ", e);
        }
        return subjects;
    }

    public List<Subjects> listClassSubjectswithMarks(Connection conn, String className) {
        List<Subjects> subjects = new ArrayList<>();

        String listSubjectSQL = "SELECT Subjects.SubjectName, Subjects.Marks, Class.ClassName FROM Subjects LEFT JOIN Class ON Subjects.ClassID = Class.ClassID WHERE ClassName = ?";
        // try-block
        try (
                PreparedStatement rm = conn.prepareStatement(listSubjectSQL);
                ResultSet rs = rm.executeQuery()) {
            rm.setString(1, className);
            int count = 0;

            // loop to display every row
            while (rs.next()) {
                subjects.add(new Subjects(rs.getString("SubjectName"), rs.getInt("Marks"),
                        new ClassRoom(rs.getString("ClassName"))));
                count++;
            }
            logger.log(Level.FINE, count + " Subjects Listed");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while listing Subjects: ", e);
        }
        return subjects;
    }

}
