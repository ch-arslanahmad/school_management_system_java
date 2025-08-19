package display;

// PDF imports
import java.awt.Color; // for cell background color
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;

import people.Student;
import classroom.ClassRoom;
import classroom.Subjects;
import database.Database;
import database.DAO.ClassDAO;
import database.DAO.SchoolDAO;
import database.DAO.StudentDAO;
import database.DAO.SubjectDAO;
import school.School;
import school.SchoolData;

public class testPDF {

    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(ClassDAO.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**
    static {
        //
        try {
            /*
             * // so logging is not shown in console LogManager.getLogManager().reset();
             */
            String a = "log/testPDF.txt";
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

    // Fonts
    static final Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
    static final Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14,
            Color.DARK_GRAY);
    static final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.BLACK);
    static final Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
    static final Font BoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);

    // PDF document objects
    Document document;
    PdfWriter writer;
    Paragraph heading;
    PdfPTable table;

    public void createPDF(String path) {
        try {
            document = new Document();
            writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // (PDF) - Heading Styles
    public void setHeading(String text) {
        try {
            Font Headline = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 25);
            heading = new Paragraph(text, Headline);
            heading.setAlignment(Element.ALIGN_CENTER);
            document.add(heading);
        } catch (Exception e) {
            System.out.println("Errors occured: ");
            e.printStackTrace();
        }
    }

    // make cell bold
    public Font bold() {
        Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        return bold;
    }

    // (headcell) - basic style
    public PdfPCell headCell(String text) {
        PdfPCell head = new PdfPCell(new Phrase(text, bold()));
        head.setPadding(8);
        head.setBorderWidth(0.01f);
        head.setBorderColor(new Color(128, 128, 128));
        return head;

    }

    // (cell) - basic style
    public PdfPCell styleCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(8f);
        cell.setBorderWidth(0.01f);
        cell.setBorderColor(new Color(128, 128, 128));
        return cell;
    }

    // create Table with n number of columns
    public PdfPTable Table(int NofColumn) {
        table = new PdfPTable(NofColumn); // n columns
        table.setWidthPercentage(70);

        // spacing before/after table
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        return table;
    }

