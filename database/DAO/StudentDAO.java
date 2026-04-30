// BETA VERSION FINALIZED - StudentDAO
package database.DAO;

// package imports
import display.LogHandler;
import people.Student;
import classroom.Subjects;

// imports
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class StudentDAO {

    private static final Logger logger = Logger.getLogger(StudentDAO.class.getName());

    static {
        LogHandler.createLog(logger, "StudentDAO");
    }

    public Student fetchStudent(Connection conn, String name) {
        Student student = new Student();

        String studentSQL = "SELECT * FROM Student WHERE StudentName = ?;";

        try {
            if (!studentExists(conn, name)) {
                logger.warning("Student NOT found.");
                return student;
            }

            try (PreparedStatement rm = conn.prepareStatement(studentSQL)) {
                rm.setString(1, name);

                try (ResultSet rs = rm.executeQuery()) {
                    if (rs.next()) {
                        student.setID(rs.getInt("StudentID"));
                        student.setName(rs.getString("StudentName"));
                        student.setClassID(rs.getInt("ClassID"));
                    } else {
                        logger.warning("Unable to get Student.");
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching Student.", e);
        }
        return student;
    }

    public Student fetchStudent(Connection conn, int id) {
        Student student = new Student();

        String studentSQL = "SELECT * FROM Student WHERE StudentID = ?;";

        try {
            if (!studentExists(conn, id)) {
                logger.warning("Student NOT found.");
                return student;
            }

            try (PreparedStatement rm = conn.prepareStatement(studentSQL)) {
                rm.setInt(1, id);

                try (ResultSet rs = rm.executeQuery()) {
                    if (rs.next()) {
                        student.setID(rs.getInt("StudentID"));
                        student.setName(rs.getString("StudentName"));
                        student.setClassID(rs.getInt("ClassID"));
                    } else {
                        logger.warning("Unable to get Student.");
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching Student.", e);
        }
        return student;
    }

    public Student fetchStudentWithMarks(Connection conn, String studentName) {
        Student student = fetchStudent(conn, studentName);

        if (student.getID() == null) {
            logger.warning("Student ID is null.");
            return student;
        }

        String marksSQL = "SELECT * FROM StudentMarks WHERE StudentID = ?;";
        try (PreparedStatement rm = conn.prepareStatement(marksSQL)) {
            rm.setInt(1, student.getID());

            try (ResultSet rs = rm.executeQuery()) {
                while (rs.next()) {
                    Subjects subject = new Subjects();
                    subject.setID(rs.getInt("SubjectID"));
                    subject.setObtMarks(rs.getInt("ObtainedMarks"));
                    student.addSubject(subject);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while fetching Student Marks.", e);
        }
        return student;
    }

    public boolean studentExists(Connection conn, String name) {
        String check = "SELECT 1 FROM Student WHERE StudentName = ?;";

        try (PreparedStatement rm = conn.prepareStatement(check)) {
            rm.setString(1, name);

            try (ResultSet rs = rm.executeQuery()) {
                if (rs.next()) {
                    logger.info("Match found, Student exists.");
                    return true;
                } else {
                    logger.warning("Student does not exist.");
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking Student existence.", e);
        }
        return false;
    }

    public boolean studentExists(Connection conn, int id) {
        String check = "SELECT 1 FROM Student WHERE StudentID = ?;";

        try (PreparedStatement rm = conn.prepareStatement(check)) {
            rm.setInt(1, id);

            try (ResultSet rs = rm.executeQuery()) {
                if (rs.next()) {
                    logger.info("Match found, Student exists.");
                    return true;
                } else {
                    logger.warning("Student does not exist.");
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking Student existence.", e);
        }
        return false;
    }

    public boolean insertStudent(Connection conn, Student student) {
        try {
            if (studentExists(conn, student.getName())) {
                logger.warning("Student already exists.");
                return false;
            }

            if (student.getClassRoom() == null || student.getClassRoom().getID() == null
                    || student.getClassRoom().getID() == 0) {
                logger.warning("Class not set for Student.");
                return false;
            }

            String studentSQL = "INSERT INTO Student (StudentName, ClassID) VALUES (?,?)";
            try (PreparedStatement rm = conn.prepareStatement(studentSQL, Statement.RETURN_GENERATED_KEYS)) {
                rm.setString(1, student.getName());
                rm.setInt(2, student.getClassRoom().getID());

                int rs = rm.executeUpdate();

                ResultSet ID = rm.getGeneratedKeys();
                if (ID.next()) {
                    int genID = ID.getInt(1);
                    logger.info("Inserted Student with ID: " + genID);
                    student.setID(genID);
                }
                return rs > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error inserting student", e);
            return false;
        }
    }

    /*
     * INSERT OR REPLACE INTO StudentMarks (StudentID, SubjectID, ObtainedMarks)
     * VALUES (1, 1, 85); SQLITE specific syntax for upsert.
     * MySQL uses ON DUPLICATE KEY UPDATE, SQL Server uses MERGE statement.
     */

    public boolean insertOrUpdateMarks(Connection conn, Student student, Subjects subject) {
        try {
            String upsertSQL = "INSERT INTO StudentMarks (StudentID, SubjectID, ObtainedMarks) VALUES (?, ?, ?) "
                    + "ON CONFLICT(StudentID, SubjectID) DO UPDATE SET ObtainedMarks = excluded.ObtainedMarks";

            Student fetched = fetchStudent(conn, student.getName());

            if (fetched.getID() == null) {
                logger.warning("Student Doesnt exist.");
                return false;
            }

            if (subject.getID() == null || subject.getID() == 0) {
                logger.warning("Subject ID is not set.");
                return false;
            }

            if (subject.getObtMarks() == null || subject.getObtMarks() < 0
                    || subject.getObtMarks() > subject.getTotalMarks()) {
                logger.warning("Obtained Marks is not set or is negative or exceeds total marks.");
                return false;
            }

            try (PreparedStatement rm = conn.prepareStatement(upsertSQL)) {
                rm.setInt(1, fetched.getID());
                rm.setInt(2, subject.getID());
                rm.setInt(3, subject.getObtMarks());

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error inserting or updating marks", e);
            return false;
        }
    }

    public boolean deleteStudent(Connection conn, Student student) {
        try {
            String deleteStudentSQL = "DELETE FROM Student WHERE StudentID = ?";
            try (PreparedStatement rm = conn.prepareStatement(deleteStudentSQL)) {
                rm.setObject(1, student.getID(), Types.INTEGER);

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error deleting student", e);
            return false;
        }
    }

    public boolean updateStudent(Connection conn, Student student) {
        try {
            if (student.getID() == null || student.getID() == 0) {
                logger.warning("Student ID is required.");
                return false;
            }

            if (!studentExists(conn, student.getID())) {
                logger.warning("Student does not exist.");
                return false;
            }

            StringBuilder sql = new StringBuilder("UPDATE Student SET ");
            List<Object> params = new ArrayList<>();

            if (student.getName() != null && !student.getName().isEmpty()) {
                sql.append("StudentName = ?,");
                params.add(student.getName());
            }
            if (student.getClassID() != null && student.getClassID() != 0) {
                sql.append("ClassID = ?,");
                params.add(student.getClassID());
            }

            if (params.isEmpty()) {
                logger.warning("No fields to update.");
                return false;
            }

            sql.setLength(sql.length() - 1); // removing trailing ','
            sql.append(" WHERE StudentID = ?");

            try (PreparedStatement rm = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    rm.setObject(i + 1, params.get(i));
                }
                rm.setInt(params.size() + 1, student.getID());
                int updated = rm.executeUpdate();
                return updated > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error updating student", e);
            return false;
        }
    }

    public List<Student> listStudents(Connection conn) {
        List<Student> students = new ArrayList<>();
        String listStudentSQL = "SELECT s.StudentID, s.StudentName, s.ClassID, c.ClassName FROM Student s LEFT JOIN Class c ON s.ClassID = c.ClassID";

        try (PreparedStatement rm = conn.prepareStatement(listStudentSQL);
                ResultSet rs = rm.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No Data is available.");
                return new ArrayList<>();
            }

            while (rs.next()) {
                Student student = new Student();
                student.setID(rs.getInt("StudentID"));
                student.setName(rs.getString("StudentName"));
                student.setClassID(rs.getInt("ClassID"));
                student.setClassName(rs.getString("ClassName"));
                students.add(student);
            }
            return students;

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list all Students", e);
        }
        return new ArrayList<>();
    }

    // list (filter) students by class
    public List<Student> listStudents(Connection conn, int classID) {
        List<Student> students = new ArrayList<>();
        String listStudentSQL = "SELECT * FROM Student WHERE ClassID = ?";

        try (PreparedStatement rm = conn.prepareStatement(listStudentSQL)) {
            rm.setInt(1, classID);
            try (ResultSet rs = rm.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setID(rs.getInt("StudentID"));
                    student.setName(rs.getString("StudentName"));
                    student.setClassID(rs.getInt("ClassID"));
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list Students by Class", e);
        }
        return students;
    }
}
