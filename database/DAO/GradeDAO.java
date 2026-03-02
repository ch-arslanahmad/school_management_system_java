package database.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import classroom.Subjects;
import display.LogHandler;

public class GradeDAO {

    private static final Logger logger = Logger.getLogger(GradeDAO.class.getName());

    static {
        LogHandler.createLog(logger, "GradeDAO");
    }

    public List<Subjects> fetchStudentReport(Connection conn, String studentID) {
        List<Subjects> subjects = new ArrayList<>();
        // The getGrades view in the DB exposes: StudentID, StudentName, SubjectID,
        // SubjectName, ObtainedMarks, Grade, ClassID, ClassName
        // callers may pass a StudentName or StudentID; detect numeric input and
        // filter accordingly.
        boolean isNumeric = false;
        int sid = -1;
        try {
            sid = Integer.parseInt(studentID);
            isNumeric = true;
        } catch (NumberFormatException nfe) {
            isNumeric = false;
        }

        String sqlById = "SELECT * FROM getGrades WHERE StudentID = ?";
        String sqlByName = "SELECT * FROM getGrades WHERE StudentName = ?";

        String sql = isNumeric ? sqlById : sqlByName;

        try (PreparedStatement rm = conn.prepareStatement(sql)) {
            if (isNumeric) {
                rm.setInt(1, sid);
            } else {
                rm.setString(1, studentID);
            }

            try (ResultSet rs = rm.executeQuery()) {
                while (rs.next()) {
                    Subjects subject = new Subjects();
                    // populate subject fields according to view
                    try {
                        subject.setID(rs.getInt("SubjectID"));
                    } catch (SQLException ignore) {
                    }
                    subject.setName(rs.getString("SubjectName"));
                    // ObtainedMarks may be null in some cases
                    try {
                        int om = rs.getInt("ObtainedMarks");
                        if (!rs.wasNull()) {
                            subject.setObtMarks(om);
                        }
                    } catch (SQLException ignore) {
                    }
                    try {
                        subject.setClassID(rs.getInt("ClassID"));
                    } catch (SQLException ignore) {
                    }
                    subject.setClassName(rs.getString("ClassName"));
                    subjects.add(subject);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching student report", e);
        }
        return subjects;
    }

    public List<Subjects> fetchAllStudentReports(Connection conn) {
        List<Subjects> subjects = new ArrayList<>();
        String sql = "SELECT * FROM getGrades"; // no WHERE clause

        try (PreparedStatement rm = conn.prepareStatement(sql);
                ResultSet rs = rm.executeQuery()) {
            while (rs.next()) {
                Subjects subject = new Subjects();
                subject.setID(rs.getInt("SubjectID"));
                subject.setName(rs.getString("SubjectName"));
                try {
                    int om = rs.getInt("ObtainedMarks");
                    if (!rs.wasNull()) {
                        subject.setObtMarks(om);
                    }
                } catch (SQLException ignore) {
                }
                subject.setClassName(rs.getString("ClassName"));
                // Note: view also exposes StudentName/StudentID if callers need them
                subjects.add(subject);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching all student reports", e);
        }
        return subjects;
    }

}
