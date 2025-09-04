import database.DBManager;
import database.DAO.ClassDAO;
import database.DAO.StudentDAO;
import database.DAO.SubjectDAO;
import database.DAO.TeacherDAO;
import display.ConsoleDisplay;
import display.Input;
import display.MenuHandler;

public class Main {

    public static void main(String[] args) {

        // ... Menu & Input Objects
        MenuHandler call = new MenuHandler();
        Input input = new Input();
        DBManager db = new DBManager();
        ConsoleDisplay show = new ConsoleDisplay();

        // ... Info Block

        System.out.println("================= INFO =================\n"
                + " At any point, enter [0] to go back or exit \n" + " the current menu/input.\n"
                + "========================================");

        // ? handles DB
        call.handleDatabase(db, input);

        // ... DAO Objects
        ClassDAO room = new ClassDAO(); // ClassDAO object
        SubjectDAO subject = new SubjectDAO(); // subjectDAO object
        TeacherDAO teacher = new TeacherDAO(); // TeacherDAO object
        StudentDAO student = new StudentDAO(); // StudentDAO object

        /*
         * todo: solve the insert multiple methdods so it is in line with the current
         * strucutre
         */

        boolean run = true;
        while (run) {
            // now show main menu
            call.mainMenu();
            int choice = input.validateMenuInput(4, input);
            switch (choice) {
            case 0: // stop the loop
                run = false;
                System.out.println("Exiting!! Goodbye.");
                break;
            case 1: // CLASS
                call.handleClassMenu(room, db, show, input);
                break;
            case 2: // SUBJECT
                call.handleSubjectMenu(subject, db, show, input);
                break;
            case 3: // TEACHERS
                call.handleTeacherMenu(teacher, db, show, input);
                break;
            case 4: // STUDENTS
                call.handleStudentMenu(student, db, show, input);
                break;
            default:
                System.out.println("Invalid Choice.");
                break; // stop the switch statement
            }
        }

    }

    // DONT FORGET TO CLOSE DOCUMENT/FILE and other things you opened (if any)

    /*
     * BETTER (& Current) SOLUTION MADE A LAMBDA FUNCTION THAT executes lines to
     * close a logger as soon as the JVM closes the file.
     */

}
