import database.DBValidator;
import database.DAO.ClassDAO;
import database.DAO.GradeDAO;
import database.DAO.SchoolDAO;
import database.DAO.StudentDAO;
import database.DAO.SubjectDAO;
import database.DAO.TeacherDAO;
import display.ConsoleDisplay;
import display.Input;
import display.MenuHandler;

public class Main {

    public static void main(String[] args) {

        // ... Menu & Input Objects
        MenuHandler call = new MenuHandler(); // for menus
        Input input = new Input(); // for input
        DBValidator db = new DBValidator();
        ConsoleDisplay show = new ConsoleDisplay();

        // ... Info Block

        System.out.println("================= INFO =================\n"
                + " At any point, enter [0] to go back or exit \n"
                + " the current menu/input.\n" + "========================================");

        // ? handles DB

        // ... DAO Objects
        ClassDAO room = new ClassDAO(); // ClassDAO object
        SubjectDAO subject_dao = new SubjectDAO(); // subjectDAO object
        TeacherDAO teacher_dao = new TeacherDAO(); // TeacherDAO object
        StudentDAO student_dao = new StudentDAO(); // StudentDAO object
        SchoolDAO school = new SchoolDAO(); // SchoolDAO object
        GradeDAO grade_dao = new GradeDAO(); // GradeDAO object



        // Delegate the main menu loop to MenuHandler for single responsibility
        call.runMainLoop(input, db, show, room, subject_dao, teacher_dao, student_dao, school, grade_dao);

    }

    // DONT FORGET TO CLOSE DOCUMENT/FILE and other things you opened (if any)

    /*
     * BETTER (& Current) SOLUTION MADE A LAMBDA FUNCTION THAT executes lines to
     * close a logger as soon as the JVM closes the file.
     */

}
