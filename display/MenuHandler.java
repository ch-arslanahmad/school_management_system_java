package display;

import java.util.logging.*;
import java.util.InputMismatchException;

import database.DBManager;
import database.DBmaker;
import database.DAO.ClassDAO;
import database.DAO.StudentDAO;
import database.DAO.SubjectDAO;
import database.DAO.TeacherDAO;
import school.Actions;

public class MenuHandler {

    private static final Logger logger = Logger.getLogger(MenuHandler.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "MenuHandler");
    }

    private Actions act = new Actions();

    /// ALL MENUS

    // MAIN MENU
    public void mainMenu() {
        System.out.print("\n========== School Management System ==========\n" + "1. Class >\n"
                + "2. Subject (linked to a Class) >\n" + "3. Teacher (linked to a Subject) >\n"
                + "4. Student (linked to a Class) > \n" + "5. Exit\n"
                + "===============================================\n");
    }

    // easy template for making Menus with names
    public void showMenu(String name, String[] options) {
        System.out.println("\n========== " + name + " ==========\n");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i] + " " + name);
        }
    }

    public void showboolMenu(String name, String[] options) {
        if (!name.equals("")) {
            System.out.println("\n========== " + name + " ==========\n");
        }
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i] + " ");
        }
    }

    // TEMPLATE OPTIONS FOR SUB - MENUS
    String[] options = { "Insert", "Delete", "Insert Multiple", "Update", "Show" };

    // CLASS MENU
    public void handleClassMenu(ClassDAO room, DBManager db, ConsoleDisplay show, Input input) {
        while (true) {
            try {
                showMenu("Classes", options);
                int choice = input.validateMenuInput(5, input);
                switch (choice) {
                case 0: // stop the loop
                    return;
                case 1: // insert class
                    if (!act.inputClass(room, input)) {
                        System.out.println("Error inserting Class");
                        return;
                    }
                    break;
                case 2: // delete class
                    if (!act.deleteClass(room, input)) {
                        System.out.println("Error deleting Class");
                        return;
                    }
                    break;
                case 3: { // insert multiple
                    db.insertClassesDB(room, input);
                    break;
                }
                case 4: // update class
                    if (!act.updateClass(room, input)) {
                        System.out.println("Error updating Class");
                        return;
                    }
                    break;

                case 5: // show classes
                    act.showClasses(room, input, show);
                    break;
                default:
                    System.out.println("Invalid Choice.");
                    break;
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Integer Input is causing error: ");
                e.printStackTrace();
            }
        }

    }

    // SUBJECT MENU
    public void handleSubjectMenu(SubjectDAO subject, DBManager db, ConsoleDisplay show,
            Input input) {
        while (true) {
            try {
                showMenu("Subjects", options);
                int choice = input.validateMenuInput(5, input);
                switch (choice) {
                case 0: // stop the loop
                    return;
                case 1: // insert
                    if (!act.inputSubject(subject, input)) {
                        System.out.println("Error inserting Subject");
                        return;
                    }
                    break;
                case 2: // delete
                    if (!act.deleteSubject(subject, input)) {
                        System.out.println("Error deleting Subject");
                        return;
                    }
                    break;
                case 3: // insert MULTIPLE
                    if (!db.insertSubjectsDB(subject, input)) {
                        System.out.println("Error inserting multiple subjects.");
                        return;
                    }
                    break;
                case 4: // update
                    if (!act.updateSubject(subject, input)) {
                        System.out.println("Error updating Subject");
                        return;
                    }
                    break;
                case 5: // show
                    act.showSubjects(subject, input, show);
                    break;
                default:
                    System.out.println("Invalid Choice.");
                    break;
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Integer Input is causing error: ");
                e.printStackTrace();
            }

        }
    }

    // TEACHERMENU
    public void handleTeacherMenu(TeacherDAO teacher, DBManager db, ConsoleDisplay show,
            Input input) {
        while (true) {
            try {
                showMenu("Teachers", options);
                int choice = input.validateMenuInput(5, input);
                switch (choice) {
                case 0: // stop the loop
                    return;
                case 1: // insert
                    if (!act.inputTeacher(teacher, input)) {
                        System.out.println("Error inserting Teacher");
                        return;
                    }
                    break;
                case 2: // delete
                    if (!act.deleteTeacher(teacher, input)) {
                        System.out.println("Error deleting Teacher");
                        return;
                    }
                    break;
                case 3: // insert MULTIPLE
                    if (!db.insertTeachersDB(teacher, input)) {
                        System.out.println("Error inserting multiple teachers");
                        return;
                    }
                    break;
                case 4: // update
                    if (!act.updateTeacher(teacher, input)) {
                        System.out.println("Error updating Teacher");
                        return;
                    }
                    break;

                case 5: // show
                    act.showTeachers(teacher, input, show);
                    break;
                default:
                    System.out.println("Invalid Choice.");
                    break;
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Integer Input is causing error: ");
                e.printStackTrace();
            }
        }
    }

    // STUDENT MENU
    public void handleStudentMenu(StudentDAO student, DBManager db, ConsoleDisplay show,
            Input input) {
        while (true) {
            try {
                showMenu("Students", options);
                int choice = input.validateMenuInput(5, input);
                switch (choice) {
                case 0:
                    return; // stop the loop
                case 1: // insert
                    if (!act.inputStudent(student, input)) {
                        System.out.println("Error inserting Student.");
                        return;
                    }
                    break;
                case 2: // delete
                    if (!act.deleteStudent(student, input)) {
                        System.out.println("Error deleting Student.");
                        return;
                    }
                    break;
                case 3: // insert MULTIPLE
                    if (!db.insertStudentsDB(student, input)) {
                        System.out.println("Error inserting multiple Students");
                        return;
                    }
                    break;
                case 4: // update
                    if (!act.updateStudent(student, input)) {
                        System.out.println("Error updating Student.");
                        return;
                    }
                    break;

                case 5: // show
                    act.showStudents(student, input, show);
                    break;
                default:
                    System.out.println("Invalid Choice.");
                    break;
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Integer Input is causing error: ");
                e.printStackTrace();
            }
        }
    }

    public void handleDatabase(DBManager db, Input input) {

        // DB setup section
        String[] options = { "Wipe dummy data and recreate DB (recommended for first use)",
                "Keep existing data" };
        boolean run = true;
        while (run) {
            showboolMenu("Database Setup", options);
            int dbChoice = input.validateMenuInput(2, input);

            switch (dbChoice) {
            case 0: {
                System.out.println("Exiting Setup.");
                return;
            }
            case 1:
                System.out.println(
                        "⚠ WARNING: This will delete EVERYTHING. Type 'CONFIRM' to proceed: ");
                String confirm = input.getNormalInput();
                if (confirm.equalsIgnoreCase("CONFIRM")) {
                    if (!db.delDB()) {
                        System.out.println("Unable to delete DB.");
                        break;
                    }
                    DBmaker data = new DBmaker();
                    data.createDB(input);
                    System.out.println("Database wiped and recreated successfully.");
                } else {
                    System.out.println("Cancelled wipe. Keeping existing data.");
                }
                break;

            case 2:
                if (!db.DBvalidate()) {
                    System.out.println("No valid database found. Creating a new one...");
                    DBmaker data = new DBmaker();
                    data.createDB(input);
                } else {
                    System.out.println("Using existing database.");
                }
                break;

            default:
                System.out.println("Invalid choice.");
            }
        }

    }

}
