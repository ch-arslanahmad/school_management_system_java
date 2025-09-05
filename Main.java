import database.DBManager;
import database.DAO.ClassDAO;
import database.DAO.SchoolDAO;
import database.DAO.StudentDAO;
import database.DAO.SubjectDAO;
import database.DAO.TeacherDAO;
import display.ConsoleDisplay;
import display.Input;
import display.MenuHandler;
import school.Actions;

public class Main {

    public static void main(String[] args) {

        // ... Menu & Input Objects
        MenuHandler call = new MenuHandler(); // for menus
        Actions act = new Actions(); // for seperate actions
        Input input = new Input(); // for input
        DBManager db = new DBManager();
        ConsoleDisplay show = new ConsoleDisplay();

        // ... Info Block

        System.out.println("================= INFO =================\n"
                + " At any point, \\033[1m enter [0] to go back or exit \\033[0m \n"
                + " the current menu/input.\n" + "========================================");

        // ? handles DB
        call.handleDatabase(db, input);

        // ... DAO Objects
        ClassDAO room = new ClassDAO(); // ClassDAO object
        SubjectDAO subject = new SubjectDAO(); // subjectDAO object
        TeacherDAO teacher = new TeacherDAO(); // TeacherDAO object
        StudentDAO student = new StudentDAO(); // StudentDAO object
        SchoolDAO school = new SchoolDAO(); // SchoolDAO object

        /*
         * todo: solve the insert multiple methdods so it is in line with the current
         * strucutre
         */

        boolean run = true;
        while (run) {
            // now show main menu
            call.mainMenu();
            int choice = input.validateMenuInput(5, input);
            switch (choice) {
                case 0 -> { // ... stop the loop
                    run = false;
                    System.out.println("Exiting!! Goodbye.");
                }
                // handles school INFO
                case 1 -> act.addSchoolInfo(school, input);
                // CLASS
                case 2 -> call.handleClassMenu(room, db, show, input);
                // SUBJECT
                case 3 -> call.handleSubjectMenu(subject, db, show, input);
                // TEACHERS
                case 4 -> call.handleTeacherMenu(teacher, db, show, input);
                // STUDENTS
                case 5 -> call.handleStudentMenu(student, db, show, input);
                // handle grades of student
                case 6 -> call.handleStudentGrades(input, student, subject, act);
                // ... default
                default -> System.out.println("Invalid Choice.");
            }
        }

    }

    // DONT FORGET TO CLOSE DOCUMENT/FILE and other things you opened (if any)

    /*
     * BETTER (& Current) SOLUTION MADE A LAMBDA FUNCTION THAT executes lines to
     * close a logger as soon as the JVM closes the file.
     */

}
