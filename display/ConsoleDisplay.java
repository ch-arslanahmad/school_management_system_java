package display;

import java.util.List;
import java.util.logging.*;
import java.sql.*;

import classroom.ClassRoom;
import classroom.Subjects;
import database.DAO.*;
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
                displayf(s.getName(), String.valueOf(s.getTotalMarks()),
                        String.valueOf(s.getObtMarks()), String.valueOf(s.getPercentage()),
                        String.valueOf(Subjects.findGrade(s.getPercentage())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Report Totals ---
    void ReportTotals(int totalMarks, int Obtmarks, double totalPercentage, String Grade) {
        System.out.println("---TOTALS---");
        System.out.println("Total Marks: " + totalMarks);
        System.out.println("Total Obtained Marks: " + Obtmarks);
        System.out.println("Total Percentage: " + totalPercentage);
        System.out.println("Total Grade: " + Grade);
    }

    // --- Footer - Signatories ---
    void sign() {
        try (Connection conn = database.Database.getConnection()) {
            SchoolDAO method = new SchoolDAO();
            School school = method.fetchSchool(conn);
            if (school != null) {
                System.out.println(school.getPrincipal() + "\n(Signature)");
            } else {
                System.out.println("Principal information not available.");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error printing principal name: ", e);
        }
    }

    // handle the FULL creation of whole Student Report
    public void handleStudentReport(String StudentName) {
        try (Connection conn = database.Database.getConnection()) {
            StudentDAO student_dao = new StudentDAO();


            GradeDAO grade_dao = new GradeDAO();

            Student student = student_dao.fetchStudent(conn, StudentName);

            if (student.getName() == null) {
                System.out.println("Student does not exist.");
                return;
            }

            List<Subjects> data = grade_dao.fetchStudentReport(conn, StudentName);
            // fetching data from database

            System.out.println("STUDENT REPORT");

            Student std = student_dao.fetchStudent(conn, StudentName);

            studentInfoReport(StudentName, std.getClassName(),
                    std.getID()); // Writes Student Info
            int totalMarks = 0;
            int ObtMarks = 0;
            double totalPercentage = 0;

            for (Subjects d : data) {
                totalMarks += d.getTotalMarks();
                ObtMarks += d.getObtMarks();
            }
            if (totalMarks > 0) {
                totalPercentage = (ObtMarks * 100.0) / totalMarks;
            }
            String finalGrade = Subjects.findGrade(totalPercentage);
            TableReport(data); // create report table

            ReportTotals(totalMarks, ObtMarks, totalPercentage, finalGrade); // report totals

            System.out.println(""); // line break
            sign(); // footer of signatories

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection error in handleStudentReport: ", e);
        } catch (Exception e) {
            System.err.println("Error making Student Report PDF.");
            e.printStackTrace();
        }
    }

    public void handleFeeReciept(String StudentName) {
        try (Connection conn = database.Database.getConnection()) {
            StudentDAO student = new StudentDAO();
            if (!student.studentExists(conn, StudentName)) {
                System.out.println("Student does not exist.");
                return;
            }
            Student std = student.fetchStudent(conn, StudentName);
            SchoolDAO school = new SchoolDAO();

            School info = school.fetchSchool(conn);

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
            ClassRoom room = fee.getClassFees(conn, std.getClassName());

            int tuition = room.getTuitionFee();
            int stationary = room.getStationaryFee();
            int paper = room.getPaperFee();

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
        try (Connection conn = database.Database.getConnection()) {
            School s = info.fetchSchool(conn);
            if (s == null) {
                System.out.println("School information not available.");
                return;
            }
            System.out.println("School Name: " + s.getName());
            System.out.println("Principal Name: " + s.getPrincipal());
            System.out.println("Location: " + s.getlocation());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection error in displaySchoolInfo: ", e);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error displaying school info: ", e);
        }
    }
}
