package display;

import java.util.List;
import java.util.logging.*;

import classroom.ClassRoom;
import classroom.Subjects;
import database.DAO.ClassDAO;
import database.DAO.SchoolDAO;
import database.DAO.StudentDAO;
import people.Student;
import school.School;

// main class - implement of Display.java
public class ConsoleDisplay implements Display {

    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(ConsoleDisplay.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "ConsoleDisplay");
    }

    public void displayf(String value1) {
        System.out.printf("|%-20s|\n", value1);

    }

    @Override
    public void displayf(String value1, String value2) {
        System.out.printf("| %-20s | %-20s |\n", value1, value2);
    }

    @Override
    public void displayf(String value1, String value2, String value3) {
        System.out.printf("|%-20s | %-20s | %-20s |\n", value1, value2, value3);
    }

    public void displayf(String value1, String value2, String value3, String value4,
            String value5) {
        System.out.printf("|%-20s | %-11s | %-14s| | %-10s | %-5s |\n", value1, value2, value3,
                value4, value5);

    }

    // --- Student Info ---
    void studentInfoReport(String name, String className, int ID) {
        try {

            System.out.println("Name: " + name);
            System.out.println("ID: " + ID);
            System.out.println("ClassName: " + className);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error printing Student Info: ", e);
        }
    }

    // --- ReportCard Table Header ---
    void TableReport(List<Subjects> data) {
        try {
             displayf("Subjects", "Total Marks", "Obtained Marks", "Percentage", "Grade");
            for (Subjects s : data) {
                displayf(s.getSubjectName(), String.valueOf(s.getMarks()),
                        String.valueOf(s.getObtmarks()), String.valueOf(s.getPercentage()),
                        String.valueOf(s.getGrade(s.getPercentage())));
            }

        } catch (

        Exception e) {
            e.printStackTrace();
        }
    }

    // --- Report Totals ---
    void ReportTotals(int totalMarks, int Obtmarks, double totalPercentage, char Grade) {
        System.out.println("---TOTALS---");
        System.out.println("Total Marks: " + totalMarks);
        System.out.println("Total Obtained Marks: " + Obtmarks);
        System.out.println("Total Percentage: " + totalPercentage);
        System.out.println("Total Grade: " + Grade);
    }

    // --- Footer - Signatories ---
    void sign() {
        try {
            SchoolDAO method = new SchoolDAO();
            School school = method.getSchoolInfo();
            System.out.println(school.getPrincipal() + "\n(Signature)");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error printing principal name: ", e);
        }
    }

    // handle the FULL creation of whole Student Report
    public void handleStudentReport(String StudentName) {
        try {
            StudentDAO student = new StudentDAO();
            if (!student.studentExists(StudentName)) {
                System.out.println("Student does not exist.");
                return;
            }

            List<Subjects> data = student.fetchStudentReport(new Student(StudentName));
            // fetching data from database

            System.out.println("STUDENT REPORT");

            studentInfoReport(StudentName, student.fetchStudentClass(StudentName),
                    student.fetchStudentID(StudentName)); // Writes
            // Student Info
            int totalMarks = 0;
            int ObtMarks = 0;
            double totalPercentage = 0;

            for (Subjects d : data) {
                totalMarks += d.getMarks();
                ObtMarks += d.getObtmarks();
            }
            totalPercentage = (ObtMarks * 100) / totalMarks;
            Subjects s = new Subjects();
            char finalGrade = s.getGrade(totalPercentage);
            TableReport(data); // create report table

            ReportTotals(totalMarks, ObtMarks, totalPercentage, finalGrade); // report totals

            System.out.println(""); // line break
            sign(); // footer of signatories

        } catch (Exception e) {
            System.err.println("Error making Student Report PDF.");
            e.printStackTrace();
        }
    }

    public void handleFeeReciept(String StudentName) {
        try {
            StudentDAO student = new StudentDAO();
            if (!student.studentExists(StudentName)) {
                System.out.println("Student does not exist.");
                return;
            }
            Student std = student.getStudentInfo(StudentName);
            SchoolDAO school = new SchoolDAO();

            School info = school.getSchoolInfo();

            System.out.println(info.getName());
            System.out.println("PAYMENT VOUCHER");
            System.out.println(info.getlocation() + "\n\n");

            // STUDENT INFO

            System.out.println("Name: " + StudentName);
            System.out.println("ID: " + std.getID());
            System.out.println("Class: " + std.getClassName());
            System.out.println("Session: " + info.getTime());

            // FEES

            System.out.println("Remarks: MONTHLY FEE");
            ClassDAO fee = new ClassDAO();
            ClassRoom room = fee.getClassFees(std.getClassName());

            int tuition = room.getTuition();
            int stationary = room.getStationary();
            int paper = room.getPaper();

            int total = tuition + stationary + paper;

            System.out.println("Payments(*)");
            System.out.println("Tuition Fee: " + tuition);
            System.out.println("Stationary Fee: " + stationary);
            System.out.println("Paper Money:" + paper);
            System.out.println("Total:" + total + "\n");

            // POLICIES

            String[] policies = {
                    "Late payment amount will be charged after due date and can't be waived. The collection on your behalf will be used for need based scholarships.\n",
                    "All Fees are non refundable and can be changed without prior notice.\n",
                    "Withholding tax @ 5% leviable effective July 01, 2013 under section 2361 of the ITO, 2001 where annual fee exceeds Rs. 200,000/-", };

            for (String policy : policies) {
                System.out.println("- " + policy);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displaySchoolInfo(SchoolDAO info) {
        School s = info.getSchoolInfo();
        if (s == null) {
            System.out.println("It is returning null;");
            return;
        }
        System.out.println("School Name: " + s.getName());
        System.out.println("Principal Name: " + s.getPrincipal());
        System.out.println("Location: " + s.getlocation());
    }

}
