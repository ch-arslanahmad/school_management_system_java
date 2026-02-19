package database.DAO;

import database.DBUtils;
// package imports
import display.LogHandler;
import people.Student;

// imports
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import classroom.ClassRoom;
import classroom.Subjects;

import java.sql.*;

public class StudentDAO {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(StudentDAO.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "StudentDAO");
    }

    // fetch StudentID
    public int fetchStudentID(Connection conn, String name) {
        if (!studentExists(conn, name)) {
            logger.warning("Student does not exist. Add Student.");
            return -1;
        }
        // SQL Query
        String StudentIDSQL = "SELECT StudentID FROM Student where StudentName= ?";

        // try-catch block
        try (
                PreparedStatement rm = conn.prepareStatement(StudentIDSQL)) {

            // putting value in query
            rm.setString(1, name);

            // executing query
            try (ResultSet rs = rm.executeQuery()) {
                // pointing to column and fetching ClassID
                if (rs.next()) {
                    return rs.getInt("StudentID");
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Unable to execute fetch StudentID: ", e);

            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while fetching Student ID: ", e);
        }

        return -1;
    }

    // check if StudentExists
    public boolean studentExists(Connection conn, String name) {
        String ExistSQL = "SELECT COUNT(*) AS count FROM Student WHERE StudentName = ?";

        // prepared statement in try block
        try (
                PreparedStatement rm = conn.prepareStatement(ExistSQL)) {

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
            logger.log(Level.WARNING, "Error finding Student Existance: ", e);
        }
        return false;
    }

    // fetch StudentClass from StudentName
    public String fetchStudentClass(Connection conn, String name) {
        // this requires a fairly long query, explaination is as follows:
        // - SELECT Class.ClassName - this tells the end point e.g., we want ClassName
        // - from Class Table
        // - FROM Student - this says from Student Table
        // - JOIN Class ON Student.ClassID == Class.ClassID - This simply tells to join
        // - the ClassID column of both the 'Student' & 'Class' Table
        // - WHERE StudentName = ?" - It says fetch that row where StudentName.
        /* SO LONG STORY SHORT: It will fetch ClassName from StudentName */
        String fetchStudentClass = "SELECT Class.ClassName " + "FROM Student "
                + "JOIN Class ON Student.ClassID = Class.ClassID " + "WHERE StudentName = ?";

        try (
                PreparedStatement rm = conn.prepareStatement(fetchStudentClass)) {
            rm.setString(1, name);
            try (ResultSet rs = rm.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("ClassName");
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while executing fetchStudentClass Query: ", e);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while fetching Student ClassName: ", e);
        }

        return "-1";

    }

    public Student fetchStudent(Connection conn, String studentName) {
        String infoSQL = "SELECT Student.StudentName, Student.StudentID, Class.ClassName FROM Student JOIN Class ON Student.ClassID = Class.ClassID WHERE Student.StudentName = ?";
        try (
                PreparedStatement rm = conn.prepareStatement(infoSQL)) {
            rm.setString(1, studentName);
            try (ResultSet rs = rm.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No Data is available.");
                }
                while (rs.next()) {
                    return new Student(rs.getString("StudentName"), rs.getInt("StudentID"),
                            new ClassRoom(rs.getString("ClassName")));
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error RETURNING Student Info: ", e);
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error fetching Student Info: ", e);
        }
        return new Student();
    }

    // insert Student
    public boolean insertStudent(String ClassName, String name) {
        return DBUtils.runInTransaction(con -> {
            ClassDAO check = new ClassDAO();
            int classID = check.getIDfromClass(con, ClassName);
            // -1 is error-code
            if (classID == -1) {
                logger.info("Class does not exist.");
                return false;
            }

            String studentSQL = "INSERT INTO Student (StudentName, ClassID) VALUES (?,?)";

            try (PreparedStatement rm = con.prepareStatement(studentSQL)) {
                rm.setString(1, name);
                rm.setInt(2, classID);
                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    // delete Student
    public boolean deleteStudent(String name) {

        return DBUtils.runInTransaction(con -> {

            if (!(studentExists(con, name))) {
                logger.warning("No Match found");
                return false;
            }
            String deleteClassSQL = "DELETE FROM Student WHERE StudentName = ?";
            try (PreparedStatement rm = con.prepareStatement(deleteClassSQL)) {

                // set values in the query
                rm.setString(1, name);

                // execute query
                int rs = rm.executeUpdate();

                return rs > 0;
            }
        });
    }

    // update studentName
    public boolean updateStudent(String name, String updateName) {

        return DBUtils.runInTransaction(con -> {

            if (!studentExists(con, name)) {
                return false;
            }
            String updateStudent = "UPDATE Student SET StudentName = ? WHERE StudentName = ?";
            try (PreparedStatement rm = con.prepareStatement(updateStudent)) {

                rm.setString(1, updateName);
                rm.setString(2, name);

                int rs = rm.executeUpdate();
                return rs > 0;
            }
        });
    }

    // set, update ObtMarks
    public boolean updateStudentObtMarks(SubjectDAO subject, String studentName, String subjectName, int ObtMarks) {

        return DBUtils.runInTransaction(conn -> {

            int subID = subject.fetchSubjectID(conn, subjectName);
            int stuID = fetchStudentID(conn, studentName);
            if (stuID == -1) {
                logger.warning("Student does not exist.");
                return false;
            }
            if (subID == -1) {
                logger.warning("Subject does not exist.");

                return false;
            }

            String uptObt = "UPDATE Grade SET ObtainedMarks = ? WHERE StudentID = ? AND SubjectID = ? ";

            try (PreparedStatement rm = conn.prepareStatement(uptObt)) {
                rm.setInt(1, ObtMarks);
                rm.setInt(2, stuID);
                rm.setInt(3, subID);

                int rs = rm.executeUpdate();

                return rs > 0;
            }
        });
    }

    // list all students with class
    public List<Student> listStudent(Connection conn) {
        List<Student> student = new ArrayList<>();
        // Query to list all students
        String listStudentSQL = "SELECT Student.StudentName, Class.ClassName " + "FROM Student "
                + "LEFT JOIN Class ON Student.ClassID = Class.ClassID";

        try (PreparedStatement rm = conn.prepareStatement(listStudentSQL); ResultSet rs = rm.executeQuery()) {
            // loop to display every row
            while (rs.next()) {
                // display each row
                student.add(new Student(rs.getString("StudentName"),
                        new ClassRoom(rs.getString("ClassName"))));
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while listing Students: ", e);
        }
        return student; // if no student. will return empty list
    }

    // method to return StudentReport Data
    public List<Subjects> fetchStudentReport(Connection conn, String name) {
        List<Subjects> subjectsList = new ArrayList<>();

        // i created a view for this named 'getGrades'
        // cannot call it directly
        // this lengthy query is used, so not getStudent/ClassName Name writen multiple
        // times
        String fetchStudentReport = "SELECT SubjectName, Marks, ObtainedMarks, Percentage, Grade FROM getGrades WHERE StudentName = ?";

        try (PreparedStatement rm = conn.prepareStatement(fetchStudentReport);
                ResultSet rs = rm.executeQuery();) {

            rm.setString(1, name);

            while (rs.next()) {
                Subjects subject = new Subjects(rs.getString("SubjectName"), rs.getInt("Marks"),
                        rs.getInt("ObtainedMarks"));
                subjectsList.add(subject);
            }
            return subjectsList;

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while fetching Student Report: ", e);
            return new ArrayList<>();
        }

    }

}
