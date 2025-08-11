package database.DAO;

// package imports
import database.*;

//imports
import java.io.File;
import java.util.Scanner;
import java.util.logging.*;
import java.sql.*;

public class SubjectDAO {
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
            String a = "SubjectDAOlog.txt";
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

    private String subjectNameDAO;

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
                // fetches and stores the ClassID in a variable from the matched row
                subjectID = rs.getInt("SubjectID");
                logger.info(name + " ID is: " + subjectID);
            } else {
                logger.config("Unable to get SubjectID.");
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while fetching SubjectID.", e);
        }

        return subjectID;

    }

    // see if subject exists
    public boolean subjectExists(String name) {
        // SQL query to check
        String check = "SELECT COUNT(*) AS count FROM Subjects WHERE SubjectName = ?;";

        // prepared statement in try block
        try (PreparedStatement rm = Database.getConn().prepareStatement(check)) {

            // adding value to query
            rm.setString(1, name);

            /*
             * 1. We use executeUpdate() because we are not adding, removing anything we are
             * simply executing the query.
             * 2. executeQuery returns value in ResultSet Object hence that value is also
             * stored ResultSet variable, 'rs'.
             */

            ResultSet rs = rm.executeQuery();

            /*
             * ResultSet (rs) returns a table with one row.
             * It does not automatically point to the matched column. To do that you do
             * rs.next().
             * - res.next() returns row.
             * - true if more than 1
             * - false if 0
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

    /* FIX THE FOLLOWING METHODS - insertSubject() FIRST */

    public boolean insertSubject(String ClassName, String name) {

        // STOP if return ERROR code (-1)
        if (check.getValidClassID() == -1) {
            logger.info("Incorrect ID");
        } else {

            int classID = check.getValidClassID(ClassName);

            // SQL Query
            String subjectSQL = "INSERT INTO Subject (SubjectName, ClassID) VALUES (?,?)";
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

    public boolean insertSubjects(String ClassName, Scanner input) {
        // adding Subjects
        // STOP if return ERROR code (-1)
        if (check.getValidClassID(ClassName) == -1) {
            logger.config("Unable to get ValidID of Class");
            logger.warning("Class Does not exist.");
        } else {
            while (true) {
                try {
                    boolean subjectAdded = insertSubject(ClassName, ClassName);
                    if (subjectAdded) {
                        logger.finest("Class Successfully entered");
                    } else {
                        System.out.println("Class must be entered to continue.");
                        continue;
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

            return false;
        }
    }

    public boolean deleteSubject(String ClassName, String name) {
        return false;
    }

}
