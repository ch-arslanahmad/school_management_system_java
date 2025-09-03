package database.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.*;

import database.Database;
import display.LogHandler;

public class GradeDAO {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(GradeDAO.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "GradeDAO");
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