    // --- School Name ---
    void addInstitutionHeader(String title) {
        // "PARKLAND HIGH SCHOOL"
        // STUDENT REPORT CARD

        try {
            SchoolDAO method = new SchoolDAO();
            School school = method.getSchoolInfo();
            Paragraph schoolName = new Paragraph(school.getName(), titleFont);
            schoolName.setAlignment(Element.ALIGN_CENTER);
            document.add(schoolName);

            Paragraph reportTitle = new Paragraph(title, subTitleFont);
            reportTitle.setAlignment(Element.ALIGN_CENTER);
            reportTitle.setSpacingAfter(15f);
            document.add(reportTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Easiest way to write student details in a two column way while making label
     * bold and its value, not bold. Hence why method takes input of label1, value1,
     * label2, value2 then fonts or styles of these, label and value respectively.
     * It essentially makes it easier to call as i have to call this method and
     * everything and have to provide, labels and values and their style/font. And
     * the actual process is handled by this method
     */

    // add Student Detail Row - Template
    private static void addStudentRow(PdfPTable table, String label1, String value1, String label2,
            String value2, Font labelFont, Font valueFont) {

        // First phrase: label1 + value1
        Phrase phrase1 = new Phrase();
        phrase1.add(new Chunk(label1 + ": ", labelFont));
        phrase1.add(new Chunk(value1, valueFont));

        PdfPCell cell1 = new PdfPCell(phrase1);
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setPadding(4);

        // Second phrase: label2 + value2
        Phrase phrase2 = new Phrase();
        phrase2.add(new Chunk(label2 + ": ", labelFont));
        phrase2.add(new Chunk(value2, valueFont));

        PdfPCell cell2 = new PdfPCell(phrase2);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setPadding(4);

        // Add both cells in same row
        table.addCell(cell1);
        table.addCell(cell2);

    }

    // --- Student Info ---
    void studentInfoReport(String name, String className) {
        try {

            table = new PdfPTable(2);
            table.setWidthPercentage(90);
            table.setWidths(new float[] { 2, 1.5f }); // controls spacing
            // Name Row
            addStudentRow(table, "Name", name, "Class", className, BoldFont, normalFont);

            addStudentRow(table, "ID", "123456", "Year", "2025", BoldFont, normalFont);

            document.add(table);

            lineBreak();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- ReportCard Table Header ---
    void TableReport() {
        try {

            PdfPTable marksTable = new PdfPTable(5);
            marksTable.setWidthPercentage(90);
            marksTable.setSpacingAfter(15f);
            marksTable.setWidths(new float[] { 2, 1, 1, 1, 1 }); // controls spacing

            int headSize = 10;
            Color headColor = new Color(220, 220, 220);

            // Headers with background color
            PdfPCell h1 = new PdfPCell(new Phrase("Subject", headerFont));
            h1.setBackgroundColor(headColor);
            h1.setHorizontalAlignment(Element.ALIGN_CENTER);
            h1.setPadding(headSize); // add space
            marksTable.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Phrase("T. Marks", headerFont));
            h2.setBackgroundColor(headColor);
            h2.setHorizontalAlignment(Element.ALIGN_CENTER);
            h2.setPadding(headSize);
            marksTable.addCell(h2);

            PdfPCell h3 = new PdfPCell(new Phrase("Marks", headerFont));
            h3.setBackgroundColor(headColor);
            h3.setHorizontalAlignment(Element.ALIGN_CENTER);
            h3.setPadding(headSize);
            marksTable.addCell(h3);

            PdfPCell h4 = new PdfPCell(new Phrase("(%)", headerFont));
            h4.setBackgroundColor(headColor);
            h4.setHorizontalAlignment(Element.ALIGN_CENTER);
            h4.setPadding(headSize);

            marksTable.addCell(h4);

            PdfPCell h5 = new PdfPCell(new Phrase("Grade", headerFont));
            h5.setBackgroundColor(headColor);
            h5.setHorizontalAlignment(Element.ALIGN_CENTER);
            h5.setPadding(headSize);
            marksTable.addCell(h5);

            // Dummy Data
            String[][] subjects = { { "Mathematics", "100", "95", "95%", "A" },
                    { "English", "100", "87", "87%", "B+" },
                    { "Physics", "100", "92", "92%", "A-" },
                    { "Chemistry", "100", "85", "85%", "B" },
                    { "History", "100", "90", "90%", "A" },
                    { "Physical Education", "100", "98", "98%", "A+" }, };

            for (String[] row : subjects) {
                for (String col : row) {
                    PdfPCell cell = new PdfPCell(new Phrase(col, normalFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(5f);
                    marksTable.addCell(cell);
                }
            }

            document.add(marksTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Totals of Report---
    void ReportTotals(int totalmarks, int totalObtmarks, float percentage, String Grade) {
        try {
            // marks
            Phrase mark = new Phrase();
            mark.add(new Chunk("Total Marks", BoldFont));
            mark.add(new Chunk(String.valueOf(totalmarks) + " / ", normalFont));
            mark.add(new Chunk(String.valueOf(totalObtmarks) + " / ", normalFont));

            // percentage
            Phrase perc = new Phrase();
            perc.add(new Chunk("Percentage", BoldFont));
            perc.add(new Chunk(String.valueOf(percentage) + "%", normalFont));

            // final Grade
            Phrase grade = new Phrase();
            grade.add(new Chunk("Grade", BoldFont));
            grade.add(new Chunk(Grade, normalFont));

            Paragraph totals = new Paragraph();
            totals.add(mark);
            totals.add(perc);
            totals.add(grade);

            // formatting
            totals.setAlignment(Element.ALIGN_RIGHT);
            totals.setSpacingAfter(25f);

            // add to document
            document.add(totals);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Footer - Signatories ---
    void sign() {
        try {
            SchoolDAO method = new SchoolDAO();
            School school = method.getSchoolInfo();

            PdfPTable signTable = new PdfPTable(2);
            signTable.setWidthPercentage(95);

            // phrase: principal + sign
            Phrase princpalSign = new Phrase();
            princpalSign.add(new Chunk(school.getPrincipal() + "\n(Signature)", normalFont));

            PdfPCell rightSign = new PdfPCell(princpalSign);
            rightSign.setBorder(PdfPCell.NO_BORDER);
            rightSign.setHorizontalAlignment(Element.ALIGN_CENTER);

            // add cell
            signTable.addCell(rightSign);

            // add that in the doc
            document.add(signTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // create the whole Student Report
    void createStudentReport(String StudentName) {
        try {
            StudentDAO student = new StudentDAO();
            StudentName = "Ahmed Raza";
            if (!student.studentExists(StudentName)) {
                System.out.println("Student does not exist.");
                return;
            }

            createPDF("Student.pdf");

            addInstitutionHeader("STUDENT REPORT");

            studentInfoReport(StudentName, student.fetchStudentClass(StudentName)); // Writes
                                                                                    // Student Info

            TableReport(); // create report table

            ReportTotals(600, 599, 99, "A+"); // report totals

            lineBreak(); // line break
            sign(); // footer of signatories

            closeDoc(); // closing the doc
        } catch (Exception e) {
            System.err.println("Error making Student Report PDF.");
            e.printStackTrace();
        }
    }

    // create ClassTable

    public void classTable() {
        try {
            createPDF("classes.pdf");
            setHeading("Classes");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error making PDF & SETTING HEADING.");
            return; // stop if theres an error
        }

        try {
            table = Table(1);
            ClassDAO room = new ClassDAO();
            List<ClassRoom> classList = room.listClass();
            table.addCell(headCell("Class"));
            for (ClassRoom cls : classList) {
                table.addCell(styleCell(String.valueOf(cls.getClassName())));
            }
            document.add(table);
            closeDoc();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error making ClassTable PDF: ", e);
        }

    }

    // create Subject Table

    public void subjectTable() {
        try {
            createPDF("subject.pdf");
            setHeading("Subject");
        } catch (Exception e) {
            logger.log(Level.WARNING, "(Subject) Error making PDF & SETTING HEADING.");
            return; // stop if theres an error
        }

        try {
            table = Table(2);
            SubjectDAO sub = new SubjectDAO();
            List<Subjects> subjectList = sub.listSubjects();
            table.addCell(headCell("Subject"));
            table.addCell(headCell("Class"));
            for (Subjects cls : subjectList) {
                table.addCell(styleCell(String.valueOf(cls.getSubjectName())));
                table.addCell(styleCell(cls.getSubjectName()));
                table.addCell(styleCell(cls.getClassName()));
            }
            document.add(table);
            closeDoc();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error making subjectTable PDF: ", e);
        }
    }

    // method to write student classes list
    public void studentClassTable() {
        // create files
        try {
            createPDF("studentClass.pdf");
            setHeading("Classes : Students");
        } catch (Exception e) {
            logger.log(Level.WARNING, "(studentClass) Error making PDF & SETTING HEADING.");
            return; // stop if theres an error
        }

        // tables
        try {
            table = Table(2);
            StudentDAO stu = new StudentDAO();
            List<Student> studentClasses = stu.listStudent();
            table.addCell(headCell("Classes"));
            table.addCell(headCell("Students"));
            for (Student cls : studentClasses) {
                table.addCell(styleCell(cls.getClassName()));
                table.addCell(styleCell(cls.getName()));
            }
            document.add(table);
            closeDoc();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error making studentClassTable PDF: ", e);
        }
    }

    // method to make individual Student Report FILE (only with nothing)
    public void makeStudentReportFile() {
        // create files
        try {
            createPDF("studentClass.pdf");
            setHeading("Student Report");
        } catch (Exception e) {
            logger.log(Level.WARNING, "(studentReport) Error making PDF & SETTING HEADING.");
            return; // stop if theres an error
        }

    }

    // create Table - Testing Table
    public void createTeTable() {
        table = new PdfPTable(1); // 1 columns
        table.setWidthPercentage(70);

        table.setSpacingBefore(15f);
        table.setSpacingAfter(15f);

        table.addCell(headCell("Class"));
        for (int i = 0; i < 5; i++) {
            try {
                table.addCell(styleCell(String.valueOf(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // add that table
        try {
            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lineBreak() {
        try {
            document.add(new Paragraph(" "));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while adding LineBreak", e);
        }
    }

    // (PDF) close PDF writer & 'Doc'
    public boolean closeDoc() {
        try {
            if (document != null) {
                document.close();
            }
            if (writer != null) {
                writer.close();
            }
            return true;

        } catch (Exception e) {
            System.out.println("Errors while Closing PDF writer or Doc: ");
            e.printStackTrace();
            return false;
        }
    }

    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        testPDF pdf = new testPDF();

        try {
            pdf.studentClassTable();
            pdf.closeDoc();
            Database.closeCon();
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("Logging Test Complete");
        closeLog();

    }
}
