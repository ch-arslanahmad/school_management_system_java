package display;

import java.io.File;
import java.util.List;
import java.util.logging.*;

import classroom.Subjects;
import database.DAO.SchoolDAO;
import database.DAO.StudentDAO;
import people.Student;
import school.School;

// main class - implement of Display.java
public class ConsoleDisplay implements Display {

    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(testPDF.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**
    static {
        //
        try {
            /*
             * // so logging is not shown in console LogManager.getLogManager().reset();
             */
            String a = "log/ConsoleDisplay.txt";
            File file = new File(a);
            // if file does not exist, create it
            if (!(file.exists())) {
                file.createNewFile();
            }
            fh = new FileHandler(a, true);
            fh.setLevel(Level.ALL);

            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());

            // checking if file exists
            if (file.exists()) {
                logger.info("Log File is created!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeLog() {
        fh.flush();
        fh.close();
        logger.info("Logger Closed.");
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

            // Dummy Data
            /*
             * String[][] subjects = { { "Mathematics", "100", "95", "95%", "A" }, {
             * "English", "100", "87", "87%", "B+" }, { "Physics", "100", "92", "92%", "A-"
             * }, { "Chemistry", "100", "85", "85%", "B" }, { "History", "100", "90", "90%",
             * "A" }, { "Physical Education", "100", "98", "98%", "A+" }, };
             */

            /*
             * for (String[] row : subjects) { for (String col : row) { PdfPCell cell = new
             * PdfPCell(new Phrase(col, normalFont));
             * cell.setHorizontalAlignment(Element.ALIGN_CENTER); cell.setPadding(5f);
             * marksTable.addCell(cell); } }
             */
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
        System.out.println("Total Obtained Marks: " + totalMarks);
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

}
