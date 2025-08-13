package database.DAO;

// package imports
import database.*;
import display.ConsoleDisplay;

//imports
import java.io.File;
import java.util.logging.*;
import java.sql.*;

public class TeacherDAO {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(TeacherDAO.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**
    static {
        //
        try {
            /*
             * // so logging is not shown in console
             * LogManager.getLogManager().reset();
             */
            String a = "TeacherDAOlog.txt";
            File file = new File(a);
            // if file does not exist, create it
            if (!(file.exists())) {
                file.createNewFile();
            }
            fh = new FileHandler(a, true);
            fh.setLevel(Level.FINE);
            ;
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

    // fetch teacherID from name
    public int fetchTeacherID(String name) {
        if (!teacherExists(name)) {
            logger.warning("Teacher does not exist. Add Teacher.");
        } else {
            // SQL Query
            String TeacherIDSQL = "SELECT TeacherID FROM Teacher where TeacherName= ?";

            // try-catch block
            try (PreparedStatement rm = Database.getConn().prepareStatement(TeacherIDSQL)) {

                // putting value in query
                rm.setString(1, name);

                // executing query
                ResultSet rs = rm.executeQuery();

                // pointing to column and fetching ClassID
                if (rs.next()) {
                    return rs.getInt("TeacherID");
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while fetching Teacher ID: ", e);
            }

        }
        return -1;
    }

    // see if teacher exists
    private boolean teacherExists(String name) {
        String ExistSQL = "SELECT COUNT(*) AS count FROM Teacher WHERE  TeacherName = ?";

        // prepared statement in try block
        try (PreparedStatement rm = Database.getConn().prepareStatement(ExistSQL)) {

            // adding value to query
            rm.setString(1, name);

            // storing in a variable
            ResultSet rs = rm.executeQuery();

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
            logger.log(Level.WARNING, "Error finding Teacher Existance: ", e);
        }
        return false;
    }

    // fetch TeacherSubject from TeacherName
    public String fetchTeacherSubject(String name) {
        // this requires a fairly long query, similar explaination is already given in
        // TeacherDAO
        String fetchTeacherClass = "SELECT Subjects.SubjectName "
                + "FROM Teacher "
                + "JOIN SUbjects ON Teacher.SubjectID = Subjects.SubjectID "
                + "WHERE TeacherName = ?";

        try (PreparedStatement rm = Database.getConn().prepareStatement(fetchTeacherClass)) {
            rm.setString(1, name);
            try (ResultSet rs = rm.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("SubjectName");
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while fetching SubjectName of Teacher");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while fetching SubjectName of Teacher.");
        }

        return "-1";

    }

    ClassDAO check = new ClassDAO();

    // insert Subject
    public boolean insertSubject(String ClassName, String name) {
        int classID = check.getIDfromClass(ClassName);
        // -1 is error-code
        if (classID == -1) {
            logger.info("Class does not exist.");
        } else {
            String TeacherSQL = "INSERT INTO Subjects (TeacherName, ClassID) VALUES (?,?)";

            try (PreparedStatement rm = Database.getConn().prepareStatement(TeacherSQL)) {
                rm.setString(1, name);
                rm.setInt(2, classID);

                int rs = rm.executeUpdate();
                if (rs > 0) {
                    logger.info("Added Subject.");
                    return true;
                } else {
                    logger.config("Unable to add Subject");
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while inserting Subject.", e);
            }
        }
        return false;

    }

    // delete Teacher
    public boolean deleteTeacher(String name) {
        if (!(teacherExists(name))) {
            System.out.println("No Match found");
        } else {
            String delTeachSQL = "DELETE FROM Teacher WHERE TeacherName = ?";
            try (PreparedStatement rm = Database.getConn().prepareStatement(delTeachSQL)) {

                // set values in the query
                rm.setString(1, name);

                // execute query
                int rs = rm.executeUpdate();

                if (rs > 0) {
                    // confirmation
                    logger.info("Teacher Deleted");
                    return true;
                } else {
                    logger.config("Teacher unable to delete.");
                    return false;
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while deleting Teacher: ", e);
            }
        }
        return false;

    }

    ConsoleDisplay display = new ConsoleDisplay();

    /*
     * LEFT JOIN is better for this method as it will list all the columns of the
     * left and its related things of the column of the right if they exist unless
     * explicitly show, 'NULL'.
     */

    // list all Teachers
    public boolean listTeacher() {
        // Query to list all Teachers
        String listSubjectSQL = "SELECT Teacher.TeacherName, Subjects.SubjectName "
                + "FROM Teacher "
                + "LEFT JOIN Subjects ON Teacher.SubjectID = Subjects.SubjectID";

        // try-block
        try (Connection conn = Database.getConn(); PreparedStatement rm = conn.prepareStatement(listSubjectSQL)) {

            // variable to count total rows printed
            int count = 0;
            // inner try-block to fetch and display each row
            try (ResultSet rs = rm.executeQuery()) {

                System.out.printf("%-20s | %-20s\n", "Teacher", "Subject");

                // loop to display every row
                while (rs.next()) {
                    String teachname = rs.getString("TeacherName") != null ? rs.getString("TeacherName") : "NAN";
                    String subname = rs.getString("SubjectName") != null ? rs.getString("SubjectName") : "NAN";

                    // method to display
                    display.displayf(teachname, subname);
                    // increment the count
                    count++;
                }
                // return true only if teachers are displayed
                return count > 0;
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error while executing Query to List Teachers: ", e);
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while listing Teachers: ", e);
        }

        return false;

    }

}
