// BETA VERSION FINALIZED - StudentDAO
package database.DAO;

// package imports
import display.LogHandler;
import people.Student;
import classroom.ClassRoom;
import classroom.Subjects;
import database.DBUtils;

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
                        ClassRoom cls = new ClassRoom();
                        cls.setID(rs.getInt("ClassID"));
                        student.setClassRoom(cls);
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
                    logger.info("Match found, Student Exists.");
                    return true;
                } else {
                    logger.warning("No match found, Student Does Not Exist.");
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking Student existence.", e);
        }
        return false;
    }

    public boolean insertStudent(Student student) {
        return DBUtils.runInTransaction(conn -> {
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
        });
    }

    /*
     * INSERT OR REPLACE INTO StudentMarks (StudentID, SubjectID, ObtainedMarks)
     * VALUES (1, 1, 85); SQLITE specific syntax for upsert.
     * MySQL uses ON DUPLICATE KEY UPDATE, SQL Server uses MERGE statement.
     */

    public boolean insertOrUpdateMarks(Student student, Subjects subject) {

        return DBUtils.runInTransaction(conn -> {
            String upsertSQL = "INSERT INTO StudentMarks (StudentID, SubjectID, ObtainedMarks) VALUES (?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE ObtainedMarks = VALUES(ObtainedMarks)";

            if (student.getID() == null) {
                logger.warning("Student Doesnt exist.");
                return false;
            }

            if (subject.getID() == null) {
                logger.warning("Subject ID is not set.");
                return false;
            }

            if (subject.getObtMarks() == null || subject.getObtMarks() < 0
                    || subject.getObtMarks() > subject.getTotalMarks()) {
                logger.warning("Obtained Marks is not set or is negative or exceeds total marks.");
                return false;
            }

            try (PreparedStatement rm = conn.prepareStatement(upsertSQL)) {
                rm.setInt(1, student.getID());
                rm.setInt(2, subject.getID());
                rm.setInt(3, subject.getObtMarks());

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    public boolean deleteStudent(Student student) {
        return DBUtils.runInTransaction(conn -> {
            String deleteStudentSQL = "DELETE FROM Student WHERE StudentID = ?";
            try (PreparedStatement rm = conn.prepareStatement(deleteStudentSQL)) {
                rm.setObject(1, student.getID(), Types.INTEGER);

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    public boolean updateStudent(Student oldStudent, Student newStudent) {
        return DBUtils.runInTransaction(conn -> {
            if (!studentExists(conn, oldStudent.getName())) {
                logger.warning("Student Doesnt exist.");
                return false;
            }

            if (newStudent.getName() != null && studentExists(conn, newStudent.getName())) {
                logger.warning("Updated Name: " + newStudent.getName() + "' already exists.");
                return false;
            }

            StringBuilder sql = new StringBuilder("UPDATE Student SET ");

            List<Object> parameters = new ArrayList<>();

            if (newStudent.getName() != null) {
                sql.append("StudentName = ?,");
                parameters.add(newStudent.getName());
            }
            if (newStudent.getClassRoom() != null && newStudent.getClassRoom().getID() != null
                    && newStudent.getClassRoom().getID() != 0) {
                sql.append(" ClassID = ?,");
                parameters.add(newStudent.getClassRoom().getID());
            }

            sql.append(" WHERE StudentName = ?");

            try (PreparedStatement rm = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < parameters.size(); i++) {
                    rm.setObject(i + 1, parameters.get(i));
                }
                rm.setString(parameters.size() + 1, oldStudent.getName());

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    public List<Student> listStudent(Connection conn) {
        List<Student> students = new ArrayList<>();
        String listStudentSQL = "SELECT * FROM Student";

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
                ClassRoom room = new ClassRoom();
                room.setID(rs.getInt("ClassID"));
                student.setClassRoom(room);
                students.add(student);
            }
            return students;

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Unable to list all Students", e);
        }
        return new ArrayList<>();
    }

}
