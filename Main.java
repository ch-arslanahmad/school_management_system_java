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
import web.WebServer;

public class Main {

    public static void main(String[] args) {
        System.out.println("Select\n1. TUI\n2. Web Server");

        int choice = Input.validateMenuInput(2);

        boolean runWeb = choice != 1;

        if (runWeb) {
            System.out.println("Starting Web Server");
            WebServer.start();
            return;
        }

        DBValidator db = new DBValidator();

        System.out.println("================= INFO =================\n"
                + " At any point, enter [0] to go back or exit \n"
                + " the current menu/input.\n" + "========================================");

        ClassDAO room = new ClassDAO();
        SubjectDAO subject_dao = new SubjectDAO();
        TeacherDAO teacher_dao = new TeacherDAO();
        StudentDAO student_dao = new StudentDAO();
        SchoolDAO school = new SchoolDAO();
        GradeDAO grade_dao = new GradeDAO();

        MenuHandler.runMainLoop(db, room, subject_dao, teacher_dao, student_dao, school, grade_dao);
    }

    // DON'T FORGET TO CLOSE DOCUMENT/FILE and other things you opened (if any)

    /*
     * BETTER (& Current) SOLUTION MADE A LAMBDA FUNCTION THAT executes lines to
     * close a logger as soon as the JVM closes the file.
     */

}
