package school;

import java.util.logging.*;
import java.util.List;
import classroom.ClassRoom;
import classroom.Subjects;
import database.DAO.ClassDAO;
import database.DAO.StudentDAO;
import database.DAO.SubjectDAO;
import database.DAO.TeacherDAO;
import display.ConsoleDisplay;
import display.Input;
import display.LogHandler;
import display.PdfDisplay;
import people.Student;
import people.Teacher;

public class Actions {

    private static final Logger logger = Logger.getLogger(Actions.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "Actions");
    }

    // CLASS with MENU (with or without fees)
    public boolean inputClass(ClassDAO room, Input input) {
        while (true) { // ... infinite until '0' input
            System.out.println("1. Add ClassName without Fees\n2. Add Class with Fees");
            int choice = input.validateMenuInput(2, input);
            switch (choice) {
            case 0: {
                return true;
            }
            case 1: {
                System.out.print("Enter ClassName: ");
                String className = input.getNormalInput();
                if (room.Classexists(className) == true) {
                    System.out.println("Class Already exists.");
                    return false;
                }

                return true;
            }
            case 2: {
                System.out.print("Enter ClassName: ");
                String className = input.getNormalInput();
                if (room.Classexists(className)) {
                    System.out.println("Class Already exists.");
                    break;
                }
                System.out.print("Enter Tuition Fee: ");
                int tuition = input.getIntInput();
                System.out.print("Enter Stationary Fee: ");
                int stationary = input.getIntInput();
                System.out.print("Enter Exam/Paper Fee: ");
                int exam = input.getIntInput();
                return room.insertWithClassFees(className, tuition, stationary, exam);
            }
            default:
                System.out.println("Invalid choice.");
                break;
            }
        }
    }

    public boolean deleteClass(ClassDAO room, Input input) {
        while (true) {
            System.out.print("Enter your ClassName (0 to exit): ");
            String className = input.getNormalInput();
            if (className.equals("0")) {
                return true;
            }
            if (!room.Classexists(className)) {
                System.out.println("Class does not exist.");
                return false;
            }
            System.out.println("Valid ClassName. Press 0 to exit.");
        }
    }

    public boolean updateClass(ClassDAO room, Input input) {
        while (true) {
            System.out.println(
                    "1. Update ClassName Only\n2. Update Class with Fees\n3. Only Fees of Class");
            int choice = input.validateMenuInput(3, input);
            switch (choice) {
            case 0: {
                return true;
            }
            case 1: {
                System.out.println("Enter previous ClassName: ");
                String className = input.getNormalInput();
                if (!room.Classexists(className)) {
                    System.out.println("Class does not exist.");
                    return false;
                }
                System.out.println("Enter updated ClassName: ");
                String updateClass = input.getNormalInput();
                if (room.updateClass(className, updateClass)) {
                    return true;
                }
            }
            case 2: {
                System.out.println("Enter previous ClassName: ");
                String className = input.getNormalInput();
                if (!room.Classexists(className)) {
                    System.out.println("Class does not exist.");
                    return false;
                }
                System.out.println("Enter updated ClassName: ");
                String updateClass = input.getNormalInput();
                if (!room.updateClass(className, updateClass)) {
                    return false;
                }
                // ENTER FEES
                System.out.println("Enter Tuition Fee: ");
                int tuition = input.getIntInput();
                System.out.println("Enter Stationary Fee: ");
                int stationary = input.getIntInput();
                System.out.println("Enter Exam/Paper Fee: ");
                int exam = input.getIntInput();
                if (room.updateClassFees(updateClass, tuition, stationary, exam)) {
                    return true;
                }
                break;
            }
            case 3:
                updateFees(room, input); // updating fee method
            default:
                break;
            }
        }
    }

    // update/set fees of a class
    public boolean updateFees(ClassDAO room, Input input) {
        System.out.println("Enter ClassName: ");
        String className = input.getNormalInput();
        if (!room.Classexists(className)) {
            System.out.println("Class does not exist");
        }
        // ENTER FEES
        System.out.println("Enter Tuition Fee: ");
        int tuition = input.getIntInput();
        System.out.println("Enter Stationary Fee: ");
        int stationary = input.getIntInput();
        System.out.println("Enter Exam/Paper Fee: ");
        int exam = input.getIntInput();
        if (room.updateClassFees(className, tuition, stationary, exam)) {
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
        choice = input.validateMenuInput(2, input); // has loop
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

    ClassDAO room = new ClassDAO(); // ... get Class DAO

    public boolean inputSubject(SubjectDAO subject, Input input) {
        System.out.print("Enter the Class: ");
        String className = input.getNormalInput();
        if (!room.Classexists(className)) {
            System.out.println("Class does not exist.");
            return false;
        }
        System.out.print("Enter the Subject: ");
        String subjectName = input.getNormalInput();
        if (subject.subjectExists(subjectName)) {
            System.out.println("Subject already exist.");
            return false;
        }
        System.out.print("Enter the Subject Total Marks: ");
        int marks = input.getIntInput();

        if (subject.insertSubject(className, subjectName, marks)) {
            return true;
        }
        return false;
    }

    // deleting subject
    public boolean deleteSubject(SubjectDAO subject, Input input) {
        System.out.print("Enter the Class: ");
        String className = input.getNormalInput();
        if (!room.Classexists(className)) { // stop if class doesnt exist
            System.out.println("Class does not exist.");
            return false;
        }
        System.out.print("Enter the Subject: ");
        String subjectName = input.getNormalInput();
        if (!subject.subjectExists(subjectName)) { // * stop if subject doesn't exist
            System.out.println("Subject does not exist.");
        }
        if (subject.deleteSubject(className, subjectName)) {
            return true;
        }
        return false;
    }

    public boolean updateSubject(SubjectDAO subject, Input input) {
        System.out.print("Enter the Class: ");
        String className = input.getNormalInput();
        if (!room.Classexists(className)) {
            System.out.println("Class does not exist.");
            return false;
        }
        System.out.print("Enter the SubjectName: ");
        String subjectName = input.getNormalInput();
        if (!subject.subjectExists(subjectName)) {
            System.out.println("Subject does not exist.");
            return false;
        }
        System.out.print("Enter the Updated Name: ");
        String uptName = input.getNormalInput();

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
        choice = input.validateMenuInput(2, input);
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
        SubjectDAO subject = new SubjectDAO();
        String subjectName = input.getNormalInput();
        if (!subject.subjectExists(subjectName)) {
            System.out.println("Subject does not exist.");
            return false;
        }
        System.out.print("Enter the Teacher: ");
        String name = input.getNormalInput();
        if (teacher.teacherExists(name)) {
            System.out.println("Teacher already exists.");
        }
        if (teacher.insertTeacher(subjectName, name)) {
            return true;
        }
        return false;
    }

    public boolean deleteTeacher(TeacherDAO teacher, Input input) {
        System.out.print("Enter the TeacherName: ");
        String name = input.getNormalInput();

        if (!teacher.teacherExists(name)) {
            System.out.println("Teacher does not exist.");
            return false;
        }
        if (teacher.deleteTeacher(name)) {
            return true;
        }
        return false;
    }

    public boolean updateTeacher(TeacherDAO teacher, Input input) {
        System.out.print("Enter the TeacherName: ");
        String name = input.getNormalInput();
        if (!teacher.teacherExists(name)) {
            System.out.println("Teacher does not exist.");
            return false;
        }
        System.out.print("Enter the Updated Name: ");
        String uptName = input.getNormalInput();
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
        choice = input.validateMenuInput(2, input);
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
        String className = input.getNormalInput();
        ClassDAO room = new ClassDAO();
        if (!room.Classexists(className)) {
            System.out.println("Class does not exist.");
            return false;
        }
        System.out.print("Enter the StudentName: ");
        String name = input.getNormalInput();
        if (student.insertStudent(className, name)) {
            return true;
        }
        return false;
    }

    public boolean deleteStudent(StudentDAO student, Input input) {
        System.out.print("Enter the StudentName: ");
        String name = input.getNormalInput();
        if (!student.studentExists(name)) {
            System.out.println("Student does not exist.");
            return false;
        }
        if (student.deleteStudent(name)) {
            return true;
        }
        return false;
    }

    public boolean updateStudent(StudentDAO student, Input input) {
        System.out.print("Enter the StudentName: ");
        String name = input.getNormalInput();
        if (!student.studentExists(name)) {
            System.out.println("Student does not exist.");
            return false;
        }
        System.out.print("Enter the Updated Name: ");
        String uptName = input.getNormalInput();
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
        // print Student report
        else if (choice == 3) {
            System.out.print("Enter StudentName: ");
            String studentName = input.getNormalInput();
            pdf.handleFeeReciept(studentName);
        }
    }

    public void ManageDisplayStu(List<Student> students /* for student list (in PDF) */,
            ConsoleDisplay show, PdfDisplay pdf, int choice, Input input) {
        System.out.println("1. Console\n2. PDF\n");
        int ch = input.validateMenuInput(2, input);

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
        choice = input.validateMenuInput(3, input);

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

}
