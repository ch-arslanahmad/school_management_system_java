package database;

import database.DAO.*;
import display.Input;
import display.LogHandler;
import school.Actions;

import java.util.logging.*;

public class DBmaker {
    private static final Logger logger = Logger.getLogger(DBmaker.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "DBMaker");
    }

    // ? using Boolean wrapper to allow nulls, helpful in finding if user stopped
    // the method or an error

    // METHOD to create whole database
    public void createDB(Input input) {

        // all the objects of DB
        SchoolDAO school = new SchoolDAO();
        ClassDAO room = new ClassDAO();
        StudentDAO student = new StudentDAO();
        SubjectDAO subject = new SubjectDAO();
        TeacherDAO teacher = new TeacherDAO();

        Actions act = new Actions(); // method for actions

        act.addSchoolInfo(school, input);

        // arrayList of classes that were inserted

        // CLASSES
        System.out.println("Now Classes.");
        act.inputClasses(room, input);

        // STUDENTS
        System.out.println("Now Students");
        act.inputStudents(student, input);

        // SUBJECTS
        System.out.println("Now Subjects");
        act.inputSubjects(subject, input);

        // TEACHERS
        System.out.println("Now Teachers");
        act.inputTeachers(teacher, input);

    }

}