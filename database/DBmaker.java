package database;

import database.DAO.*;
import display.Input;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.*;

public class DBmaker {
    private static final Logger logger = Logger.getLogger(DBmaker.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**

    static {
        //
        try {
            // so logging is not shown in console
            // LogManager.getLogManager().reset();
            String a = "log/DBmaker.txt";
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeLog() {
        fh.flush();
        fh.close();
    }

    // METHOD to create whole database
    public void createDB(Input input) {
        DBManager check = new DBManager();

        System.out.println(
                "To Use this APP you must create a database. BUT BEFORE That, they may be dummy data beforehand in the DB.\n\n");
        System.out.println("WARNING: ALL THE DATA WILL BE DELETED.");
        check.removeAllData(input);

        // all the objects of DB
        SchoolDAO school = new SchoolDAO();
        ClassDAO room = new ClassDAO();
        StudentDAO student = new StudentDAO();
        SubjectDAO subject = new SubjectDAO();
        TeacherDAO teacher = new TeacherDAO();

        // SCHOOL
        System.out.println("Ok, First. \nTell me school name: ");
        String schoolName = input.getStrInput();
        System.out.println("Tell me School Principle name: ");
        String principleName = input.getStrInput();

        // if School-info is not inserted
        if (!school.insertSchool(schoolName, principleName)) {
            String error = "Database Creation Abort : School-Info";
            logger.warning(error);
            System.exit(0);
        }

        // arrayList of classes that were inserted

        // CLASSES
        System.out.println("Ok, NOW. \nLets add a class or multiple classes.\n");
        ArrayList<String> classes = check.insertClassesDB(room, input);
        System.out.println("All Classes Inserted.\n");

        // STUDENTS
        System.out.println("Now Students");
        check.insertStudentsDB(classes, student, input);

        // SUBJECTS
        System.out.println("Now Subjects");
        ArrayList<String> subjects = check.insertSubjectsDB(classes, subject, input);

        // TEACHERS
        System.out.println("Now Teachers");
        check.insertTeachersDB(subjects, teacher, input);

    }

}