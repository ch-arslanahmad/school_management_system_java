package database.DAO;

// package imports
import database.Database;
import display.ConsoleDisplay;
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
    public int fetchStudentID(String name) {
        if (!studentExists(name)) {
            logger.warning("Student does not exist. Add Student.");
            return -1;
        }
        // SQL Query
        String StudentIDSQL = "SELECT StudentID FROM Student where StudentName= ?";

        // try-catch block
        try (PreparedStatement rm = Database.getConnection().prepareStatement(StudentIDSQL)) {

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
    public boolean studentExists(String name) {
        String ExistSQL = "SELECT COUNT(*) AS count FROM Student WHERE StudentName = ?";

        // prepared statement in try block
        try (PreparedStatement rm = Database.getConnection().prepareStatement(ExistSQL)) {

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
    public String fetchStudentClass(String name) {
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

        try (Connection conn = Database.getConnection();
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

    public Student getStudentInfo(String studentName) {
        String infoSQL = "SELECT Student.StudentName, Student.StudentID, Class.ClassName FROM Student JOIN Class ON Student.ClassID = Class.ClassID WHERE Student.StudentName = ?";
        try (Connection conn = Database.getConnection();
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
        ClassDAO check = new ClassDAO();
        int classID = check.getIDfromClass(ClassName);
        // -1 is error-code
        if (classID == -1) {
            logger.info("Class does not exist.");
            return false;
        }
        String studentSQL = "INSERT INTO Student (StudentName, ClassID) VALUES (?,?)";

        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(studentSQL)) {
            rm.setString(1, name);
            rm.setInt(2, classID);

            int rs = rm.executeUpdate();
            if (rs > 0) {
                logger.info("Added Student.");
                conn.commit();
                return true;
            } else {
                logger.config("Unable to add Student");
                conn.rollback();
                return false;
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while inserting Student.", e);
        }
        return false;

    }

    // delete Student
    public boolean deleteStudent(String name) {
        if (!(studentExists(name))) {
            System.out.println("No Match found");
            return false;
        }
        String deleteClassSQL = "DELETE FROM Student WHERE StudentName = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(deleteClassSQL)) {

            // set values in the query
            rm.setString(1, name);

            // execute query
            int rs = rm.executeUpdate();

            if (rs > 0) {
                // confirmation
                logger.info("Student Deleted");
                conn.commit();
                return true;
            } else {
                logger.config("Student unable to delete.");
                conn.rollback();
                return false;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while deleting Student: ", e);
        }
        return false;
    }

    // update studentName
    public boolean updateStudent(String name, String updateName) {

        if (!studentExists(name)) {
            return false;
        }
        String updateStudent = "UPDATE Student SET StudentName = ? WHERE StudentName = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(updateStudent)) {

            rm.setString(1, updateName);
            rm.setString(2, name);

            int rs = rm.executeUpdate();
            if (rs > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while updating Student: ", e);
        }
        return false;
    }

    ConsoleDisplay display = new ConsoleDisplay();

    // list all students with class
    public List<Student> listStudent() {
        List<Student> student = new ArrayList<>();
        // Query to list all students
        String listStudentSQL = "SELECT Student.StudentName, Class.ClassName " + "FROM Student "
                + "LEFT JOIN Class ON Student.ClassID = Class.ClassID";
        // try-block
        // added the initilization of connnection so its automatically closed by the
        // try-catch block
        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(listStudentSQL)) {
            // variable to count total rows printed
            // inner try-block to fetch and display each row
            try (ResultSet rs = rm.executeQuery()) {
                // loop to display every row
                while (rs.next()) {
                    // display each row
                    student.add(new Student(rs.getString("StudentName"),
                            new ClassRoom(rs.getString("ClassName"))));
                }
                // return true if Students are displayed
                if (student.size() > 0) {
                    logger.info("Students are successfully displayed.");
                    return student; // returns student with classes
                } else {
                    logger.warning("Students are not displayed.");
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error while executing Query to List Students: ", e);
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while listing Students: ", e);
        }

        return new ArrayList<>();

    }

    // method to return StudentReport Data
    public List<Subjects> fetchStudentReport(Student student) {
        List<Subjects> subjectsList = new ArrayList<>();

        // i created a view for this named 'getGrades'
        // cannot call it directly
        // this lengthy query is used, so not getStudent/ClassName Name writen multiple
        // times
        String fetchStudentReport = "SELECT SubjectName, Marks, ObtainedMarks, Percentage, Grade FROM getGrades WHERE StudentName = ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(fetchStudentReport)) {

            rm.setString(1, student.getName());

            ResultSet rs = rm.executeQuery();

            while (rs.next()) {
                Subjects subject = new Subjects(rs.getString("SubjectName"), rs.getInt("Marks"),
                        rs.getInt("ObtainedMarks"));
                subjectsList.add(subject);
            }
            return subjectsList;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    // set / update ObtMarks
    public boolean updateObtMarks(SubjectDAO subject, String studentName, String SubjectName,
            int ObtMarks) {

        int subID = subject.getClassIdBySubject(SubjectName);
        int stuID = fetchStudentID(studentName);
        if (stuID == -1) {
            logger.warning("Student does not exist.");
            return false;
        }
        if (subID == -1) {
            logger.warning("Subject does not exist.");

            return false;
        }

        String uptObt = "UPDATE Grade SET ObtainedMarks = ? WHERE StudentID = ? AND SubjectID = ? ";

        try (Connection conn = Database.getConnection();
                PreparedStatement rm = conn.prepareStatement(uptObt)) {
            rm.setInt(1, ObtMarks);
            rm.setInt(2, stuID);
            rm.setInt(3, subID);

            int rs = rm.executeUpdate();

            if (rs > 0) {
                conn.commit(); // commit
                return true;
            } else {
                conn.rollback();
                return false; // rollback if error
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error setting Grade: ", e);
        }
        return false;
    }

}
