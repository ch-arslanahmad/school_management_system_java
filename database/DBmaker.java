package database;

import display.LogHandler;
import school.Actions;
import java.sql.*;
import java.util.logging.*;

public final class DBmaker {
    private static final Logger logger = Logger.getLogger(DBmaker.class.getName());

    static {
        LogHandler.createLog(logger, "DBMaker");
    }

    public static boolean createDB() {
        // First create tables, views, triggers (infrastructure)
        boolean infraOk = create();
        if (!infraOk) {
            logger.warning("Failed to create database infrastructure");
            return false;
        }

        // Now ask user for input via Actions
        Actions.addSchoolInfo();
        Actions.inputClasses();
        Actions.inputStudents();
        Actions.inputSubjects();
        Actions.inputTeachers();

        return true;
    }

    public static boolean create() {
        boolean tablesOk = createTables();
        boolean viewTriggerOk = createStudentMarksViewAndTrigger();
        return tablesOk && viewTriggerOk;
    }

    public static boolean init() {
        if (!DBValidator.DBfileExists()) {
            return createDB();
        }
        return true;
    }

    public static boolean createView() {
        try {
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

            return DBUtils.runInTransaction(conn -> {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createViewSQL);
                    logger.info("Created getGrades VIEW");
                    return true;
                }
            });
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error creating VIEW", e);
            return false;
        }
    }

    public static boolean createTrigger() {
        try {
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

            return DBUtils.runInTransaction(conn -> {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTriggerSQL);
                    logger.info("Created CheckStudentClass trigger");
                    return true;
                }
            });
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error creating trigger", e);
            return false;
        }
    }

    public static boolean createStudentMarksViewAndTrigger() {
        boolean viewOk = createView();
        boolean triggerOk = createTrigger();
        return viewOk && triggerOk;
    }

    public static boolean createTables() {
        try {
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

            return DBUtils.runInTransaction(conn -> {
                try (Statement stmt = conn.createStatement()) {
                    for (String sql : tableSQLs) {
                        stmt.execute(sql);
                    }
                    logger.info("Created all database tables");
                    return true;
                }
            });
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error creating tables", e);
            return false;
        }
    }
}