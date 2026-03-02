package school;

import java.util.logging.*;
import java.util.List;
import classroom.*;
import database.*;
import database.DAO.*;
import display.*;

import people.Student;
import people.Teacher;

public class Actions {

    private static final Logger logger = Logger.getLogger(Actions.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "Actions");
    }

    // ADD SCHOOL INFO
    public Boolean addSchoolInfo(SchoolDAO school_dao, Input input) {

        return DBUtils.runInTransaction(conn -> {
            while (true) {
                // SCHOOL
                System.out.print("Enter Updated School name: ");
                String schoolName = input.getNormalInput();
                if (schoolName.equals("0")) {
                    return null;
                }

                School school = new School(schoolName);

                System.out.print("Enter Updated School Principle name: ");
                String principleName = input.getNormalInput();
                System.out.print("Enter Updated School Location: ");
                String location = input.getNormalInput();

                School newSchool = new School(schoolName, principleName, location);
                // if School-info is not inserted
                if (!school_dao.updateSchool(school, newSchool)) {
                    String error = "Database Creation Abort : School-Info";
                    logger.warning(error);
                } else {
                    return true;
                }
            }
        });
    }

    // show school info
    public void showSchoolInfo(SchoolDAO school_dao) {
        DBUtils.runInTransaction(conn -> {
            School school = school_dao.fetchSchool(conn);
            if (school.getName() == null) {
                System.out.println("No school info found. Please add school info first.");
                return false;
            }
            ConsoleDisplay console = new ConsoleDisplay();
            console.displaySchoolInfo(school_dao);
            return true;
        });
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

        ClassRoom cls = new ClassRoom(className);
        return DBUtils.runInTransaction(conn -> {

            if (room.ClassExists(conn, cls.getName())) {
                System.out.println("Class Already exists.");
                return true;
            }
            // for checking if class has students when deleting class

            if (choice == 2) {
                System.out.print("Enter Tuition Fee: ");
                int tuition = input.getIntInput();
                System.out.print("Enter Stationary Fee: ");
                int stationary = input.getIntInput();
                System.out.print("Enter Exam/Paper Fee: ");
                int exam = input.getIntInput();

                cls.setTuitionFee(tuition);
                cls.setStationaryFee(stationary);
                cls.setPaperFee(exam);

            }
            return room.insertClass(cls);
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

        ClassRoom cls = new ClassRoom(className);

        return room.deleteClass(cls);
    }

    public boolean updateClass(ClassDAO room, Input input) {
        while (true) {
            System.out.println(
                    "1. Update ClassName Only\n2. Update Class with Fees\n3. Only Fees of Class");
            int choice = input.validateMenuInput(3, input);

            return DBUtils.runInTransaction(conn -> {
                ClassRoom updatedCLS, cls;

                System.out.print("Enter previous ClassName: ");
                String className = input.getNormalInput();

                cls = room.fetchClass(conn, className); // fetch existing class
                if (cls.isEmpty()) {
                    System.out.println("Class does not exist.");
                    return false;
                }

                if (choice == 3) {
                    System.out.print("Enter Tuition Fee: ");
                    int tuition = input.getIntInput();
                    System.out.print("Enter Stationary Fee: ");
                    int stationary = input.getIntInput();
                    System.out.print("Enter Exam/Paper Fee: ");
                    int exam = input.getIntInput();

                    cls.setTuitionFee(tuition);
                    cls.setStationaryFee(stationary);
                    cls.setPaperFee(exam);

                    return room.updateClass(cls, cls);
                }

                System.out.print("Enter updated ClassName: ");
                String updateClassName = input.getNormalInput();

                switch (choice) {
                    case 0: {
                        return true;
                    }
                    case 1: {
                        updatedCLS = new ClassRoom(updateClassName);
                        return room.updateClass(cls, updatedCLS);
                    }
                    case 2: {
                        System.out.print("Enter Tuition Fee: ");
                        int tuition = input.getIntInput();
                        System.out.print("Enter Stationary Fee: ");
                        int stationary = input.getIntInput();
                        System.out.print("Enter Exam/Paper Fee: ");
                        int exam = input.getIntInput();

                        updatedCLS = new ClassRoom(updateClassName, tuition, stationary, exam);
                        return room.updateClass(cls, updatedCLS);
                    }
                }
                // default fallback
                return false;
            });
        }
    }

    public boolean showClasses(ClassDAO rooms, Input input, ConsoleDisplay show) {

        return DBUtils.runInTransaction(conn -> {
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
                    show.displayf(room.getName());
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

    }

    // SUBJECT

    ClassDAO room = new ClassDAO(); // ... get Class DAO

    public Boolean inputSubject(SubjectDAO subject, Input input) {
        return DBUtils.runInTransaction(conn -> {
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

                if (subject.insertSubject(className, subjectName)) {
                    return true;
                }
            }
            // unreachable but required for compilation
            // return false as default
            // return false;
        });
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

            if (subject.updateSubject(subjectName, uptName)) {
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
                    show.displayf(s.getName(), s.getClassName());
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
    public boolean inputTeacher(TeacherDAO teacher, Input input) {
        return DBUtils.runInTransaction(conn -> {
            System.out.print("Enter the Subject of Teacher: ");
            SubjectDAO subject = new SubjectDAO();
            String subjectName = input.getNormalInput();
            if (!subject.subjectExists(conn, subjectName)) {
                System.out.println("Subject does not exist.");
                return false;
            } else if (subjectName.equals("0")) {
                return false;
            }
            System.out.print("Enter the Teacher: ");
            String name = input.getNormalInput();
            if (teacher.teacherExists(conn, name)) {
                System.out.println("Teacher already exists.");
                return false;
            } else if (name.equals("0")) {
                return false;
            }

            Teacher teach = new Teacher(name, new Subjects(subjectName));

            return teacher.insertTeacher(teach);
        });
    }

    // method to add multiple Teachers
    public void inputTeachers(TeacherDAO teacher, Input input) {
        while (true) { // ... infinite until '0' input
            boolean run = inputTeacher(teacher, input);

            if (run == false) {
                break;
            }
        }
    }

    public boolean deleteTeacher(TeacherDAO teacher, Input input) {
        System.out.print("Enter the TeacherName: ");
        String name = input.getNormalInput();

        return teacher.deleteTeacher(new Teacher(name));
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

        Teacher teach = new Teacher(name);
        Teacher newTeach = new Teacher(uptName);
        if (choice == 1) {
            return teacher.updateTeacher(teach, newTeach);
        } else if (choice == 2) {

            System.out.print("Enter the Updated SubjectName: ");
            String updateSubject = input.getNormalInput();

            newTeach.setSubject(new Subjects(updateSubject));

            return teacher.updateTeacher(teach, newTeach);

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
                Student stu = new Student(name, new ClassRoom(className));
                if (student.insertStudent(stu)) {
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
            if (student.deleteStudent(new Student(name))) {
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
            if (student.updateStudent(new Student(name), new Student(uptName))) {
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
            // add to print reciep System.out.print("Enter StudentName: ");
            String studentName = input.getNormalInput();
            show.handleFeeReciept(studentName);
        }
        // print PDF on ALL CHOICES
        if (ch == 2)
            stuPrintPDF(pdf, students, choice /* choice on what to print */, input);

    }

    public boolean showStudents(StudentDAO student, Input input, ConsoleDisplay show) {

        return DBUtils.runInTransaction(conn -> {

            List<Student> students = student.listStudents(conn);
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

    public boolean updateOrInsert(Student student, StudentDAO student_dao, Input input) {

        return DBUtils.runInTransaction(conn -> {

            GradeDAO grade_dao = new GradeDAO();

            List<Subjects> subjects = grade_dao.fetchStudentReport(conn, student.getName()); // just to get subjects
                                                                                             // list for input

            for (Subjects s : subjects) {
                while (true) {
                    System.out.print("Total marks of " + s.getName() + ": " + s.getTotalMarks());
                    System.out.print("Enter Obtained marks of " + s.getName() + ": ");
                    int marks = input.getIntInput();
                    if (marks == 0) {
                        System.out.println("Invalid input. Please enter a valid number.");
                        continue;
                    } else if (s.getTotalMarks() < marks) {
                        System.out.println("Obtained marks cannot be greater than total Marks.");
                        continue;
                    }
                    s.setObtMarks(marks);
                    student_dao.insertOrUpdateMarks(student, s);
                    break;
                }
            }
            return true;
        });
    }

}
