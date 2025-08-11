package database.DAO;

// package imports
import database.*;

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

    // fetch teacherID from name
    public int fetchTeacherID(String name) {
        if (!teacherExists(name)) {
            logger.warning("Teacher does not exist. Add Teacher.");
        } else {
            // SQL Query
            String TeacherIDSQL = "SELECT TeacherID FROM Student where TeacherName= ?";

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
        // StudentDAO
        String fetchStudentClass = "SELECT Subjects.SubjectName"
                + "FROM Teacher"
                + "JOIN SUbjects ON Teacher.SubjectID = Subjects.SubjectID"
                + "WHERE TeacherName = ?";

        try (PreparedStatement rm = Database.getConn().prepareStatement(fetchStudentClass)) {
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

}
