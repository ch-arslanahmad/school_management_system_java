package database;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;

import database.DAO.ClassDAO;
import database.DAO.StudentDAO;
import database.DAO.SubjectDAO;
import database.DAO.TeacherDAO;
import display.Input;

public class DBManager {
    private static final Logger logger = Logger.getLogger(DBManager.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**

    static {
        //
        try {
            // so logging is not shown in console
            // LogManager.getLogManager().reset();
            String a = "log/DBManager.txt";
            File file = new File(a);
            // if file does not exist, create it
            if (!(file.exists())) {
                file.createNewFile();
            }
            fh = new FileHandler(a, 1024 * 1024, 1, true); // path, size, n of files, append or not
            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
            logger.setLevel(Level.FINE);

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
        logger.info("Logger Closed.");
        fh.flush();
        fh.close();
    }

    Connection conn;

    // Checking, does DB file exists?
    public boolean DBfileExists() {
        File DB = new File("database/people.db");
        if (DB.exists()) {
            String checkStructure = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'";
            try (Connection conn = Database.getConnection();
                    PreparedStatement rm = conn.prepareStatement(checkStructure)) {

                ResultSet rs = rm.executeQuery();
                if (rs.next()) {
                    logger.info("The file exists with a structure.");
                    return true;
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Error while checking DB: ", e);
            }
        }
        return false;
    }

    // to test one table if it has data
    public boolean testTable(String table) {
        String sqlQuery = "SELECT COUNT(*) FROM " + table;

        try (Connection conn = Database.getConnection(); Statement rm = conn.createStatement()) {
            boolean rs = rm.execute(sqlQuery);
            if (rs) {
                logger.info("the table " + table + " exist & is not empty. ");
                return true;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while validating Table " + table + " in DB: ", e);
        }
        return false;
    }

    // validate Database with Data
    public boolean DBvalidate() {
        if (DBfileExists()) {
            String[] tables = { "School", "Class", "Subjects", "Student", "Teacher", "Grade" };
            for (String t : tables) {
                if (!testTable(t)) {
                    return false;
                }
            }
            logger.fine("Data in Database File is available.");
            return true;
        }
        return false;

    }

    // to remove one table's data if it has data
    public boolean removeTable(String table) {
        String sqlQuery = "DELETE FROM " + table;

        try (Connection conn = Database.getConnection(); Statement rm = conn.createStatement()) {
            boolean rs = rm.execute(sqlQuery);
            if (rs) {
                logger.info("the table " + table + " exist & is not empty. ");
                return true;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while validating Table " + table + " in DB: ", e);
        }
        return false;
    }

    public boolean delDB() {
        if (!DBvalidate()) {
            return false;
        }
        String[] tables = { "Grade", "Student", "Teacher", "Subjects", "Class", "School",
                "sqlite_sequence" };

        try (Connection conn = Database.getConnection()) {
            // Disable foreign keys
            try (Statement rm = conn.createStatement()) {
                rm.execute("PRAGMA foreign_keys = OFF");
            }

            // Execute all delete statements of table
            for (String t : tables) {
                if (!removeTable(t)) {
                    logger.warning("Table" + t + " has a problem. ");
                    return false;
                }
            }

            // Re-enable foreign keys
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error clearing database data", e);
        }
        return false;
    }

    public boolean removeAllData(Input input) {
        while (true) {
            String deleteDB = input.getNormalInput();
            if (deleteDB.equals("yes")) {
                return delDB();
            } else if (deleteDB.equals("no")) {
                break;
            } else {
                logger.info("Enter either, yes/no");
            }
        }
        return false;
    }

    public ArrayList<String> insertClassesDB(ClassDAO room, Input input) {
        ArrayList<String> classes = new ArrayList<>();
        // loop to get insert classes
        while (true) {
            System.err.println("(Press 0 to stop)\nEnter Class: ");
            String className = input.getStrInput();
            // condition to stop infinite loop
            if (className.equals("0")) {
                break;
            } else {
                if (!room.insertClass(className)) {
                    String error = "Database Operation Abort : Class";
                    logger.warning(error);
                    return new ArrayList<>();
                }
                classes.add(className);
            }
        }
        return classes;
    }

    // insert multiple students if arraylist of classes already exists
    public void insertStudentsDB(ArrayList<String> classes, StudentDAO student, Input input) {
        // for-each loop to get insert students in classes
        for (String className : classes) {

            // infinite loop to insert students in a class
            while (true) {

                System.out
                        .println("(Press 0 to stop)\nEnter Student in Class " + className + " : ");
                String studentName = input.getStrInput().trim();
                // condition to stop infinite loop
                if (studentName.equals("0")) {
                    break;
                }
                if (!student.insertStudent(className, studentName)) {
                    String error = "Database Creation Abort : Student";
                    logger.warning(error);
                    System.exit(0);
                }

            }
        }

    }

    // insert multiple students
    public boolean insertStudentsDB(StudentDAO student, Input input) {
        // infinite loop to insert students in a class
        while (true) {

            System.out.println("(Press 0 to stop)\nEnter ClassName: ");
            String className = input.getStrInput();
            System.out.println("Enter Student Name: ");
            String name = input.getStrInput();
            // condition to stop infinite loop
            if (name.equals("0") && className.equals("0")) {
                break;
            }
            if (!student.insertStudent(className, name)) {
                String error = "Database Creation Abort : Student";
                logger.warning(error);
                return false;
            }

        }
        return true;
    }

    // insert multiple subjects if arraylist of classes already exists
    public ArrayList<String> insertSubjectsDB(ArrayList<String> classes, SubjectDAO subject,
            Input input) {
        ArrayList<String> subjects = new ArrayList<>();
        // add subjects to the classes
        for (String className : classes) {
            // infinite loop to insert subjects in a class
            while (true) {

                System.out
                        .println("(Press 0 to stop)\nEnter Subject in Class " + className + " : ");
                String subjectName = input.getStrInput();
                System.out.println("Enter Total Marks of Subject: ");
                int subjectMarks = input.getIntInput();
                // condition to stop infinite loop
                if (subjectName.equals("0")) {
                    break;
                } else {
                    if (!subject.insertSubject(className, subjectName, subjectMarks)) {
                        String error = "Database Creation Abort : Subjects";
                        logger.warning(error);
                        return new ArrayList<>();
                    }
                }
                subjects.add(subjectName);
            }

        }
        return subjects;
    }

    // insert multiple subjects
    public boolean insertSubjectsDB(SubjectDAO subject, Input input) {
        // add subjects to the classes
        // infinite loop to insert subjects in a class
        while (true) {
            System.out.print("(Press 0 to stop)\nEnter ClassName: ");
            String className = input.getStrInput();
            System.out.print("Enter Subject in Class " + className + " : ");
            String subjectName = input.getStrInput();
            System.out.print("Enter Total Marks of Subject: ");
            int subjectMarks = input.getIntInput();
            // condition to stop infinite loop
            if (className.equals("0") && subjectName.equals("0")) {
                break;
            } else {
                if (!subject.insertSubject(className, subjectName, subjectMarks)) {
                    String error = "Database Creation Abort : Subjects";
                    logger.warning(error);
                    return false;
                }
            }
        }
        return true;
    }

    public void insertTeachersDB(ArrayList<String> subjects, TeacherDAO teacher, Input input) {
        // add teachers of subjects

        for (String subjectName : subjects) {
            System.out
                    .println("(Press 0 to stop)\nEnter Teacher in Subject " + subjectName + " : ");
            String teacherName = input.getStrInput();
            // condition to stop infinite loop
            if (teacherName.equals("0")) {
                break;
            } else {
                if (!teacher.insertTeacher(subjectName, teacherName)) {
                    String error = "Database Creation Abort : Subjects";
                    logger.warning(error);
                    System.exit(0);
                }
                subjects.add(subjectName);
            }
        }

    }

    // insert multiple teachers
    public boolean insertTeachersDB(TeacherDAO teacher, Input input) {
        // add teachers to the subjects
        // infinite loop to insert teacher in subject
        while (true) {
            System.out.print("(Press 0 to stop)\nEnter TeacherName: ");
            String name = input.getStrInput();
            System.out.print("Enter Subject : ");
            String subjectName = input.getStrInput();
            // condition to stop infinite loop
            if (name.equals("0") && subjectName.equals("0")) {
                break;
            } else {
                if (!teacher.insertTeacher(subjectName, name)) {
                    String error = "Database Creation Abort : Subjects";
                    logger.warning(error);
                    return false;
                }
            }
        }
        return true;
    }

}
