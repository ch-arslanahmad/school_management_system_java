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

    // Fetch student report by numeric student ID
    public List<Subjects> fetchStudentReport(Connection conn, int studentId) {
        List<Subjects> subjects = new ArrayList<>();
        String sql = "SELECT * FROM getGrades WHERE StudentID = ?";
        try (PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setInt(1, studentId);
            try (ResultSet rs = rm.executeQuery()) {
                while (rs.next()) {
                    Subjects subject = new Subjects();
                    try { subject.setID(rs.getInt("SubjectID")); } catch (SQLException ignore) {}
                    subject.setName(rs.getString("SubjectName"));
                    try {
                        int om = rs.getInt("ObtainedMarks");
                        if (!rs.wasNull()) { subject.setObtMarks(om); }
                    } catch (SQLException ignore) {}
                    try { subject.setClassID(rs.getInt("ClassID")); } catch (SQLException ignore) {}
                    subject.setClassName(rs.getString("ClassName"));
                    subjects.add(subject);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching student report by ID", e);
        }
        return subjects;
    }

    // Fetch student report by student name
    public List<Subjects> fetchStudentReport(Connection conn, String studentName) {
        List<Subjects> subjects = new ArrayList<>();
        String sql = "SELECT * FROM getGrades WHERE StudentName = ?";
        try (PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setString(1, studentName);
            try (ResultSet rs = rm.executeQuery()) {
                while (rs.next()) {
                    Subjects subject = new Subjects();
                    try { subject.setID(rs.getInt("SubjectID")); } catch (SQLException ignore) {}
                    subject.setName(rs.getString("SubjectName"));
                    try {
                        int om = rs.getInt("ObtainedMarks");
                        if (!rs.wasNull()) { subject.setObtMarks(om); }
                    } catch (SQLException ignore) {}
                    try { subject.setClassID(rs.getInt("ClassID")); } catch (SQLException ignore) {}
                    subject.setClassName(rs.getString("ClassName"));
                    subjects.add(subject);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching student report by name", e);
        }
        return subjects;
    }

    public List<Subjects> fetchAllStudentReports(Connection conn) {
        List<Subjects> subjects = new ArrayList<>();
        String sql = "SELECT * FROM getGrades";

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
                subjects.add(subject);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching all student reports", e);
        }
        return subjects;
    }

    public List<Subjects> fetchClassGrades(Connection conn, int classId) {
        List<Subjects> subjects = new ArrayList<>();
        String sql = "SELECT * FROM getGrades WHERE ClassID = ?";

        try (PreparedStatement rm = conn.prepareStatement(sql)) {
            rm.setInt(1, classId);
            try (ResultSet rs = rm.executeQuery()) {
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
                    subjects.add(subject);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching class grades", e);
        }
        return subjects;
    }
}
