package school;

import java.util.logging.*;
import java.util.List;
import classroom.*;
import database.*;
import java.sql.Connection;
import display.*;
import school.api.*; 
import database.DAO.GradeDAO;

import people.Student;
import people.Teacher;

public class Actions {

    private static final Logger logger = Logger.getLogger(Actions.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "Actions");
    }

    // ADD SCHOOL INFO
    public static Boolean addSchoolInfo() {
        while (true) {
            System.out.print("Enter Updated School name: ");
            String schoolName = Input.getNormalInput();
            if (schoolName.equals("0")) {
                return null;
            }

            System.out.print("Enter Updated School Principle name: ");
            String principleName = Input.getNormalInput();
            System.out.print("Enter Updated School Location: ");
            String location = Input.getNormalInput();

            School newSchool = new School(schoolName, principleName, location);
            if (!SchoolService.updateSchool(newSchool)) {
                String error = "Database Creation Abort : School-Info";
                logger.warning(error);
            } else {
                return true;
            }
        }
    }

    public static void showSchoolInfo() {
        School school = SchoolService.getSchool();
        if (school.getName() == null) {
            System.out.println("No school info found. Please add school info first.");
            return;
        }
        ConsoleDisplay.displaySchoolInfo(school);
    }

    public static boolean inputClass() {
        System.out.println("1. Add ClassName without Fees\n2. Add Class with Fees");
        int choice = Input.validateMenuInput(2);

        if (choice == 0) {
            return false;
        }

        System.out.print("Enter ClassName: ");
        String className = Input.getNormalInput();

        ClassRoom existing = ClassService.getClass(className);
        if (!existing.isEmpty()) {
            System.out.println("Class Already exists.");
            return true;
        }

        ClassRoom cls = new ClassRoom(className);

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

        return ClassService.addClass(cls);
    }

    public static void inputClasses() {
        while (true) {
            Boolean run = inputClass();

            if (!run) {
                break;
            }
        }
    }

    public static boolean deleteClass() {
        System.out.print("Enter your ClassName (0 to exit): ");
        String className = Input.getNormalInput();
        if (className.equals("0")) {
            return true;
        }

        ClassRoom cls = ClassService.getClass(className);
        if (cls.isEmpty()) {
            System.out.println("Class not found.");
            return false;
        }

        return ClassService.deleteClass(cls.getID());
    }

    public static boolean updateClass() {
        while (true) {
            System.out.println(
                    "1. Update ClassName Only\n2. Update Class with Fees\n3. Only Fees of Class");
            int choice = Input.validateMenuInput(3);

            System.out.print("Enter previous ClassName: ");
            String className = Input.getNormalInput();

            ClassRoom cls = ClassService.getClass(className);
            if (cls.isEmpty()) {
                System.out.println("Class does not exist.");
                return false;
            }

            System.out.print("Enter updated ClassName: ");
            String updateClassName = Input.getNormalInput();
            cls.setName(updateClassName);

            if (choice == 2 || choice == 3) {
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

            return ClassService.updateClass(cls.getID(), updateClassName);
        }
    }

public static boolean showClasses() {
        List<ClassRoom> classroom = ClassService.getClasses();
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

        if (choice == 2) {
            PdfDisplay.displayClasses(classroom);
            return true;
        }
        return false;
    }

    // SUBJECT
    public static Boolean inputSubject() {
        while (true) {
            System.out.print("Enter the Class: ");
            String className = Input.getNormalInput();

            ClassRoom cls = ClassService.getClass(className);
            if (cls.isEmpty()) {
                System.out.println("Class does not exist.");
                return false;
            } else if (className.equals("0")) {
                return null;
            }
            System.out.print("Enter the Subject: ");
            String subjectName = Input.getNormalInput();

            Subjects existing = SubjectService.getSubject(subjectName);
            if (existing.getID() != null && existing.getID() != 0) {
                System.out.println("Subject already exist.");
                return false;
            } else if (subjectName.equals("0")) {
                return null;
            }

            if (SubjectService.addSubject(className, subjectName)) {
                return true;
            }
        }
    }

    public static void inputSubjects() {
        while (true) {
            Boolean run = inputSubject();

            if (run == null) {
                break;
            }
        }
    }

    public static boolean deleteSubject() {
        System.out.print("Enter the Class: ");
        String className = Input.getNormalInput();
        ClassRoom cls = ClassService.getClass(className);
        if (cls.isEmpty()) {
            System.out.println("Class does not exist.");
            return false;
        }
        System.out.print("Enter the Subject: ");
        String subjectName = Input.getNormalInput();

        Subjects subj = SubjectService.getSubject(subjectName);
        if (subj.getID() == null || subj.getID() == 0) {
            System.out.println("Subject does not exist.");
            return false;
        }
        return SubjectService.deleteSubject(subj.getID());
    }

    public static boolean updateSubject() {
        System.out.print("Enter the Class: ");
        String className = Input.getNormalInput();
        ClassRoom cls = ClassService.getClass(className);
        if (cls.isEmpty()) {
            System.out.println("Class does not exist.");
            return false;
        }
        System.out.print("Enter the SubjectName: ");
        String subjectName = Input.getNormalInput();

        Subjects existing = SubjectService.getSubject(subjectName);
        if (existing.getID() == null || existing.getID() == 0) {
            System.out.println("Subject does not exist.");
            return false;
        }

        System.out.print("Enter the Updated Name: ");
        String uptName = Input.getNormalInput();

        return SubjectService.updateSubject(existing.getID(), uptName);
    }

    public static boolean showSubjects() {
        List<Subjects> subjects = SubjectService.getSubjects();
        if (subjects.isEmpty()) {
            System.out.println("Subjects List is empty");
            return false;
        }

        System.out.println("1. Console\n2. PDF\n");
        int choice = Input.validateMenuInput(2);

        if (choice == 1) {
            ConsoleDisplay.displayf("Subjects", "ClassName");
            for (Subjects s : subjects) {
                ConsoleDisplay.displayf(s.getName(), s.getClassName());
            }
        }

        if (choice == 2) {
            PdfDisplay.displaySubject(subjects);
        }

        return true;
    }

    public static boolean inputTeacher() {
        System.out.print("Enter the Subject of Teacher: ");
        String subjectName = Input.getNormalInput();

        Subjects subj = SubjectService.getSubject(subjectName);
        if (subj.getID() == null || subj.getID() == 0) {
            System.out.println("Subject does not exist.");
            return false;
        } else if (subjectName.equals("0")) {
            return false;
        }

        System.out.print("Enter the Teacher: ");
        String name = Input.getNormalInput();

        Teacher existing = TeacherService.getTeacher(name);
        if (existing.getID() != null && existing.getID() != 0) {
            System.out.println("Teacher already exists.");
            return false;
        } else if (name.equals("0")) {
            return false;
        }

        Teacher teach = new Teacher(name, new Subjects(subjectName));
        return TeacherService.addTeacher(teach);
    }

    public static void inputTeachers() {
        while (true) {
            boolean run = inputTeacher();

            if (run == false) {
                break;
            }
        }
    }

    public static boolean deleteTeacher() {
        System.out.print("Enter the TeacherName: ");
        String name = Input.getNormalInput();
        Teacher t = TeacherService.getTeacher(name);
        if (t.getID() == null || t.getID() == 0) {
            System.out.println("Teacher not found.");
            return false;
        }
        return TeacherService.deleteTeacher(t.getID());
    }

    public static boolean updateTeacher() {
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

        Teacher existing = TeacherService.getTeacher(name);
        if (existing.getID() == null || existing.getID() == 0) {
            System.out.println("Teacher not found.");
            return false;
        }

        if (choice == 1) {
            return TeacherService.updateTeacher(existing.getID(), uptName);
        } else if (choice == 2) {
            System.out.print("Enter the Updated SubjectName: ");
            String updateSubject = Input.getNormalInput();
            // Need to get subject ID
            Subjects subj = SubjectService.getSubject(updateSubject);
            if (subj.getID() == null || subj.getID() == 0) {
                System.out.println("Subject not found.");
                return false;
            }
            return TeacherService.updateTeacher(existing.getID(), uptName, subj.getID());
        }
        return false;
    }

    public static boolean showTeachers() {
        List<Teacher> teachers = TeacherService.getTeachers();
        if (teachers.isEmpty()) {
            System.out.println("Teacher List is empty.");
            return false;
        }
        System.out.println("1. Console\n2. PDF\n");

        int choice = Input.validateMenuInput(2);

        if (choice == 1) {
            ConsoleDisplay.displayf("Teachers", "Subjects");
            for (Teacher t : teachers) {
                ConsoleDisplay.displayf(t.getName(), t.getSubjectName());
            }
        }

        if (choice == 2) {
            PdfDisplay.displayTeacher(teachers);
            return true;
        }
        return true;
    }

    public static Boolean inputStudent() {
        while (true) {
            System.out.print("Enter the Class of Student: ");
            String className = Input.getNormalInput();

            ClassRoom cls = ClassService.getClass(className);
            if (cls.isEmpty()) {
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
            if (StudentService.addStudent(stu)) {
                return true;
            }
        }
    }

    public static void inputStudents() {
        while (true) {
            Boolean run = inputStudent();

            if (run == null) {
                break;
            }
        }
    }

    public static boolean deleteStudent() {
        System.out.print("Enter the StudentName: ");
        String name = Input.getNormalInput();
        Student stu = StudentService.getStudent(name);
        if (stu.getID() == null || stu.getID() == 0) {
            System.out.println("Student does not exist.");
            return false;
        }
        return StudentService.deleteStudent(stu.getID());
    }

    public static boolean updateStudent() {
        System.out.print("Enter the StudentName: ");
        String name = Input.getNormalInput();
        Student existing = StudentService.getStudent(name);
        if (existing.getID() == null || existing.getID() == 0) {
            System.out.println("Student does not exist.");
            return false;
        }
        System.out.print("Enter the Updated Name: ");
        String uptName = Input.getNormalInput();
        return StudentService.updateStudent(existing.getID(), uptName);
    }

    static void stuPrintPDF(List<Student> students, int choice) {
        if (choice == 1) {
            // Student list in 'PDF'
            PdfDisplay.displayStudent(students);
        }
        // print Student report
        else if (choice == 2) {
            System.out.print("Enter StudentName: ");
            String studentName = Input.getNormalInput();
            Student student = StudentService.getStudent(studentName);
            if (student != null && student.getID() != null) {
                PdfDisplay.handleStudentReport(student);
            } else {
                System.out.println("Student not found.");
            }
        }
        // print Student report
        else if (choice == 3) {
            System.out.print("Enter StudentName: ");
            String studentName = Input.getNormalInput();
            Student student = StudentService.getStudent(studentName);
            if (student != null && student.getID() != null) {
                PdfDisplay.handleFeeReciept(student);
            } else {
                System.out.println("Student not found.");
            }
        }
    }

    public static void ManageDisplayStu(List<Student> students, int choice) {
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
            stuPrintPDF(students, choice);

    }

    public static boolean showStudents() {
        System.out.print("1. Student List\n2. Individual Student Report\n3. Fee Receipt\n");

        int choice = Input.validateMenuInput(3);

        if (choice == 1) {
            List<Student> students = StudentService.getStudents();
            if (students.isEmpty()) {
                System.out.println("Student List is empty.");
                return false;
            }
            ManageDisplayStu(students, choice);
            return true;
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

    public static boolean updateOrInsert(Student student) {

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
                    StudentService.insertOrUpdateMarks(student.getName(), s.getID(), marks);
                    break;
                }
            }
            return true;
        });
    }

    public static List<Student> getStudents() {
        return StudentService.getStudents();
    }


/* Fetch a single student (with marks) by name. Returns an empty Student
     * object if not found.
     */
    public static Student getStudent(String name) {
        return StudentService.getStudent(name);
    }


    // -- S


}
