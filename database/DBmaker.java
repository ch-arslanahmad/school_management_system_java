package database;

import database.DAO.*;
import display.LogHandler;
import school.Actions;

import java.sql.*;
import java.util.logging.*;

public class DBmaker {
    private static final Logger logger = Logger.getLogger(DBmaker.class.getName());

    static {
        LogHandler.createLog(logger, "DBMaker");
    }

    public void createDB() {
        SchoolDAO school = new SchoolDAO();
        ClassDAO room = new ClassDAO();
        StudentDAO student = new StudentDAO();
        SubjectDAO subject = new SubjectDAO();
        TeacherDAO teacher = new TeacherDAO();

        createSchoolInfo(school);
        createClasses(room);
        createStudents(student);
        createSubjects(subject, room);
        createTeachers(teacher);
    }

    public DBmaker create() {
        createTables();
        createStudentMarksViewAndTrigger();
        return this;
    }

    public DBmaker init() {
        DBValidator validator = new DBValidator();
        if (!validator.DBfileExists()) {
            create();
        }
        return this;
    }

    public DBmaker createSchoolInfo(SchoolDAO school) {
        System.out.println("Now School Info.");
        Actions.addSchoolInfo();
        return this;
    }

    public DBmaker createClasses(ClassDAO room) {
        System.out.println("Now Classes.");
        Actions.inputClasses();
        return this;
    }

    public DBmaker createStudents(StudentDAO student) {
        System.out.println("Now Students");
        Actions.inputStudents();
        return this;
    }

    public DBmaker createSubjects(SubjectDAO subject, ClassDAO room) {
        System.out.println("Now Subjects");
        Actions.inputSubjects();
        return this;
    }

    public DBmaker createTeachers(TeacherDAO teacher) {
        System.out.println("Now Teachers");
        Actions.inputTeachers();
        return this;
    }

    public DBmaker createView() {
        // Use a view definition that matches the existing DB schema to avoid discrepancies
        String createViewSQL = "CREATE VIEW IF NOT EXISTS getGrades AS "
                + "SELECT "
                + "  s.StudentID, "
                + "  s.StudentName, "
                + "  sub.SubjectID, "
                + "  sub.SubjectName, "
                + "  g.ObtainedMarks, "
                + "  CASE "
                + "    WHEN g.ObtainedMarks >= 90 THEN 'A' "
                + "    WHEN g.ObtainedMarks >= 80 THEN 'B' "
                + "    WHEN g.ObtainedMarks >= 60 THEN 'C' "
                + "    WHEN g.ObtainedMarks >= 50 THEN 'D' "
                + "    ELSE 'F' "
                + "  END AS Grade, "
                + "  c.ClassID, "
                + "  c.ClassName "
                + "FROM StudentMarks g "
                + "  JOIN Student s ON s.StudentID = g.StudentID "
                + "  JOIN Subjects sub ON sub.SubjectID = g.SubjectID "
                + "  JOIN Class c ON s.ClassID = c.ClassID";

        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(createViewSQL);
            logger.info("Created getGrades VIEW");
            conn.commit();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error creating VIEW", e);
        }
        return this;
    }

    public DBmaker createTrigger() {
        String createTriggerSQL = "CREATE TRIGGER IF NOT EXISTS CheckStudentClass "
                + "BEFORE INSERT ON StudentMarks "
                + "FOR EACH ROW "
                + "BEGIN "
                + "  SELECT "
                + "    CASE "
                + "      WHEN (SELECT ClassID FROM Student WHERE StudentID = NEW.StudentID) "
                + "         != (SELECT ClassID FROM Subjects WHERE SubjectID = NEW.SubjectID) "
                + "      THEN RAISE(ABORT, 'MISMATCH: Subject is not in the Student Class.') "
                + "    END; "
                + "END";

        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(createTriggerSQL);
            logger.info("Created CheckStudentClass trigger");
            conn.commit();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error creating trigger", e);
        }
        return this;
    }

    public DBmaker createStudentMarksViewAndTrigger() {
        createView();
        createTrigger();
        return this;
    }

    public DBmaker createTables() {
        String[] tableSQLs = {
                "CREATE TABLE IF NOT EXISTS School ("
                        + "id INTEGER PRIMARY KEY CHECK (id = 1), "
                        + "Name TEXT, "
                        + "Principal TEXT, "
                        + "location TEXT DEFAULT 'Unknown')",

                "CREATE TABLE IF NOT EXISTS Class ("
                        + "ClassID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "ClassName TEXT NOT NULL UNIQUE, "
                        + "Tuition_Fee INTEGER DEFAULT 0, "
                        + "Stationary_Fee INTEGER DEFAULT 0, "
                        + "Paper_Fee INTEGER DEFAULT 0)",

                "CREATE TABLE IF NOT EXISTS Subjects ("
                        + "SubjectID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "SubjectName TEXT NOT NULL, "
                        + "ClassID INTEGER NOT NULL, "
                        + "FOREIGN KEY (ClassID) REFERENCES Class(ClassID))",

                "CREATE TABLE IF NOT EXISTS Student ("
                        + "StudentID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "StudentName TEXT, "
                        + "ClassID INTEGER, "
                        + "FOREIGN KEY (ClassID) REFERENCES Class(ClassID) ON DELETE CASCADE)",

                "CREATE TABLE IF NOT EXISTS Teacher ("
                        + "TeacherID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "TeacherName TEXT NOT NULL, "
                        + "SubjectID INTEGER NOT NULL, "
                        + "FOREIGN KEY (SubjectID) REFERENCES Subjects(SubjectID))",

                "CREATE TABLE IF NOT EXISTS StudentMarks ("
                        + "StudentMarkID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "StudentID INTEGER, "
                        + "SubjectID INTEGER, "
                        + "ObtainedMarks INTEGER, "
                        + "FOREIGN KEY (StudentID) REFERENCES Student(StudentID), "
                        + "FOREIGN KEY (SubjectID) REFERENCES Subjects(SubjectID), "
                        + "UNIQUE(StudentID, SubjectID))"
        };

        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement()) {
            for (String sql : tableSQLs) {
                stmt.execute(sql);
            }
            conn.commit();
            logger.info("Created all database tables");
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error creating tables", e);
        }
        return this;
    }
}
