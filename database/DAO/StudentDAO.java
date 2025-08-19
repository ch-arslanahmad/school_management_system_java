package database.DAO;

// package imports
import database.Database;
import display.ConsoleDisplay;
import people.Student;
import school.SchoolData;

// imports
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import classroom.ClassRoom;

import java.sql.*;

public class StudentDAO {
    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(StudentDAO.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**
    static {
        //
        try {
            /*
             * // so logging is not shown in console LogManager.getLogManager().reset();
             */
            String a = "log/StudentDAOlog.txt";
            File file = new File(a);
            // if file does not exist, create it
            if (!(file.exists())) {
                file.createNewFile();
            }
            fh = new FileHandler(a, true);
            fh.setLevel(Level.FINE);
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

    // fetch StudentID
    public int fetchStudentID(String name) {
        if (!studentExists(name)) {
            logger.warning("Student does not exist. Add Student.");
        } else {
            // SQL Query
            String StudentIDSQL = "SELECT StudentID FROM Student where StudentName= ?";

            // try-catch block
            try (PreparedStatement rm = Database.getConn().prepareStatement(StudentIDSQL)) {

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

        }
        return -1;
    }

    // check if StudentExists
    public boolean studentExists(String name) {
        String ExistSQL = "SELECT COUNT(*) AS count FROM Student WHERE StudentName = ?";

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
        String fetchStudentClass = "SELECT Class.ClassName" + "FROM Student"
                + "JOIN Class ON Student.ClassID = Class.ClassID" + "WHERE StudentName = ?";

        try (Connection conn = Database.getConn();
                PreparedStatement rm = conn.prepareStatement(fetchStudentClass)) {
            rm.setString(1, name);
            try (ResultSet rs = rm.executeQuery()) {

                if (rs.next()) {
                    return rs.getString("ClassName");
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while fetching ClassName of Student");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while fetching ClassName of Student.");
        }

        return "-1";

    }

    ClassDAO check = new ClassDAO();

    // insert Student
    public boolean insertStudent(String ClassName, String name) {
        int classID = check.getIDfromClass(ClassName);
        // -1 is error-code
        if (classID == -1) {
            logger.info("Class does not exist.");
        } else {
            String studentSQL = "INSERT INTO Students (StudentName, ClassID) VALUES (?,?)";

            try (PreparedStatement rm = Database.getConn().prepareStatement(studentSQL)) {
                rm.setString(1, name);
                rm.setInt(2, classID);

                int rs = rm.executeUpdate();
                if (rs > 0) {
                    logger.info("Added Student.");
                    return true;
                } else {
                    logger.config("Unable to add Student");
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while inserting Student.", e);
            }
        }
        return false;

    }

    // delete Student
    public boolean deleteStudent(String name) {
        if (!(studentExists(name))) {
            System.out.println("No Match found");
        } else {
            String deleteClassSQL = "DELETE FROM Student WHERE StudentName = ?";
            try (PreparedStatement rm = Database.getConn().prepareStatement(deleteClassSQL)) {

                // set values in the query
                rm.setString(1, name);

                // execute query
                int rs = rm.executeUpdate();

                if (rs > 0) {
                    // confirmation
                    logger.info("Student Deleted");
                    return true;
                } else {
                    logger.config("Student unable to delete.");
                    return false;
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while deleting Student: ", e);
            }
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
        try (Connection conn = Database.getConn();
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
                    logger.warning("Student are not displayed.");
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error while executing Query to List Students: ", e);
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while listing Students: ", e);
        }

        return null;

    }

    public List<SchoolData> studentsClass() {
        List<ClassRoom> room = new ArrayList<>();
        List<Student> student = new ArrayList<>();
        List<SchoolData> studentClass = new ArrayList<>();

        String stdClassSQL = "SELECT Class.ClassName, Student.StudentName " + "FROM Class "
                + "JOIN Student ON Student.ClassID = Class.ClassID;";

        try (Connection conn = Database.getConn();
                PreparedStatement rm = conn.prepareStatement(stdClassSQL)) {

            ResultSet rs = rm.executeQuery();
            while (rs.next()) {
                room.add(new ClassRoom(rs.getString("ClassName")));
                student.add(new Student(rs.getString("StudentName")));
            }
            studentClass.add(new SchoolData(room, student));
            return studentClass;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Errors while listing Student Classes", e);
        }
        return null;
    }

}
