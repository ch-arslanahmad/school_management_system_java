package display;

import java.util.logging.*;
import java.util.List;
import classroom.Subjects;
import java.util.InputMismatchException;

import database.*;
import database.DAO.*;
import people.Student;
import school.Actions;

public class MenuHandler {

    private static final Logger logger = Logger.getLogger(MenuHandler.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "MenuHandler");
    }

    /// ALL MENUS

    // MAIN MENU
    public void mainMenu() {
        String[] menu = {
                "School-Info",
                "Class >",
                "Subject (linked to a Class) >",
                "Teacher (linked to a Subject) >",
                "Student (linked to a Class)",
                "Student Grades",
                "Options"
        };

        showSimpleMenu("School Management System", menu);
    }

    // easy template for making Menus with names
    public void showMenu(String name, String[] options) {
        System.out.println("\n========== " + name + " ==========\n");
        System.out.println("0. Back/Exit");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i] + " " + name);
        }
    }

    public void showSimpleMenu(String name, String[] options) {
        System.out.println("\n========== " + name + " ==========\n");
        System.out.println("0. Back/Exit");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }

    // TEMPLATE OPTIONS FOR SUB - MENUS
    String[] options = { "Insert", "Delete", "Insert Multiple", "Update", "Show" };

    // CLASS MENU
    public void handleClassMenu(ClassDAO room, DBValidator db, ConsoleDisplay show) {
        while (true) {
            try {
                showMenu("Classes", options);

                int choice = Input.validateMenuInput(5);
                switch (choice) {
                    case 0: // stop the loop
                        return;
                    case 1: // insert class
                        if (!Actions.inputClass(room)) {
                            System.out.println("Error inserting Class");
                            return;
                        }
                        break;
                    case 2: // delete class
                        if (!Actions.deleteClass(room)) {
                            System.out.println("Error deleting Class");
                            return;
                        }
                        break;
                    case 3: { // insert multiple
                        Actions.inputClasses(room);
                        break;
                    }
                    case 4: // update class
                        if (!Actions.updateClass(room)) {
                            System.out.println("Error updating Class");
                            return;
                        }
                        break;

                    case 5: // show classes
                        Actions.showClasses(room, show);
                        break;
                    default:
                        System.out.println("Invalid Choice.");
                        break;
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    // SUBJECT MENU
    public void handleSubjectMenu(SubjectDAO subject, ClassDAO room, DBValidator db, ConsoleDisplay show) {
        while (true) {
            try {
                showMenu("Subjects", options);

                int choice = Input.validateMenuInput(5);
                switch (choice) {
                    case 0: // stop the loop
                        return;
                    case 1: // insert
                        if (!Actions.inputSubject(subject, room)) {
                            System.out.println("Error inserting Subject");
                            return;
                        }
                        break;
                    case 2: // delete
                        if (!Actions.deleteSubject(subject, room)) {
                            System.out.println("Error deleting Subject");
                            return;
                        }
                        break;
                    case 3: // insert MULTIPLE
                        Actions.inputSubjects(subject, room);
                        break;
                    case 4: // update
                        if (!Actions.updateSubject(subject, room)) {
                            System.out.println("Error updating Subject");
                            return;
                        }
                        break;
                    case 5: // show
                        Actions.showSubjects(subject, room, show);
                        break;
                    default:
                        System.out.println("Invalid Choice.");
                        break;
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    // TEACHERMENU
    public void handleTeacherMenu(TeacherDAO teacher, DBValidator db, ConsoleDisplay show) {
        while (true) {
            try {
                showMenu("Teachers", options);

                int choice = Input.validateMenuInput(5);
                switch (choice) {
                    case 0: // stop the loop
                        return;
                    case 1: // insert
                        if (!Actions.inputTeacher(teacher)) {
                            System.out.println("Error inserting Teacher");
                            return;
                        }
                        break;
                    case 2: // delete
                        if (!Actions.deleteTeacher(teacher)) {
                            System.out.println("Error deleting Teacher");
                            return;
                        }
                        break;
                    case 3: // insert MULTIPLE
                        Actions.inputTeachers(teacher);
                        break;
                    case 4: // update
                        if (!Actions.updateTeacher(teacher)) {
                            System.out.println("Error updating Teacher");
                            return;
                        }
                        break;

                    case 5: // show
                        Actions.showTeachers(teacher, show);
                        break;
                    default:
                        System.out.println("Invalid Choice.");
                        break;
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    // STUDENT MENU
    public void handleStudentMenu(StudentDAO student, DBValidator db, ConsoleDisplay show) {
        while (true) {
            try {
                showMenu("Students", options);
                int choice = Input.validateMenuInput(5);
                switch (choice) {
                    case 0:
                        return; // stop the loop
                    case 1: // insert
                        if (!Actions.inputStudent(student)) {
                            System.out.println("Error inserting Student.");
                            return;
                        }
                        break;
                    case 2: // delete
                        if (!Actions.deleteStudent(student)) {
                            System.out.println("Error deleting Student.");
                            return;
                        }
                        break;
                    case 3: // insert MULTIPLE
                        Actions.inputStudents(student);
                        break;
                    case 4: // update
                        if (!Actions.updateStudent(student)) {
                            System.out.println("Error updating Student.");
                            return;
                        }
                        break;

                    case 5: // show
                        Actions.showStudents(student, show);
                        break;
                    default:
                        System.out.println("Invalid Choice.");
                        break;
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    public boolean handleDatabase(DBValidator db) {

        // DB setup section
        String[] options = { "Wipe dummy data and recreate DB (recommended for first use)",
                "Keep existing data" };
        showMenu("Database Setup", options);
        int dbChoice = Input.validateMenuInput(2);

        switch (dbChoice) {
            case 0: {
                System.out.println("\nExiting Setup.\n");
                return true;
            }
            case 1:
                System.out.println(
                        "⚠ WARNING: This will delete EVERYTHING. Type 'CONFIRM' to proceed: ");
                String confirm = Input.getNormalInput();
                if (confirm.equalsIgnoreCase("CONFIRM")) {
                    if (!db.delDB()) {
                        System.out.println("Unable to delete DB.");
                        break;
                    }
                    DBmaker data = new DBmaker();
                    data.createDB();
                    System.out.println("Database wiped and recreated successfully.");
                } else {
                    System.out.println("Cancelled wipe. Keeping existing data.");
                }
                break;

            case 2:
                if (!db.DBvalidate()) {
                    System.out.println("No valid database found. Creating a new one...");
                    DBmaker data = new DBmaker();
                    data.createDB();
                } else {
                    System.out.println("Using existing database.");
                }
                break;

            default:
                System.out.println("Invalid choice.");
        }
        return false;
    }

public boolean handleStudentGrades(StudentDAO student_dao, SubjectDAO subject_dao, GradeDAO grade_dao) {
        while (true) {
            System.out.print("Enter Student name: "); // get student
            String studentName = Input.getNormalInput();

            if (studentName.equals("0")) {
                return true;
            }

            DBUtils.runInTransaction(conn -> {
                if (!student_dao.studentExists(conn, studentName)) {
                    System.out.println("Student does not exist.");
                    return false;
                }

                System.out.println("1. Add Obtained Marks of every Subject\n"
                        + "2. Add Obtained marks of a Subject");

                int choice = Input.validateMenuInput(2);

                Student stu = student_dao.fetchStudent(conn, studentName);

                if (choice == 0) {
                    return true;
                } else if (choice == 1) {
                    // get subjects for the student and prompt for obtained marks one by one
                    List<Subjects> subjects = grade_dao.fetchStudentReport(conn, studentName);
                    for (Subjects sub : subjects) {
                        int total = (sub.getTotalMarks() != 0) ? sub.getTotalMarks() : 100;
                        System.out.print("Enter Obtained marks for '" + sub.getName() + "' (Total " + total
                                + ") [enter -1 to skip]: ");
                        int obt;
                        try {
                            obt = Input.getIntInput();
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number, skipping.");
                            continue;
                        }
                        if (obt == -1) {
                            continue; // skip this subject
                        }
                        sub.setObtMarks(obt);
                        if (!student_dao.insertOrUpdateMarks(stu, sub)) {
                            System.out.println("Failed to update marks for '" + sub.getName() + "'.");
                        }
                    }
                } else if (choice == 2) { // Add Obtained marks of a Subject
                    System.out.print("Enter Subject name: "); // get subject
                    String subjectName = Input.getNormalInput();

                    Subjects sub = subject_dao.fetchSubject(conn, subjectName); // check if subject exists

                    if (sub == null) {
                        System.out.println("Subject does not exist.");
                        return false;
                    } else if (subjectName.equals("0")) {
                        return true;
                    }

                    int totalMarks = (sub.getTotalMarks() != 0) ? sub.getTotalMarks() : 100; // prefer subject total if
                                                                                 // available

                    System.out.print("Enter Obtained marks of " + subjectName + " (Total " + totalMarks
                            + ") "); // get obt marks
                    int ObtMarks;
                    try {
                        ObtMarks = Input.getIntInput();
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number.");
                        return true;
                    }
                    if (ObtMarks == -1) {
                        return true;
                    }

                    sub.setObtMarks(ObtMarks);

                    if (student_dao.insertOrUpdateMarks(stu, sub)) {
                        System.out.println(
                                "Successfully updated marks for " + studentName + " in subject " + subjectName);
                    } else {
                        System.out.println("Failed to update marks for " + studentName + " in subject " + subjectName);
                    }
                }
                return false;
            });

        }

    }

    // SchoolInfo Menu
    public void handleSchoolMenu(SchoolDAO school) {
        while (true) {
            System.out.println("1. Show School Info\n2. Add School Info");

            int choice = Input.validateMenuInput(2);

            switch (choice) {
                case 0:
                    return;
                case 1:
                    Actions.showSchoolInfo(school);
                    break;
                case 2:
                    Actions.addSchoolInfo(school);
                    break;
                default:
                    break;
            }
        }
    }

    // Run the main menu loop from MenuHandler so this class fully
    // manages showing menus, reading input and dispatching handlers.
    // This complements Main.java which can simply create the required
    // helpers and call this method to start the UI loop.
    public void runMainLoop(DBValidator db, ConsoleDisplay show,
            ClassDAO room, SubjectDAO subject_dao, TeacherDAO teacher_dao,
            StudentDAO student_dao, SchoolDAO school, GradeDAO grade_dao) {

        boolean run = true;
        while (run) {
            // Show top-level menu
            mainMenu();
            int choice = Input.validateMenuInput(7);
            switch (choice) {
                case 0 -> { // ... stop the loop
                    System.out.println("Exiting Program.");
                    run = false;
                }
                // handles school INFO
                case 1 -> handleSchoolMenu(school);
                // CLASS
                case 2 -> handleClassMenu(room, db, show);
                // SUBJECT
                case 3 -> handleSubjectMenu(subject_dao, room, db, show);
                // TEACHERS
                case 4 -> handleTeacherMenu(teacher_dao, db, show);
                // STUDENTS
                case 5 -> handleStudentMenu(student_dao, db, show);
                // handle grades of student
                case 6 -> handleStudentGrades(student_dao, subject_dao, grade_dao);
                case 7 -> {
                    run = handleDatabase(db);
                    System.out.println("Exiting Setup.");
                }
                // ... default
                default -> System.out.println("Invalid Choice.");
            }
        }
    }

}
