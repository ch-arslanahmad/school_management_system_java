package school;

import java.util.logging.*;
import java.sql.*;
import java.util.List;
import classroom.*;
import database.*;
import database.DAO.*;
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
                if (!school.updateSchool(schoolName, principleName, location)) {
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
    public boolean inputClass(ClassDAO room, Input input) {

        System.out.println("1. Add ClassName without Fees\n2. Add Class with Fees");
        int choice = input.validateMenuInput(2, input);

        if (choice == 0) {
            return false;
        }

        System.out.print("Enter ClassName: ");
        String className = input.getNormalInput();

        return DBUtils.runInTransaction(conn -> {

            if (room.ClassExists(conn, className)) {
                System.out.println("Class Already exists.");
                return true;
            }

            if (choice == 1) {
                return room.insertClass(className);
            }

            if (choice == 2) {
                System.out.print("Enter Tuition Fee: ");
                int tuition = input.getIntInput();
                System.out.print("Enter Stationary Fee: ");
                int stationary = input.getIntInput();
                System.out.print("Enter Exam/Paper Fee: ");
                int exam = input.getIntInput();

                return room.insertWithClassFees(className, tuition, stationary, exam);
            }

            return false;
        });

    }

    // method to add classes
    public void inputClasses(ClassDAO room, Input input) {
        while (true) { // ... infinite until '0' input
            Boolean run = inputClass(room, input);

            if (!run) {
                break;
            }
        }
    }

    public boolean deleteClass(ClassDAO room, Input input) {
        System.out.print("Enter your ClassName (0 to exit): ");
        String className = input.getNormalInput();
        if (className.equals("0")) {
            return true;
        }

        return room.deleteClass(className);
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
                    System.out.print("Enter previous ClassName: ");
                    String className = input.getNormalInput();

                    System.out.print("Enter updated ClassName: ");
                    String updateClass = input.getNormalInput();
                    return room.updateClass(className, updateClass);

                }
                case 2: {
                    System.out.print("Enter previous ClassName: ");
                    String className = input.getNormalInput();
                    System.out.print("Enter updated ClassName: ");
                    String updateClass = input.getNormalInput();
                    if (!room.updateClass(className, updateClass)) {
                        return false;
                    }
                    // ENTER FEES
                    System.out.print("Enter Tuition Fee: ");
                    int tuition = input.getIntInput();
                    System.out.print("Enter Stationary Fee: ");
                    int stationary = input.getIntInput();
                    System.out.print("Enter Exam/Paper Fee: ");
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
        System.out.print("Enter ClassName: ");
        String className = input.getNormalInput();

        DBUtils.runInTransaction(conn -> {
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
            return room.updateClassFees(className, tuition, stationary, exam);
        });
        return false;

    }

    public boolean showClasses(ClassDAO rooms, Input input, ConsoleDisplay show) {

        DBUtils.runInTransaction(conn -> {
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
                return true;
            }

            // print on PDF

            PdfDisplay pdf = new PdfDisplay(); // PDF object

            if (choice == 2) {
                pdf.displayClasses(classroom);
                // this prints one column in PDF.
                return true;
            }
            return false;
        });
        return false;

    }

    // SUBJECT

    ClassDAO room = new ClassDAO(); // ... get Class DAO

    public Boolean inputSubject(SubjectDAO subject, Input input) {
        DBUtils.runInTransaction(conn -> {
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

                if (subject.insertSubject(className, subjectName, marks)) {
                    return true;
                }
            }
        });
        return false;
    }

    // method to add multiple Subjects
    public void inputSubjects(SubjectDAO subject, Input input) {
        while (true) { // ... infinite until '0' input
            Boolean run = inputSubject(subject, input);

            if (run == null) {
                break;
            }
        }
    }

    // deleting subject
    public boolean deleteSubject(SubjectDAO subject, Input input) {
        DBUtils.runInTransaction(conn -> {
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
                return false;
            }
            if (subject.deleteSubject(className, subjectName)) {
                return true;
            }
            return false;
        });
        return false;
    }

    public boolean updateSubject(SubjectDAO subject, Input input) {
        return DBUtils.runInTransaction(conn -> {
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

            if (subject.updateSubject(className, subjectName, uptName)) {
                return true;
            }
            return false;
        });
    }

    public boolean showSubjects(SubjectDAO subject, Input input, ConsoleDisplay show) {
        return DBUtils.runInTransaction(conn -> {
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
        });
    }

    // TEACHER
    public Boolean inputTeacher(TeacherDAO teacher, Input input) {
        DBUtils.runInTransaction(conn -> {
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
            return teacher.insertTeacher(subjectName, name);
        });
        return false;
    }

    // method to add multiple Teachers
    public void inputTeachers(TeacherDAO teacher, Input input) {
        while (true) { // ... infinite until '0' input
            Boolean run = inputTeacher(teacher, input);

            if (run == null) {
                break;
            }
        }
    }

    public boolean deleteTeacher(TeacherDAO teacher, Input input) {
        System.out.print("Enter the TeacherName: ");
        String name = input.getNormalInput();

        if (teacher.deleteTeacher(name)) {
            return true;
        }
        return false;
    }

    public boolean updateTeacher(TeacherDAO teacher, Input input) {
        System.out.println("1. Update TeacherName\n2. Update Teacher (with Subject)");
        int choice = input.getIntInput();

        if (choice == 0) {
            return true;
        }
        System.out.print("Enter the TeacherName: ");
        String name = input.getNormalInput();

        if (name.equals("0")) {
            return true;
        }
        System.out.print("Enter the Updated Name: ");
        String uptName = input.getNormalInput();
        if (choice == 1) {
            return teacher.updateTeacher(name, uptName);
        } else if (choice == 2) {

            System.out.print("Enter the Updated SubjectName: ");
            String updateSubject = input.getNormalInput();
            return teacher.updateTeacherSubject(name, uptName, updateSubject);

        }
        return false;
    }

    public boolean showTeachers(TeacherDAO teacher, Input input, ConsoleDisplay show) {

        DBUtils.runInTransaction(conn -> {

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
                return true;
            }
            return false;
           
                });
        return false;
    }

    // STUDENT
    public Boolean inputStudent(StudentDAO student, Input input) {
        DBUtils.runInTransaction(conn -> {
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
        });
        return false;
    }

    // method to add multiple Students
    public void inputStudents(StudentDAO student, Input input) {
        while (true) { // ... infinite until '0' input
            Boolean run = inputStudent(student, input);

            if (run == null) {
                break;
            }
        }
    }

    public boolean deleteStudent(StudentDAO student, Input input) {
        return DBUtils.runInTransaction(conn -> {

            System.out.print("Enter the StudentName: ");
            String name = input.getNormalInput();
            if (!student.studentExists(conn, name)) {
                System.out.println("Student does not exist.");
                return false;
            }
            if (student.deleteStudent(name)) {
                return true;
            }
            return false;
        });
    }

    public boolean updateStudent(StudentDAO student, Input input) {
        return DBUtils.runInTransaction(conn -> {
            System.out.print("Enter the StudentName: ");
            String name = input.getNormalInput();
            if (!student.studentExists(conn, name)) {
                System.out.println("Student does not exist.");
                return false;
            }
            System.out.print("Enter the Updated Name: ");
            String uptName = input.getNormalInput();
            if (student.updateStudent(name, uptName)) {
                return true;
            }
            return false;
        });
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

    public boolean showStudents(StudentDAO student, Input input, ConsoleDisplay show) {

        return DBUtils.runInTransaction(conn -> {

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

        });
    }

    public boolean addSubjectObtMarks(String studentName, String SubjectName, int ObtMarks,
            StudentDAO student, SubjectDAO subject) {
        if (student.updateStudentObtMarks(subject, studentName, SubjectName, ObtMarks)) {
            return true;
        }
        return false;
    }

    public boolean addClassObtMarks(String studentName, String className, Input input,
            StudentDAO student, SubjectDAO subject) {

        return DBUtils.runInTransaction(conn -> {
            List<Subjects> classSubjectList = subject.listClassSubjectswithMarks(conn, className);

            for (Subjects s : classSubjectList) {
                while (true) {
                    System.out.print("Total marks of " + s.getSubjectName() + ": " + s.getMarks());
                    System.out.print("Enter Obtained marks of " + s.getSubjectName() + ": ");
                    int marks = input.getIntInput();
                    if (marks == 0) {
                        System.out.println("Invalid input. Please enter a valid number.");
                        continue;
                    } else if (s.getMarks() < marks) {
                        System.out.println("Obtained marks cannot be greater than total Marks.");
                        continue;
                    }
                    addSubjectObtMarks(studentName, s.getSubjectName(), marks, student, subject);
                    break;
                }
            }
            return true;
        });
    }
}
