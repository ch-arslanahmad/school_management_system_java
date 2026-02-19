package display;

import java.util.logging.*;
import java.sql.*;
import java.util.InputMismatchException;

import database.DBValidator;
import database.DBmaker;
import database.Database;
import database.DAO.ClassDAO;
import database.DAO.SchoolDAO;
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
        System.out.print("\n========== School Management System ==========\n"
                + "1. School-Info\n2. Class >\n" + "3. Subject (linked to a Class) >\n"
                + "4. Teacher (linked to a Subject) >\n"
                + "5. Student (linked to a Class)\n6. Student Grades\n"
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
    public void handleClassMenu(ClassDAO room, DBValidator db, ConsoleDisplay show, Input input) {
        try (Connection conn = Database.getConnection()) {
            while (true) {
                try {
                    showMenu("Classes", options);
                    int choice = input.validateMenuInput(5, input);
                    switch (choice) {
                        case 0: // stop the loop
                            return;
                        case 1: // insert class
                            if (!act.inputClass(conn, room, input)) {
                                System.out.println("Error inserting Class");
                                return;
                            }
                            break;
                        case 2: // delete class
                            if (!act.deleteClass(conn, room, input)) {
                                System.out.println("Error deleting Class");
                                return;
                            }
                            break;
                        case 3: { // insert multiple
                            act.inputClasses(conn, room, input);
                            break;
                        }
                        case 4: // update class
                            if (!act.updateClass(conn, room, input)) {
                                System.out.println("Error updating Class");
                                return;
                            }
                            break;

                        case 5: // show classes
                            act.showClasses(conn, room, input, show);
                            break;
                        default:
                            System.out.println("Invalid Choice.");
                            break;
                    }
                } catch (InputMismatchException | NumberFormatException e) {
                    System.out.println("Integer Input is causing error: ");
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not create a DB connection.");
        }
    }

    // SUBJECT MENU
    public void handleSubjectMenu(SubjectDAO subject, DBValidator db, ConsoleDisplay show,
            Input input) {
        try (Connection conn = Database.getConnection()) {
            while (true) {
                try {
                    showMenu("Subjects", options);
                    int choice = input.validateMenuInput(5, input);
                    switch (choice) {
                        case 0: // stop the loop
                            return;
                        case 1: // insert
                            if (!act.inputSubject(conn, subject, input)) {
                                System.out.println("Error inserting Subject");
                                return;
                            }
                            break;
                        case 2: // delete
                            if (!act.deleteSubject(conn, subject, input)) {
                                System.out.println("Error deleting Subject");
                                return;
                            }
                            break;
                        case 3: // insert MULTIPLE
                            act.inputSubjects(conn, subject, input);
                            break;
                        case 4: // update
                            if (!act.updateSubject(conn, subject, input)) {
                                System.out.println("Error updating Subject");
                                return;
                            }
                            break;
                        case 5: // show
                            act.showSubjects(conn, subject, input, show);
                            break;
                        default:
                            System.out.println("Invalid Choice.");
                            break;
                    }
                } catch (InputMismatchException | NumberFormatException e) {
                    System.out.println("Integer Input is causing error: ");
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not create a DB connection.");
        }

    }

    // TEACHERMENU
    public void handleTeacherMenu(TeacherDAO teacher, DBValidator db, ConsoleDisplay show,
            Input input) {
        try (Connection conn = Database.getConnection()) {
            while (true) {
                try {
                    showMenu("Teachers", options);
                    int choice = input.validateMenuInput(5, input);
                    switch (choice) {
                        case 0: // stop the loop
                            return;
                        case 1: // insert
                            if (!act.inputTeacher(conn, teacher, input)) {
                                System.out.println("Error inserting Teacher");
                                return;
                            }
                            break;
                        case 2: // delete
                            if (!act.deleteTeacher(conn, teacher, input)) {
                                System.out.println("Error deleting Teacher");
                                return;
                            }
                            break;
                        case 3: // insert MULTIPLE
                            act.inputTeachers(conn, teacher, input);
                            break;
                        case 4: // update
                            if (!act.updateTeacher(conn, teacher, input)) {
                                System.out.println("Error updating Teacher");
                                return;
                            }
                            break;

                        case 5: // show
                            act.showTeachers(conn, teacher, input, show);
                            break;
                        default:
                            System.out.println("Invalid Choice.");
                            break;
                    }
                } catch (InputMismatchException | NumberFormatException e) {
                    System.out.println("Integer Input is causing error: ");
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not create a DB connection.");
        }
    }

    // STUDENT MENU
    public void handleStudentMenu(StudentDAO student, DBValidator db, ConsoleDisplay show,
            Input input) {
        try (Connection conn = Database.getConnection()) {
            while (true) {
                try {
                    showMenu("Students", options);
                    int choice = input.validateMenuInput(5, input);
                    switch (choice) {
                        case 0:
                            return; // stop the loop
                        case 1: // insert
                            if (!act.inputStudent(conn, student, input)) {
                                System.out.println("Error inserting Student.");
                                return;
                            }
                            break;
                        case 2: // delete
                            if (!act.deleteStudent(conn, student, input)) {
                                System.out.println("Error deleting Student.");
                                return;
                            }
                            break;
                        case 3: // insert MULTIPLE
                            act.inputStudents(conn, student, input);
                            break;
                        case 4: // update
                            if (!act.updateStudent(conn, student, input)) {
                                System.out.println("Error updating Student.");
                                return;
                            }
                            break;

                        case 5: // show
                            act.showStudents(conn, student, input, show);
                            break;
                        default:
                            System.out.println("Invalid Choice.");
                            break;
                    }
                } catch (InputMismatchException | NumberFormatException e) {
                    System.out.println("Integer Input is causing error.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not create a DB connection.");
        }

    }

    public void handleDatabase(DBValidator db, Input input) {

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

    public void handleStudentGrades(Input input, StudentDAO student, SubjectDAO subject,
            Actions act) {
        try (Connection conn = Database.getConnection()) {
            while (true) {
                System.out.print("Enter Student name: "); // get student
                String studentName = input.getNormalInput();
                if (!student.studentExists(conn, studentName)) {
                    System.out.println("Student does not exist.");
                    return;
                } else if (studentName.equals("0")) {
                    return;
                }

                System.out.println("1. Add Obtained Marks of every Subject\n"
                        + "2. Add Obtained marks of a Subject");
                int choice = input.validateMenuInput(2, input);

                switch (choice) {
                    case 0: {
                        return;
                    }
                    case 1: {
                        // get ClassName of Student
                        String className = student.fetchStudentClass(conn, studentName);
                        act.addClassObtMarks(conn, studentName, className, input, student, subject);
                    }
                        break;
                    case 2: { // obt marks of ONE SUBJECT
                        System.out.print("Enter Subject name: "); // get subject
                        String subjectName = input.getNormalInput();
                        if (!subject.subjectExists(conn, subjectName)) {
                            System.out.println("Subjecct does not exist.");
                            break;
                        } else if (subjectName.equals("0")) {
                            return;
                        }

                        int totalMarks = 100; // hardcode, standard // (todo: change)

                        System.out.print("Enter Total marks of " + subjectName + ": " + totalMarks + "\n");
                        System.out.print("Enter Obtained marks of " + subjectName + ": "); // get obt marks
                        int ObtMarks = input.getIntInput();
                        if (ObtMarks == 0) {
                            return;
                        }

                        if (act.addSubjectObtMarks(conn, studentName, subjectName, ObtMarks, student,
                                subject)) {
                            return;
                        }

                        break;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not create a DB connection.");
        }
    }

    // SchoolInfo Menu
    public void handleSchoolMenu(Input input, SchoolDAO school, Actions act) {
        while (true) {
            System.out.println("1. Show School Info\n2. Add School Info");
            int choice = input.validateMenuInput(2, input);

            switch (choice) {
                case 0:
                    return;
                case 1:
                    act.showSchoolInfo(school);
                    break;
                case 2:
                    act.addSchoolInfo(school, input);
                    break;
                default:
                    break;
            }
        }
    }

}
