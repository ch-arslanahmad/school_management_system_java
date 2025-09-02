import java.util.List;

import java.util.InputMismatchException;

import classroom.ClassRoom;
import classroom.Subjects;
import database.DBManager;
import database.DBmaker;
import database.DAO.ClassDAO;
import database.DAO.StudentDAO;
import database.DAO.SubjectDAO;
import database.DAO.TeacherDAO;
import display.ConsoleDisplay;
import display.Input;
import display.PdfDisplay;
import people.Student;
import people.Teacher;

public class Main {

    /// ALL MENUS

    // MAIN MENU
    public void mainMenu() {
        System.out.print("\n========== School Management System ==========\n" + "1. Class >\n"
                + "2. Subject (linked to a Class) >\n" + "3. Teacher (linked to a Subject) >\n"
                + "4. Student (linked to a Class) > \n" + "5. Exit\n"
                + "===============================================\n" + "Enter your choice(1-5): ");
    }

    // easy template for making Menus with names
    public void showMenu(String name, String[] options) {
        System.out.println("\n========== " + name + " ==========\n");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i] + " " + name);
        }
        System.out.print("Enter your choice (1-" + options.length + ", 0 for exit): ");
    }

    public void showboolMenu(String name, String[] options) {
        if (!name.equals("")) {
            System.out.println("\n========== " + name + " ==========\n");
        }
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i] + " ");
        }
        System.out.print("Enter your choice: ");
    }

    // TEMPLATE OPTIONS FOR SUB - MENUS
    String[] options = { "Insert", "Delete", "Update", "Insert Multiple", "Show" };

    // CLASS MENU
    void handleClassMenu(ClassDAO room, DBManager db, ConsoleDisplay show, Input input) {
        while (true) {
            try {
                showMenu("Classes", options);
                int choice = input.getIntInput();
                switch (choice) {
                case 1: // insert class
                    if (!inputClass(room, input)) {
                        System.out.println("Error inserting Class");
                        return;
                    }
                    break;
                case 2: // delete class
                    if (!deleteClass(room, input)) {
                        System.out.println("Error deleting Class");
                        return;
                    }
                    break;
                case 3: // update class
                    if (updateClass(room, input)) {
                        System.out.println("Error updating Class");
                        return;
                    }
                    break;
                case 4: // insert multiple classes
                    db.insertClassesDB(room, input); // this method is in DBManager
                    break;
                case 5: // show classes
                    showClasses(room, input, show);
                    break;
                default:
                    if (choice == 0) {
                    } else {
                        System.out.println("Invalid Choice.");
                    }

                    break;
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Integer Input is causing error: ");
                e.printStackTrace();
            }
        }
    }

    // SUBJECT MENU
    void handleSubjectMenu(SubjectDAO subject, DBManager db, ConsoleDisplay show, Input input) {
        while (true) {
            try {
                showMenu("Subjects", options);
                int choice = input.getIntInput();
                switch (choice) {
                case 1: // insert
                    if (inputSubject(subject, input)) {
                        System.out.println("Error inserting Subject");
                        return;
                    }
                    break;
                case 2: // delete
                    if (deleteSubject(subject, input)) {
                        System.out.println("Error deleting Subject");
                        return;
                    }
                    break;
                case 3: // update
                    if (updateSubject(subject, input)) {
                        System.out.println("Error updating Subject");
                        return;
                    }
                    break;
                case 4: // insert MULTIPLE
                    if (db.insertSubjectsDB(subject, input)) {
                        System.out.println("Error inserting multiple subjects.");
                        return;
                    }
                    break;
                case 5: // show
                    showSubjects(subject, input, show);
                    break;
                default:
                    System.out.println("Subjects - Invalid Choice.");
                    break;
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Integer Input is causing error: ");
                e.printStackTrace();
            }

        }
    }

    // TEACHERMENU
    void handleTeacherMenu(TeacherDAO teacher, DBManager db, ConsoleDisplay show, Input input) {
        while (true) {
            try {
                showMenu("Teachers", options);
                int choice = input.getIntInput();
                switch (choice) {
                case 1: // insert
                    if (inputTeacher(teacher, input)) {
                        System.out.println("Error inserting Teacher");
                        return;
                    }
                    break;
                case 2: // delete
                    if (deleteTeacher(teacher, input)) {
                        System.out.println("Error deleting Teacher");
                        return;
                    }
                    break;
                case 3: // update
                    if (updateTeacher(teacher, input)) {
                        System.out.println("Error updating Teacher");
                        return;
                    }
                case 4: // insert MULTIPLE
                    if (db.insertTeachersDB(teacher, input)) {
                        System.out.println("Error inserting multiple teachers");
                        return;
                    }
                    break;
                case 5: // show
                    showTeachers(teacher, input, show);
                    break;
                default:
                    break;
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Integer Input is causing error: ");
                e.printStackTrace();
            }
        }
    }

    // STUDENT MENU
    void handleStudentMenu(StudentDAO student, DBManager db, ConsoleDisplay show, Input input) {
        while (true) {
            try {
                showMenu("Students", options);
                int choice = input.getIntInput();
                switch (choice) {
                case 1: // insert
                    if (inputStudent(student, input)) {
                        System.out.println("Error inserting Student.");
                        return;
                    }
                    break;
                case 2: // delete
                    if (deleteStudent(student, input)) {
                        System.out.println("Error deleting Student.");
                        return;
                    }
                    break;
                case 3: // update
                    if (updateStudent(student, input)) {
                        System.out.println("Error updating Student.");
                        return;
                    }
                    break;
                case 4: // insert MULTIPLE
                    if (db.insertStudentsDB(student, input)) {
                        System.out.println("Error inserting multiple Students");
                        return;
                    }
                    break;
                case 5: // show
                    showStudents(student, input, show);
                    break;
                default:
                    break;
                }
            } catch (InputMismatchException | NumberFormatException e) {
                System.out.println("Integer Input is causing error: ");
                e.printStackTrace();
            }
        }
    }

    // VALIDATION of Show Input
    public int validateShowInput(int n, Input input) {
        int choice;
        // loop to only stop when valid input given
        while (true) {
            try {
                System.out.print("Enter your choice: ");
                choice = input.getIntInput();
                if (choice > -1 && choice <= n) {
                    return choice;
                } else {
                    System.out.print("Try again. Only, (0-" + n + ")\t");
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter valid Integer value: ");
                e.printStackTrace();
            }
        }
    }

    // CLASS
    public boolean inputClass(ClassDAO room, Input input) {
        System.out.print("Enter your ClassName: ");
        if (room.insertClass(input.getNormalInput())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteClass(ClassDAO room, Input input) {
        System.out.print("Enter your ClassName: ");
        if (room.deleteClass(input.getNormalInput())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updateClass(ClassDAO room, Input input) {
        System.out.print("Enter the className: ");
        String name = input.getNormalInput();
        System.out.print("Enter the updated className: ");
        String UpdateName = input.getNormalInput();
        if (room.updateClass(name, UpdateName)) {
            return true;
        }
        return false;
    }

    public boolean showClasses(ClassDAO rooms, Input input, ConsoleDisplay show) {
        List<ClassRoom> classroom = rooms.listClass();
        if (classroom.isEmpty()) { // check if list is empty
            System.out.println("Classroom List is empty");
            return false;
        }

        System.out.println("1. Console\n2. PDF\n");
        int choice;
        // VALIDATING INPUT
        choice = validateShowInput(2, input); // has loop
        // now divide if to print in PDF / Console

        // print on CONSOLE
        if (choice == 1) {
            // this prints one column on console
            show.displayf("ClassName");
            for (ClassRoom room : classroom) {
                show.displayf(room.getClassName());
            }
        }

        // print on PDF

        PdfDisplay pdf = new PdfDisplay(); // PDF object

        if (choice == 2) {
            String path = pdf.createPDF("Classes.pdf"); // create Class PDF
            System.out.println("Path of the file is: " + path);
            pdf.displayClass(classroom);
            // this prints one column in PDF.
            pdf.closeDoc();
        }

        return true;
    }

    // SUBJECT
    public boolean inputSubject(SubjectDAO subject, Input input) {
        System.out.print("Enter the Class: ");
        String className = input.getStrInput();
        System.out.print("Enter the Subject: ");
        String subjectName = input.getStrInput();
        System.out.print("Enter the Subject Total Marks: ");
        int marks = input.getIntInput();
        if (subject.insertSubject(className, subjectName, marks)) {
            return true;
        }
        return false;
    }

    public boolean deleteSubject(SubjectDAO subject, Input input) {
        System.out.print("Enter the Class: ");
        String className = input.getStrInput();
        System.out.print("Enter the Subject: ");
        String subjectName = input.getStrInput();
        if (subject.deleteSubject(className, subjectName)) {
            return true;
        }
        return false;
    }

    public boolean updateSubject(SubjectDAO subject, Input input) {
        System.out.print("Enter the Class: ");
        String className = input.getStrInput();
        System.out.print("Enter the SubjectName: ");
        String subjectName = input.getStrInput();
        System.out.print("Enter the Updated Name: ");
        String uptName = input.getStrInput();

        if (subject.updateSubject(className, subjectName, uptName)) {
            return true;
        }
        return false;
    }

    public boolean showSubjects(SubjectDAO subject, Input input, ConsoleDisplay show) {
        List<Subjects> subjects = subject.listSubjects();
        if (subjects.isEmpty()) { // check if list is empty
            System.out.println("Subjects List is empty");
            return false;
        }

        System.out.println("1. Console\n2. PDF\n");
        int choice;
        // VALIDATING INPUT
        choice = validateShowInput(2, input);
        // now divide if to print in PDF / Console

        // print on CONSOLE
        if (choice == 1) {
            // this prints one column on console
            show.displayf("Subjects");
            for (Subjects s : subjects) {
                show.displayf(s.getSubjectName(), s.getClassName());
            }
        }

        // print on PDF

        PdfDisplay pdf = new PdfDisplay(); // PDF object

        if (choice == 2) {
            String path = pdf.createPDF("Subjects.pdf"); // create Subjects PDF
            System.out.println("Path of the file is: " + path);
            pdf.displaySubject(subjects);
            // this prints one column in PDF.
            pdf.closeDoc(); // close doc
        }

        return true;
    }

    // TEACHER
    public boolean inputTeacher(TeacherDAO teacher, Input input) {
        System.out.print("Enter the Subject of Teacher: ");
        String subjectName = input.getStrInput();
        System.out.print("Enter the Teacher: ");
        String name = input.getStrInput();
        if (teacher.insertTeacher(subjectName, name)) {
            return true;
        }
        return false;
    }

    public boolean deleteTeacher(TeacherDAO teacher, Input input) {
        System.out.print("Enter the TeacherName: ");
        String name = input.getStrInput();
        if (teacher.deleteTeacher(name)) {
            return true;
        }
        return false;
    }

    public boolean updateTeacher(TeacherDAO teacher, Input input) {
        System.out.print("Enter the TeacherName: ");
        String name = input.getStrInput();
        System.out.print("Enter the Updated Name: ");
        String uptName = input.getStrInput();
        if (teacher.updateTeacher(name, uptName)) {
            return true;
        }
        return false;
    }

    public boolean showTeachers(TeacherDAO teacher, Input input, ConsoleDisplay show) {
        List<Teacher> teachers = teacher.listTeacher();
        if (teachers.isEmpty()) { // check if list is empty
            System.out.println("Teacher List is empty.");
            return false;
        }
        System.out.println("1. Console\n2. PDF\n");

        int choice;
        // VALIDATING INPUT
        choice = validateShowInput(2, input);
        // now divide if to print in PDF / Console

        // print on CONSOLE
        if (choice == 1) {
            // this prints one column on console
            show.displayf("Teachers");
            for (Teacher t : teachers) {
                show.displayf(t.getName(), t.getSubjectName());
            }
        }

        // print on PDF

        PdfDisplay pdf = new PdfDisplay(); // PDF object

        if (choice == 2) {
            String path = pdf.createPDF("Teachers.pdf"); // create Teachers PDF
            System.out.println("Path of the file is: " + path);
            pdf.displayTeacher(teachers);
            // this prints one column in PDF.
            pdf.closeDoc();
        }
        return true;
    }

    // STUDENT
    public boolean inputStudent(StudentDAO student, Input input) {
        System.out.print("Enter the Class of Student: ");
        String className = input.getStrInput();
        System.out.print("Enter the StudentName: ");
        String name = input.getStrInput();
        if (student.insertStudent(className, name)) {
            return true;
        }
        return false;
    }

    public boolean deleteStudent(StudentDAO student, Input input) {
        System.out.print("Enter the StudentName: ");
        String name = input.getStrInput();
        if (student.deleteStudent(name)) {
            return true;
        }
        return false;
    }

    public boolean updateStudent(StudentDAO student, Input input) {
        System.out.print("Enter the StudentName: ");
        String name = input.getStrInput();
        System.out.print("Enter the Updated Name: ");
        String uptName = input.getStrInput();
        if (student.updateStudent(name, uptName)) {
            return true;
        }
        return false;
    }

    // FOR PDF in ManageDisplayStu Method
    void stuPrintPDF(PdfDisplay pdf, List<Student> students, int choice, Input input) {
        if (choice == 1) {
            // Student list in 'PDF'
            String path = pdf.createPDF("Students.pdf"); // create Students PDF
            System.out.println("Path of the file is: " + path);
            pdf.displayStudent(students);
            // this prints one column in PDF.
            pdf.closeDoc();
        }
        // print Student report
        else if (choice == 2) {
            System.out.print("Enter StudentName: ");
            String studentName = input.getNormalInput();
            pdf.handleStudentReport(studentName);

        }
    }

    public void ManageDisplayStu(List<Student> students /* for student list (in PDF) */,
            ConsoleDisplay show, PdfDisplay pdf, int choice, Input input) {
        System.out.println("1. Console\n2. PDF\n");
        int ch = validateShowInput(2, input);

        // FOR CONSOLE
        // Student LIST in 'console'
        if (choice == 1 && ch == 1) {
            // this prints students columns on console
            show.displayf("Students");
            for (Student t : students) {
                show.displayf(t.getName(), t.getClassName());
            }
        }
        // student report in 'console'
        else if (choice == 2 && ch == 1) {
            System.out.print("Enter StudentName: ");
            String studentName = input.getNormalInput();
            show.handleStudentReport(studentName);
        } else if (choice == 3 && ch == 1) {
            // add to print reciept on console
            System.out.print("Enter StudentName: ");
            String studentName = input.getNormalInput();
            show.handleFeeReciept(studentName);
        }
        // print PDF on ALL CHOICES
        if (ch == 2)
            stuPrintPDF(pdf, students, choice /* choice on what to print */, input);

    }

    public boolean showStudents(StudentDAO student, Input input, ConsoleDisplay show) {
        List<Student> students = student.listStudent();
        if (students.isEmpty()) { // check if list is empty
            System.out.println("Student List is empty.");
            return false;
        }

        System.out.print("1. Student List\n2. Individual Student Report\n3. Fee Receipt\n");

        int choice;
        // VALIDATING INPUT
        choice = validateShowInput(3, input);

        PdfDisplay pdf = new PdfDisplay(); // PDF object

        // if they want student list print
        if (choice == 1) {
            ManageDisplayStu(students, show /* Console */, pdf, choice, input);
        }
        // print on PDF

        // if they want Student Report
        if (choice == 2) {
            ManageDisplayStu(students, show /* Console */, pdf, choice, input);
        }
        // fee reciept
        if (choice == 3) {
            ManageDisplayStu(students, show, /* Console */ pdf, choice, input);
        }
        return true;
    }

    public static void main(String[] args) {
        Main call = new Main();
        Input input = new Input();
        DBManager db = new DBManager();

        // if DB structure doeI.txt";s not exist return
        if (!db.DBvalidate()) {
            return;
        }

        System.out.println(
                "There may be dummy data lying around in DB \t(recommended during 1st use ONLY).");
        System.out.println("NOTE: THIS WILL DELETE EVERYTHING in DB.");
        System.out.println("\n\nDo you want to remove all Data: (yes/no): ");

        call.showboolMenu("School Management System", new String[] { "yes", "no" });

        db.removeAllData(input);

        // CREATE DB
        System.out.println("Do you want to make a Database?");
        call.showboolMenu("", new String[] { "yes", "no" });
        while (true) {
            String choice = input.getStrInput();

            if (choice.equals("yes")) {
                DBmaker data = new DBmaker();
                data.createDB(input);
                DBmaker.closeLog(); // close its log
            } else if (choice.equals("no")) {
                break;
            } else {
                System.out.print(("Input can only be yes/no."));
            }
        }
        ConsoleDisplay show = new ConsoleDisplay();

        // now show main menu
        call.mainMenu();
        switch (input.getIntInput()) {
        case 1: // CLASS
            ClassDAO room = new ClassDAO(); // ClassDAO object
            call.handleClassMenu(room, db, show, input);
            ClassDAO.closeLog();
            break;
        case 2: // SUBJECT
            SubjectDAO subject = new SubjectDAO(); // subjectDAO object
            call.handleSubjectMenu(subject, db, show, input);
            SubjectDAO.closeLog();
            break;
        case 3: // TEACHERS
            TeacherDAO teacher = new TeacherDAO(); // TeacherDAO object
            call.handleTeacherMenu(teacher, db, show, input);
            TeacherDAO.closeLog();
            break;
        case 4: // STUDENTS
            StudentDAO student = new StudentDAO(); // StudentDAO object
            call.handleStudentMenu(student, db, show, input);
            StudentDAO.closeLog();
            break;
        default:
            ConsoleDisplay.closeLog();
            Input.closeLog();
            DBManager.closeLog();
            break; // stop the switch statement
        }

    }

    // DONT FORGET TO CLOSE DOCUMENT/FILE and other things you opened (if any)

    /*
     * // SOLUTION IS USING ResourceManager LOOK INTO THIS or someway to close all
     * resources without hassle or something
     * 
     * 
     * ResourceManager.register(fos); // FileOutputStream
     * ResourceManager.register(conn); // DB Connection
     * ResourceManager.register(pdfDoc); // PDF Document
     * 
     */

}
