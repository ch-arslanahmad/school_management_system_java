package database.DAO;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.*;

import database.Database;

public class GradeDAO {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(GradeDAO.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**
    static {
        //
        try {
            /*
             * // so logging is not shown in console LogManager.getLogManager().reset();
             */
            String a = "log/GradeDAO.txt";
            File file = new File(a);
            // if file does not exist, create it
            if (!(file.exists())) {
                file.createNewFile();
            }
            fh = new FileHandler(a, 1024 * 1024, 1, true); // path, size, n of files, append or not
            fh.setLevel(Level.FINE);
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

    public static void closeLog() {
        logger.info("Log File is closed.");
        fh.flush();
        fh.close();
    }

    // set / update ObtMarks
    boolean updateObtMarks(String studentName, String SubjectName, int ObtMarks) {

        StudentDAO student = new StudentDAO();
        SubjectDAO subject = new SubjectDAO();
        int subID = subject.getClassIdBySubject(SubjectName);
        int stuID = student.fetchStudentID(studentName);
        if (stuID == -1) {
            logger.warning("Student does not exist.");
            return false;
        }
        if (subID == -1) {
            return false;
        }

        String uptObt = "UPDATE Grade SET ObtainedMarks = ? WHERE StudentID = ? AND SubjectID = ? ";

        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(uptObt)) {
            rm.setInt(1, ObtMarks);
            rm.setString(1, uptObt);
            rm.setString(1, uptObt);

            int rs = rm.executeUpdate();

            if (rs > 0) {
                return true;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error setting Grade: ", e);
        }
        return false;
    }
}