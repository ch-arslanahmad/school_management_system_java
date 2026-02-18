package school;

import java.util.logging.*;
import java.sql.*;
import java.util.List;
import classroom.ClassRoom;
import classroom.Subjects;
import database.DAO.ClassDAO;
import database.DAO.SchoolDAO;
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

    // ADD SCHOOL INFO
    public Boolean addSchoolInfo(SchoolDAO school, Input input) {
        try (Connection conn = database.Database.getConnection()) {
            while (true) {
                // SCHOOL
                System.out.print("Enter Updated School name: ");
                String schoolName = input.getNormalInput();
                if (schoolName.equals("0")) {
                    return null;
                }
                System.out.print("Enter Updated School Principle name: ");
                String principleName = input.getNormalInput();
                System.out.print("Enter Updated School Location: ");
                String location = input.getNormalInput();
                // if School-info is not inserted
                if (!school.updateSchool(conn, schoolName, principleName, location)) {
                    String error = "Database Creation Abort : School-Info";
                    logger.warning(error);
                } else {
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection error: ", e);
        }
        return false;
    }

    // show school info
    public void showSchoolInfo(SchoolDAO school) {
        ConsoleDisplay console = new ConsoleDisplay();
        console.displaySchoolInfo(school);
    }

    // CLASS with MENU (with or without fees)
    public Boolean inputClass(Connection conn, ClassDAO room, Input input) {
        // ↑↑↑ updating from primitive data-type to non-primitive Wrapper datatypes to
        // allow null, so to know that the user stopped the method or an error
        while (true) { // ... infinite until '0' input
            System.out.println("1. Add ClassName without Fees\n2. Add Class with Fees");
            int choice = input.validateMenuInput(2, input);
            switch (choice) {
                case 0: {
                    return null;
                }
                case 1: {
                    System.out.print("Enter ClassName: ");
                    String className = input.getNormalInput();
                    if (room.ClassExists(conn, className) == true) {
                        System.out.println("Class Already exists.");
                        return false;
                    }
                    return room.insertClass(conn, className);
                }
                case 2: {
                    System.out.print("Enter ClassName: ");
                    String className = input.getNormalInput();
                    if (room.ClassExists(conn, className)) {
                        System.out.println("Class Already exists.");
                        break;
                    }
                    System.out.print("Enter Tuition Fee: ");
                    int tuition = input.getIntInput();
                    System.out.print("Enter Stationary Fee: ");
                    int stationary = input.getIntInput();
                    System.out.print("Enter Exam/Paper Fee: ");
                    int exam = input.getIntInput();
                    return room.insertWithClassFees(conn, className, tuition, stationary, exam);
                }
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }

    }

    // method to add classes
    public void inputClasses(Connection conn, ClassDAO room, Input input) {
        while (true) { // ... infinite until '0' input
            Boolean run = inputClass(conn, room, input);

            if (run == null) {
                break;
            }
        }
    }

    public boolean deleteClass(Connection conn, ClassDAO room, Input input) {
        while (true) {
            System.out.print("Enter your ClassName (0 to exit): ");
            String className = input.getNormalInput();
            if (className.equals("0")) {
                return true;
            }
            if (!room.ClassExists(conn, className)) {
                System.out.println("Class does not exist.");
                return false;
            }
            if (room.deleteClass(conn, className)) {
                return true;
            }
        }
    }

    public boolean updateClass(Connection conn, ClassDAO room, Input input) {
        while (true) {
            System.out.println(
                    "1. Update ClassName Only\n2. Update Class with Fees\n3. Only Fees of Class");
            int choice = input.validateMenuInput(3, input);
            switch (choice) {
                case 0: {
                    return true;
                }
                case 1: {
                    System.out.print("Enter previous ClassName: ");
                    String className = input.getNormalInput();
                    if (!room.ClassExists(conn, className)) {
                        System.out.println("Class does not exist.");
                        return false;
                    }
                    System.out.print("Enter updated ClassName: ");
                    String updateClass = input.getNormalInput();
                    if (room.updateClass(conn, className, updateClass)) {
                        return true;
                    }
                }
                case 2: {
                    System.out.print("Enter previous ClassName: ");
                    String className = input.getNormalInput();
                    if (!room.ClassExists(conn, className)) {
                        System.out.println("Class does not exist.");
                        return false;
                    }
                    System.out.print("Enter updated ClassName: ");
                    String updateClass = input.getNormalInput();
                    if (!room.updateClass(conn, className, updateClass)) {
                        return false;
                    }
                    // ENTER FEES
                    System.out.print("Enter Tuition Fee: ");
                    int tuition = input.getIntInput();
                    System.out.print("Enter Stationary Fee: ");
                    int stationary = input.getIntInput();
                    System.out.print("Enter Exam/Paper Fee: ");
                    int exam = input.getIntInput();
                    if (room.updateClassFees(conn, updateClass, tuition, stationary, exam)) {
                        return true;
                    }
                    break;
                }
                case 3:
                    updateFees(conn, room, input); // updating fee method
                default:
                    break;
            }
        }
    }

    // update/set fees of a class
    public boolean updateFees(Connection conn, ClassDAO room, Input input) {
        System.out.print("Enter ClassName: ");
        String className = input.getNormalInput();
        if (!room.ClassExists(conn, className)) {
            System.out.println("Class does not exist");
        }
        // ENTER FEES
        System.out.print("Enter Tuition Fee: ");
        int tuition = input.getIntInput();
        System.out.print("Enter Stationary Fee: ");
        int stationary = input.getIntInput();
        System.out.print("Enter Exam/Paper Fee: ");
        int exam = input.getIntInput();
        if (room.updateClassFees(conn, className, tuition, stationary, exam)) {
            return true;
        }
        return false;
    }

    public boolean showClasses(Connection conn, ClassDAO rooms, Input input, ConsoleDisplay show) {
        List<ClassRoom> classroom = rooms.listClass(conn);
        if (classroom.isEmpty()) { // check if list is empty
            System.out.println("Classroom List is empty");
            return false;
        }

        System.out.println("1. Console\n2. PDF");
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
            pdf.displayClasses(classroom);
            // this prints one column in PDF.
        }

        return true;
    }

    // SUBJECT

    ClassDAO room = new ClassDAO(); // ... get Class DAO

    public Boolean inputSubject(Connection conn, SubjectDAO subject, Input input) {
        while (true) {
            System.out.print("Enter the Class: ");
            String className = input.getNormalInput();
            if (!room.ClassExists(conn, className)) {
                System.out.println("Class does not exist.");
                return false;
            } else if (className.equals("0")) {
                return null;
            }
            System.out.print("Enter the Subject: ");
            String subjectName = input.getNormalInput();
            if (subject.subjectExists(conn, subjectName)) {
                System.out.println("Subject already exist.");
                return false;
            } else if (subjectName.equals("0")) {
                return null;
            }
            System.out.print("Enter the Subject Total Marks: ");
            int marks = input.getIntInput();

            if (subject.insertSubject(conn, className, subjectName, marks)) {
                return true;
            }
        }
    }

    // method to add multiple Subjects
    public void inputSubjects(Connection conn, SubjectDAO subject, Input input) {
        while (true) { // ... infinite until '0' input
            Boolean run = inputSubject(conn, subject, input);

            if (run == null) {
                break;
            }
        }
    }

    // deleting subject
    public boolean deleteSubject(Connection conn, SubjectDAO subject, Input input) {
        System.out.print("Enter the Class: ");
        String className = input.getNormalInput();
        if (!room.ClassExists(conn, className)) { // stop if class doesnt exist
            System.out.println("Class does not exist.");
            return false;
        }
        System.out.print("Enter the Subject: ");
        String subjectName = input.getNormalInput();
        if (!subject.subjectExists(conn, subjectName)) { // * stop if subject doesn't exist
            System.out.println("Subject does not exist.");
        }
        if (subject.deleteSubject(conn, className, subjectName)) {
            return true;
        }
        return false;
    }

    public boolean updateSubject(Connection conn, SubjectDAO subject, Input input) {
        System.out.print("Enter the Class: ");
        String className = input.getNormalInput();
        if (!room.ClassExists(conn, className)) {
            System.out.println("Class does not exist.");
            return false;
        }
        System.out.print("Enter the SubjectName: ");
        String subjectName = input.getNormalInput();
        if (!subject.subjectExists(conn, subjectName)) {
            System.out.println("Subject does not exist.");
            return false;
        }
        System.out.print("Enter the Updated Name: ");
        String uptName = input.getNormalInput();

        if (subject.updateSubject(conn, className, subjectName, uptName)) {
            return true;
        }
        return false;
    }

    public boolean showSubjects(Connection conn, SubjectDAO subject, Input input, ConsoleDisplay show) {
        List<Subjects> subjects = subject.listSubjects(conn);
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
            show.displayf("Subjects", "ClassName");
            for (Subjects s : subjects) {
                show.displayf(s.getSubjectName(), s.getClassName());
            }
        }

        // print on PDF

        PdfDisplay pdf = new PdfDisplay(); // PDF object

        if (choice == 2) {
            pdf.displaySubject(subjects); // create Subjects PDF
        }

        return true;
    }

    // TEACHER
    public Boolean inputTeacher(Connection conn, TeacherDAO teacher, Input input) {
        while (true) {
            System.out.print("Enter the Subject of Teacher: ");
            SubjectDAO subject = new SubjectDAO();
            String subjectName = input.getNormalInput();
            if (!subject.subjectExists(conn, subjectName)) {
                System.out.println("Subject does not exist.");
                return false;
            } else if (subjectName.equals("0")) {
                return null;
            }
            System.out.print("Enter the Teacher: ");
            String name = input.getNormalInput();
            if (teacher.teacherExists(conn, name)) {
                System.out.println("Teacher already exists.");
            } else if (name.equals("0")) {
                return null;
            }
            if (teacher.insertTeacher(conn, subjectName, name)) {
                return true;
            }
        }
    }

    // method to add multiple Teachers
    public void inputTeachers(Connection conn, TeacherDAO teacher, Input input) {
        while (true) { // ... infinite until '0' input
            Boolean run = inputTeacher(conn, teacher, input);

            if (run == null) {
                break;
            }
        }
    }

    public boolean deleteTeacher(Connection conn, TeacherDAO teacher, Input input) {
        System.out.print("Enter the TeacherName: ");
        String name = input.getNormalInput();

        if (!teacher.teacherExists(conn, name)) {
            System.out.println("Teacher does not exist.");
            return false;
        }
        if (teacher.deleteTeacher(conn, name)) {
            return true;
        }
        return false;
    }

    public boolean updateTeacher(Connection conn, TeacherDAO teacher, Input input) {
        while (true) {
            System.out.println("1. Update TeacherName\n2. Update Teacher (with Subject)");
            int choice = input.getIntInput();
            switch (choice) {
                case 0:
                    return true;
                case 1: {
                    System.out.print("Enter the TeacherName: ");
                    String name = input.getNormalInput();
                    if (!teacher.teacherExists(conn, name)) {
                        System.out.println("Teacher does not exist.");
                        return false;
                    } else if (name.equals("0")) {
                        return true;
                    }
                    System.out.print("Enter the Updated Name: ");
                    String uptName = input.getNormalInput();
                    if (teacher.updateTeacher(conn, name, uptName)) {
                        return true;
                    }
                    return false;
                }
                case 2: {
                    System.out.print("Enter the TeacherName: ");
                    String name = input.getNormalInput();
                    if (!teacher.teacherExists(conn, name)) {
                        System.out.println("Teacher does not exist.");
                        break;
                    } else if (name.equals("0")) {
                        return true;
                    }
                    System.out.print("Enter the Updated Name: ");
                    String uptName = input.getNormalInput();

                    System.out.print("Enter the Updated SubjectName: ");
                    String updateSubject = input.getNormalInput();

                    if (teacher.updateTeacherSubject(conn, name, uptName, updateSubject)) {
                        return true;
                    }
                    return false;
                }
                default:
                    break;
            }
        }

    }

    public boolean showTeachers(Connection conn, TeacherDAO teacher, Input input, ConsoleDisplay show) {
        List<Teacher> teachers = teacher.listTeacher(conn);
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
            show.displayf("Teachers", "Subjects");
            for (Teacher t : teachers) {
                show.displayf(t.getName(), t.getSubjectName());
            }
        }

        // print on PDF

        PdfDisplay pdf = new PdfDisplay(); // PDF object

        if (choice == 2) {
            pdf.displayTeacher(teachers); // create Teachers PDF
        }
        return true;
    }

    // STUDENT
    public Boolean inputStudent(Connection conn, StudentDAO student, Input input) {
        while (true) {
            System.out.print("Enter the Class of Student: ");
            String className = input.getNormalInput();
            ClassDAO room = new ClassDAO();
            if (!room.ClassExists(conn, className)) {
                System.out.println("Class does not exist.");
                return false;
            } else if (className.equals("0")) {
                return null;
            }
            System.out.print("Enter the StudentName: ");
            String name = input.getNormalInput();
            if (name.equals("0")) {
                return null;
            }
            if (student.insertStudent(className, name)) {
                return true;
            }
        }
    }

    // method to add multiple Students
    public void inputStudents(Connection conn, StudentDAO student, Input input) {
        while (true) { // ... infinite until '0' input
            Boolean run = inputStudent(conn, student, input);

            if (run == null) {
                break;
            }
        }
    }

    public boolean deleteStudent(Connection conn, StudentDAO student, Input input) {
        System.out.print("Enter the StudentName: ");
        String name = input.getNormalInput();
        if (!student.studentExists(conn, name)) {
            System.out.println("Student does not exist.");
            return false;
        }
        if (student.deleteStudent(conn, name)) {
            return true;
        }
        return false;
    }

    public boolean updateStudent(Connection conn, StudentDAO student, Input input) {
        System.out.print("Enter the StudentName: ");
        String name = input.getNormalInput();
        if (!student.studentExists(conn, name)) {
            System.out.println("Student does not exist.");
            return false;
        }
        System.out.print("Enter the Updated Name: ");
        String uptName = input.getNormalInput();
        if (student.updateStudent(conn, name, uptName)) {
            return true;
        }
        return false;
    }

    // FOR PDF in ManageDisplayStu Method
    void stuPrintPDF(PdfDisplay pdf, List<Student> students, int choice, Input input) {
        if (choice == 1) {
            // Student list in 'PDF'
            pdf.displayStudent(students); // create Students PDF
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
        System.out.println("1. Console\n2. PDF");
        int ch = input.validateMenuInput(2, input);

        // FOR CONSOLE
        // Student LIST in 'console'
        if (choice == 1 && ch == 1) {
            // this prints students columns on console
            show.displayf("Students", "Class");
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

    public boolean showStudents(Connection conn, StudentDAO student, Input input, ConsoleDisplay show) {
        List<Student> students = student.listStudent(conn);
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

    public boolean addSubjectObtMarks(Connection conn, String studentName, String SubjectName, int ObtMarks,
            StudentDAO student, SubjectDAO subject) {
        if (student.updateObtMarks(conn, subject, studentName, SubjectName, ObtMarks)) {
            return true;
        }
        return false;
    }

    public void addClassObtMarks(Connection conn, String studentName, String className, Input input,
            StudentDAO student, SubjectDAO subject) {
        List<Subjects> classSubjectList = subject.listClassSubjectswithMarks(conn, className);

        for (Subjects s : classSubjectList) {
            while (true) {
                System.out.print("Total marks of " + s.getSubjectName() + ": " + s.getMarks());
                System.out.print("Enter Obtained marks of " + s.getSubjectName() + ": ");
                int marks = input.getIntInput();
                if (marks == 0) {
                    break;
                } else if (s.getMarks() < marks) {
                    System.out.println("Obtained marks cannot be greater than total Marks.");
                    break;
                }
                addSubjectObtMarks(conn, studentName, s.getSubjectName(), marks, student, subject);
            }
        }
    }

}
