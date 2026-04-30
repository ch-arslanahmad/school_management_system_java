package display;

import java.util.List;
import java.util.logging.*;
import java.sql.*;

import classroom.ClassRoom;
import classroom.Subjects;
import people.Student;
import school.School;
import school.service.*;

// main class - implement of Display.java
public class ConsoleDisplay {

    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(ConsoleDisplay.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "ConsoleDisplay");
    }

    public static void displayf(String value1) {
        System.out.printf("|%-20s|\n", value1);

    }

    public static void displayf(String value1, String value2) {
        System.out.printf("| %-20s | %-20s |\n", value1, value2);
    }

    public static void displayf(String value1, String value2, String value3) {
        System.out.printf("|%-20s | %-20s | %-20s |\n", value1, value2, value3);
    }

    public static void displayf(String value1, String value2, String value3, String value4,
            String value5) {
        System.out.printf("|%-20s | %-11s | %-14s| | %-10s | %-5s |\n", value1, value2, value3,
                value4, value5);

    }

    // --- Student Info ---
    static void studentInfoReport(String name, String className, int ID) {
        try {
            System.out.println("Name: " + name);
            System.out.println("ID: " + ID);
            System.out.println("ClassName: " + className);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error printing Student Info: ", e);
        }
    }

    // --- ReportCard Table Header ---
    static void TableReport(List<Subjects> data) {
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
    static void ReportTotals(int totalMarks, int Obtmarks, double totalPercentage, String Grade) {
        System.out.println("---TOTALS---");
        System.out.println("Total Marks: " + totalMarks);
        System.out.println("Total Obtained Marks: " + Obtmarks);
        System.out.println("Total Percentage: " + totalPercentage);
        System.out.println("Total Grade: " + Grade);
    }

    // --- Footer - Signatories ---
    static void sign() {
        try {
            School school = SchoolService.getSchool();
            if (school != null && school.getPrincipal() != null) {
                System.out.println(school.getPrincipal() + "\n(Signature)");
            } else {
                System.out.println("Principal information not available.");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error printing principal name: ", e);
        }
    }

    // handle the FULL creation of whole Student Report
    public static void handleStudentReport(String StudentName) {
        try {
            Student student = StudentService.getStudent(StudentName);

            if (student == null || student.getName() == null) {
                System.out.println("Student does not exist.");
                return;
            }

            List<Subjects> data = student.getSubjects();

            System.out.println("STUDENT REPORT");

            studentInfoReport(StudentName, student.getClassName(),
                    student.getID()); // Writes Student Info
            
            int totalMarks = 0;
            int ObtMarks = 0;
            double totalPercentage = 0;

            if (data != null) {
                for (Subjects d : data) {
                    totalMarks += d.getTotalMarks();
                    ObtMarks += d.getObtMarks();
                }
            }
            if (totalMarks > 0) {
                totalPercentage = (ObtMarks * 100.0) / totalMarks;
            }
            String finalGrade = Subjects.findGrade(totalPercentage);
            TableReport(data); // create report table

            ReportTotals(totalMarks, ObtMarks, totalPercentage, finalGrade); // report totals

            System.out.println(""); // line break
            sign(); // footer of signatories

        } catch (Exception e) {
            System.err.println("Error making Student Report.");
            e.printStackTrace();
        }
    }

    public static void handleFeeReciept(String StudentName) {
        try {
            Student std = StudentService.getStudent(StudentName);
            if (std == null || std.getID() == null) {
                System.out.println("Student does not exist.");
                return;
            }

            School info = SchoolService.getSchool();
            ClassRoom room = ClassService.getClass(std.getClassName());

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

            int tuition = room.getTuitionFee() != null ? room.getTuitionFee() : 0;
            int stationary = room.getStationaryFee() != null ? room.getStationaryFee() : 0;
            int paper = room.getPaperFee() != null ? room.getPaperFee() : 0;

            int total = tuition + stationary + paper;

            System.out.println("Payments(*)");
            System.out.println("Tuition Fee: " + tuition);
            System.out.println("Stationary Fee: " + stationary);
            System.out.println("Paper Money:" + paper);
            System.out.println("Total:" + total + "\n");

            // POLICIES

            String[] policies = {
                    "Late payment amount will be charged after due date and can't be waived.",
                    "All Fees are non refundable.",
                    "Withholding tax may apply."};

            for (String policy : policies) {
                System.out.println("- " + policy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displaySchoolInfo(School school) {
        if (school == null || school.getName() == null) {
            System.out.println("School information not available.");
            return;
        }
        System.out.println("School Name: " + school.getName());
        System.out.println("Principal Name: " + school.getPrincipal());
        System.out.println("Location: " + school.getlocation());
    }
}