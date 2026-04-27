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
    public static Boolean addSchoolInfo(SchoolDAO school_dao) {

        return DBUtils.runInTransaction(conn -> {
            while (true) {
                // SCHOOL
                System.out.print("Enter Updated School name: ");
                String schoolName = Input.getNormalInput();
                if (schoolName.equals("0")) {
                    return null;
                }

                School school = new School(schoolName);

                System.out.print("Enter Updated School Principle name: ");
                String principleName = Input.getNormalInput();
                System.out.print("Enter Updated School Location: ");
                String location = Input.getNormalInput();

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

    public static void showSchoolInfo(SchoolDAO school_dao) {
        DBUtils.runInTransaction(conn -> {
            School school = school_dao.fetchSchool(conn);
            if (school.getName() == null) {
                System.out.println("No school info found. Please add school info first.");
                return false;
            }
            ConsoleDisplay.displaySchoolInfo(school_dao);
            return true;
        });
    }

    public static boolean inputClass(ClassDAO room) {

        System.out.println("1. Add ClassName without Fees\n2. Add Class with Fees");
        int choice = Input.validateMenuInput(2);

        if (choice == 0) {
            return false;
        }

        System.out.print("Enter ClassName: ");
        String className = Input.getNormalInput();

        ClassRoom cls = new ClassRoom(className);
        return DBUtils.runInTransaction(conn -> {

            if (room.ClassExists(conn, cls.getName())) {
                System.out.println("Class Already exists.");
                return true;
            }
            // for checking if class has students when deleting class

            if (choice == 2) {
                System.out.print("Enter Tuition Fee: ");
                int tuition = Input.getIntInput();
                System.out.print("Enter Stationary Fee: ");
                int stationary = Input.getIntInput();
                System.out.print("Enter Exam/Paper Fee: ");
                int exam = Input.getIntInput();

                cls.setTuitionFee(tuition);
                cls.setStationaryFee(stationary);
                cls.setPaperFee(exam);

            }
            return room.insertClass(cls);
        });

    }

    public static void inputClasses(ClassDAO room) {
        while (true) { // ... infinite until '0' input
            Boolean run = inputClass(room);

            if (!run) {
                break;
            }
        }
    }

    public static boolean deleteClass(ClassDAO room) {
        System.out.print("Enter your ClassName (0 to exit): ");
        String className = Input.getNormalInput();
        if (className.equals("0")) {
            return true;
        }

        ClassRoom cls = new ClassRoom(className);

        return room.deleteClass(cls);
    }

    public static boolean updateClass(ClassDAO room) {
        while (true) {
            System.out.println(
                    "1. Update ClassName Only\n2. Update Class with Fees\n3. Only Fees of Class");
            int choice = Input.validateMenuInput(3);

            return DBUtils.runInTransaction(conn -> {
                ClassRoom updatedCLS, cls;

                System.out.print("Enter previous ClassName: ");
                String className = Input.getNormalInput();

                cls = room.fetchClass(conn, className); // fetch existing class
                if (cls.isEmpty()) {
                    System.out.println("Class does not exist.");
                    return false;
                }

                if (choice == 3) {
                    System.out.print("Enter Tuition Fee: ");
                    int tuition = Input.getIntInput();
                    System.out.print("Enter Stationary Fee: ");
                    int stationary = Input.getIntInput();
                    System.out.print("Enter Exam/Paper Fee: ");
                    int exam = Input.getIntInput();

                    cls.setTuitionFee(tuition);
                    cls.setStationaryFee(stationary);
                    cls.setPaperFee(exam);

                    return room.updateClass(cls, cls);
                }

                System.out.print("Enter updated ClassName: ");
                String updateClassName = Input.getNormalInput();

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
                        int tuition = Input.getIntInput();
                        System.out.print("Enter Stationary Fee: ");
                        int stationary = Input.getIntInput();
                        System.out.print("Enter Exam/Paper Fee: ");
                        int exam = Input.getIntInput();

                        updatedCLS = new ClassRoom(updateClassName, tuition, stationary, exam);
                        return room.updateClass(cls, updatedCLS);
                    }
                }
                // default fallback
                return false;
            });
        }
    }

public static boolean showClasses(ClassDAO class_dao) {

        return DBUtils.runInTransaction(conn -> {
            List<ClassRoom> classroom = class_dao.listClass(conn);
            if (classroom.isEmpty()) {
                System.out.println("Classroom List is empty");
                return false;
            }

            System.out.println("1. Console\n2. PDF\n");
            int choice = Input.validateMenuInput(2);

            if (choice == 1) {
                ConsoleDisplay.displayf("ClassName");
                for (ClassRoom room : classroom) {
                    ConsoleDisplay.displayf(room.getName());
                }
                return true;
            }

            PdfDisplay pdf = new PdfDisplay();

            if (choice == 2) {
                pdf.displayClasses(classroom);
                return true;
            }
            return false;
        });
    }

    // SUBJECT
    public static Boolean inputSubject(SubjectDAO subject, ClassDAO room) {
        return DBUtils.runInTransaction(conn -> {
            while (true) {
                System.out.print("Enter the Class: ");
                String className = Input.getNormalInput();

                if (!room.ClassExists(conn, className)) {
                    System.out.println("Class does not exist.");
                    return false;
                } else if (className.equals("0")) {
                    return null;
                }
                System.out.print("Enter the Subject: ");
                String subjectName = Input.getNormalInput();
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

    public static void inputSubjects(SubjectDAO subject, ClassDAO room) {
        while (true) { // ... infinite until '0' input
            Boolean run = inputSubject(subject, room);

            if (run == null) {
                break;
            }
        }
    }

    public static boolean deleteSubject(SubjectDAO subject, ClassDAO room) {
        return DBUtils.runInTransaction(conn -> {
            System.out.print("Enter the Class: ");
            String className = Input.getNormalInput();
            if (!room.ClassExists(conn, className)) { // stop if class doesnt exist
                System.out.println("Class does not exist.");
                return false;
            }
            System.out.print("Enter the Subject: ");
            String subjectName = Input.getNormalInput();
            if (!subject.subjectExists(conn, subjectName)) { // * stop if subject doesn't exist
                System.out.println("Subject does not exist.");
                return false;
            }
            if (subject.deleteSubject(className, subjectName)) {
                return true;
            }
            return false;
        });
    }

    public static boolean updateSubject(SubjectDAO subject, ClassDAO room) {
        return DBUtils.runInTransaction(conn -> {
            System.out.print("Enter the Class: ");
            String className = Input.getNormalInput();
            if (!room.ClassExists(conn, className)) {
                System.out.println("Class does not exist.");
                return false;
            }
            System.out.print("Enter the SubjectName: ");
            String subjectName = Input.getNormalInput();
            if (!subject.subjectExists(conn, subjectName)) {
                System.out.println("Subject does not exist.");
                return false;
            }
            System.out.print("Enter the Updated Name: ");
            String uptName = Input.getNormalInput();

            if (subject.updateSubject(subjectName, uptName)) {
                return true;
            }
            return false;
        });
    }

    public static boolean showSubjects(SubjectDAO subject, ClassDAO room) {
        return DBUtils.runInTransaction(conn -> {
            List<Subjects> subjects = subject.listSubjects(conn);
            if (subjects.isEmpty()) { // check if list is empty
                System.out.println("Subjects List is empty");
                return false;
            }

            System.out.println("1. Console\n2. PDF\n");
            int choice;
            // VALIDATING INPUT
            choice = Input.validateMenuInput(2);
            // now divide if to print in PDF / Console

            // print on CONSOLE
            if (choice == 1) {
                // this prints one column on console
                ConsoleDisplay.displayf("Subjects", "ClassName");
                for (Subjects s : subjects) {
                    ConsoleDisplay.displayf(s.getName(), s.getClassName());
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

    public static boolean inputTeacher(TeacherDAO teacher) {
        return DBUtils.runInTransaction(conn -> {
            System.out.print("Enter the Subject of Teacher: ");
            SubjectDAO subject = new SubjectDAO();
            String subjectName = Input.getNormalInput();
            if (!subject.subjectExists(conn, subjectName)) {
                System.out.println("Subject does not exist.");
                return false;
            } else if (subjectName.equals("0")) {
                return false;
            }
            System.out.print("Enter the Teacher: ");
            String name = Input.getNormalInput();
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

    public static void inputTeachers(TeacherDAO teacher) {
        while (true) { // ... infinite until '0' input
            boolean run = inputTeacher(teacher);

            if (run == false) {
                break;
            }
        }
    }

    public static boolean deleteTeacher(TeacherDAO teacher) {
        System.out.print("Enter the TeacherName: ");
        String name = Input.getNormalInput();
        return teacher.deleteTeacher(new Teacher(name));
    }

    public static boolean updateTeacher(TeacherDAO teacher) {
        System.out.println("1. Update TeacherName\n2. Update Teacher (with Subject)");
        int choice = Input.getIntInput();

        if (choice == 0) {
            return true;
        }
        System.out.print("Enter the TeacherName: ");
        String name = Input.getNormalInput();

        if (name.equals("0")) {
            return true;
        }
        System.out.print("Enter the Updated Name: ");
        String uptName = Input.getNormalInput();

        Teacher teach = new Teacher(name);
        Teacher newTeach = new Teacher(uptName);
        if (choice == 1) {
            return teacher.updateTeacher(teach, newTeach);
        } else if (choice == 2) {

            System.out.print("Enter the Updated SubjectName: ");
            String updateSubject = Input.getNormalInput();

            newTeach.setSubject(new Subjects(updateSubject));

            return teacher.updateTeacher(teach, newTeach);

        }
        return false;
    }

    public static boolean showTeachers(TeacherDAO teacher) {
        return DBUtils.runInTransaction(conn -> {
            List<Teacher> teachers = teacher.listTeachers(conn);
            if (teachers.isEmpty()) { // check if list is empty
                System.out.println("Teacher List is empty.");
                return false;
            }
            System.out.println("1. Console\n2. PDF\n");

            int choice;
            // VALIDATING INPUT
            choice = Input.validateMenuInput(2);
            // now divide if to print in PDF / Console

            // print on CONSOLE
            if (choice == 1) {
                // this prints one column on console
                ConsoleDisplay.displayf("Teachers", "Subjects");
                for (Teacher t : teachers) {
                    ConsoleDisplay.displayf(t.getName(), t.getSubjectName());
                }
            }

            // print on PDF

            PdfDisplay pdf = new PdfDisplay(); // PDF object

            if (choice == 2) {
                pdf.displayTeacher(teachers); // create Teachers PDF
                return true;
            }
            return true;

        });
    }

    public static Boolean inputStudent(StudentDAO student) {
        return DBUtils.runInTransaction(conn -> {
            while (true) {
                System.out.print("Enter the Class of Student: ");
                String className = Input.getNormalInput();
                ClassDAO room = new ClassDAO();
                if (!room.ClassExists(conn, className)) {
                    System.out.println("Class does not exist.");
                    return false;
                } else if (className.equals("0")) {
                    return null;
                }
                System.out.print("Enter the StudentName: ");
                String name = Input.getNormalInput();
                if (name.equals("0")) {
                    return null;
                }
                Student stu = new Student(name, new ClassRoom(className));
                if (student.insertStudent(stu)) {
                    return true;
                }
            }
        });
    }

    public static void inputStudents(StudentDAO student) {
        while (true) { // ... infinite until '0' input
            Boolean run = inputStudent(student);

            if (run == null) {
                break;
            }
        }
    }

    public static boolean deleteStudent(StudentDAO student) {
        return DBUtils.runInTransaction(conn -> {

            System.out.print("Enter the StudentName: ");
            String name = Input.getNormalInput();
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

    public static boolean updateStudent(StudentDAO student) {
        return DBUtils.runInTransaction(conn -> {
            System.out.print("Enter the StudentName: ");
            String name = Input.getNormalInput();
            if (!student.studentExists(conn, name)) {
                System.out.println("Student does not exist.");
                return false;
            }
            System.out.print("Enter the Updated Name: ");
            String uptName = Input.getNormalInput();
            if (student.updateStudent(new Student(name), new Student(uptName))) {
                return true;
            }
            return false;
        });
    }

    static void stuPrintPDF(PdfDisplay pdf, List<Student> students, int choice) {
        if (choice == 1) {
            // Student list in 'PDF'
            pdf.displayStudent(students); // create Students PDF
        }
        // print Student report
        else if (choice == 2) {
            System.out.print("Enter StudentName: ");
            String studentName = Input.getNormalInput();
            pdf.handleStudentReport(studentName);
        }
        // print Student report
        else if (choice == 3) {
            System.out.print("Enter StudentName: ");
            String studentName = Input.getNormalInput();
            pdf.handleFeeReciept(studentName);
        }
    }

    public static void ManageDisplayStu(List<Student> students, PdfDisplay pdf, int choice) {
        System.out.println("1. Console\n2. PDF");
        int ch = Input.validateMenuInput(2);

        // FOR CONSOLE
        // Student LIST in 'console'
        if (choice == 1 && ch == 1) {
            // this prints students columns on console
            ConsoleDisplay.displayf("Students", "Class");
            for (Student t : students) {
                ConsoleDisplay.displayf(t.getName(), t.getClassName());
            }
        }
        // student report in 'console'
        else if (choice == 2 && ch == 1) {
            System.out.print("Enter StudentName: ");
            String studentName = Input.getNormalInput();
            ConsoleDisplay.handleStudentReport(studentName);
        } else if (choice == 3 && ch == 1) {
            // add to print reciep System.out.print("Enter StudentName: ");
            String studentName = Input.getNormalInput();
            ConsoleDisplay.handleFeeReciept(studentName);
        }
        // print PDF on ALL CHOICES
        if (ch == 2)
            stuPrintPDF(pdf, students, choice /* choice on what to print */);

    }

    public static boolean showStudents(StudentDAO student) {

        System.out.print("1. Student List\n2. Individual Student Report\n3. Fee Receipt\n");

        int choice;
        choice = Input.validateMenuInput(3);

        PdfDisplay pdf = new PdfDisplay();

        if (choice == 1) {
            return DBUtils.runInTransaction(conn -> {
                List<Student> students = student.listStudents(conn);
                if (students.isEmpty()) {
                    System.out.println("Student List is empty.");
                    return false;
                }
                ManageDisplayStu(students, pdf, choice);
                return true;
            });
        }

        if (choice == 2) {
            System.out.print("Enter StudentName: ");
            String studentName = Input.getNormalInput();
            ConsoleDisplay.handleStudentReport(studentName);
            return true;
        }

        if (choice == 3) {
            System.out.print("Enter StudentName: ");
            String studentName = Input.getNormalInput();
            ConsoleDisplay.handleFeeReciept(studentName);
            return true;
        }

        return false;
    }

    public static boolean updateOrInsert(Student student, StudentDAO student_dao) {

        return DBUtils.runInTransaction(conn -> {

            GradeDAO grade_dao = new GradeDAO();

            List<Subjects> subjects = grade_dao.fetchStudentReport(conn, student.getName()); // just to get subjects
                                                                                             // list for input

            for (Subjects s : subjects) {
                while (true) {
                    System.out.print("Total marks of " + s.getName() + ": " + s.getTotalMarks());
                    System.out.print("Enter Obtained marks of " + s.getName() + ": ");
                    int marks = Input.getIntInput();
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

    public static List<Student> getStudents() {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO studentDAO = new StudentDAO();
            return studentDAO.listStudents(conn);
        });
    }


/* Fetch a single student (with marks) by name. Returns an empty Student
     * object if not found.
     */
    public static Student getStudent(String name) {
        return DBUtils.runInTransaction(conn -> {
            StudentDAO studentDAO = new StudentDAO();
            return studentDAO.fetchStudentWithMarks(conn, name);
        });
    }


    // -- S


}
