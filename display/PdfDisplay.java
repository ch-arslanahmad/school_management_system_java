package display;

// PDF imports
import java.awt.Color; // for cell background color
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;

import com.lowagie.text.*;

import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.VerticalPositionMark;

import people.Student;
import people.Teacher;
import classroom.ClassRoom;
import classroom.Subjects;
import database.DAO.ClassDAO;
import database.DAO.SchoolDAO;
import database.DAO.StudentDAO;
import database.DAO.SubjectDAO;
import school.School;

public class PdfDisplay {

    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(PdfDisplay.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**
    static {
        //
        try {
            /*
             * // so logging is not shown in console LogManager.getLogManager().reset();
             */
            String a = "log/PdfDisplay.txt";
            File file = new File(a);
            // if file does not exist, create it
            if (!(file.exists())) {
                file.createNewFile();
            }
            fh = new FileHandler(a, 1024 * 1024, 1, true); // path, size, n of files, append or not
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

    // display one column of 'className'
    public void displayClass(List<ClassRoom> classes) {
        addInstitutionHeader("Classes");
        try {
            PdfPTable table = Table(1);
            table.setWidthPercentage(50);
            table.setSpacingAfter(15f);

            table.addCell(headCell("Class"));
            for (ClassRoom cls : classes) {
                table.addCell(styleCell(cls.getClassName()));
            }
            document.add(table);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error printing Classes on PDF: ", e);
        }
    }

    // display 2 columns of 'subjects'
    public void displaySubject(List<Subjects> subjects) {
        addInstitutionHeader("Subjects");
        try {
            PdfPTable table = Table(2);
            table.setWidthPercentage(90);
            table.setSpacingAfter(15f);
            table.setWidths(new float[] { 2, 1 }); // controls spacing

            table.addCell(headCell("Subject"));
            table.addCell(headCell("Class"));
            for (Subjects s : subjects) {
                table.addCell(styleCell(s.getSubjectName()));
                table.addCell(styleCell(s.getClassName()));
            }
            document.add(table);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error printing Subjects on PDF: ", e);
        }
    }

    public void displayTeacher(List<Teacher> teachers) {
        addInstitutionHeader("Teachers");
        try {
            PdfPTable table = Table(2);
            table.setWidthPercentage(90);
            table.setSpacingAfter(15f);
            table.setWidths(new float[] { 2, 1 }); // controls spacing

            table.addCell(headCell("Teacher"));
            table.addCell(headCell("Subject"));
            for (Teacher t : teachers) {
                table.addCell(styleCell(t.getName()));
                table.addCell(styleCell(t.getSubjectName()));
            }
            document.add(table);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error printing Teachers on PDF: ", e);
        }
    }

    public void displayStudent(List<Student> students) {
        addInstitutionHeader("Student");
        try {
            PdfPTable table = Table(2);
            table.setWidthPercentage(90);
            table.setSpacingAfter(15f);
            table.setWidths(new float[] { 2, 1 }); // controls spacing

            table.addCell(headCell("Students"));
            table.addCell(headCell("Classes"));
            for (Student t : students) {
                table.addCell(styleCell(t.getName()));
                table.addCell(styleCell(t.getClassName()));
            }
            document.add(table);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error printing Students on PDF: ", e);
        }
    }

    public String createPDF(String path) {
        try {
            document = new Document();
            writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            File file = new File(path);
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        } catch (DocumentException | IOException e) {
            logger.log(Level.WARNING, "Unable to make PDF: ", e);
        }
        return null;
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
        PdfPTable table = new PdfPTable(NofColumn); // n columns
        table.setWidthPercentage(70);

        // spacing before/after table
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        return table;
    }

    // --- School Name ---
    public void addInstitutionHeader(String title) {
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
            logger.log(Level.WARNING, "Error adding Institute Header: ", e);
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
    void studentInfoReport(String name, String className, int ID) {
        try {

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(90);
            table.setWidths(new float[] { 2, 1.5f }); // controls spacing
            // Name Row
            addStudentRow(table, "Name", name, "Class", className, BoldFont, normalFont);

            addStudentRow(table, "ID", String.valueOf(ID), "Year", "2025", BoldFont, normalFont);

            document.add(table);

            lineBreak();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error printing Student Info: ", e);
        }
    }

    // --- ReportCard Table Header ---
    void TableReport(List<Subjects> data) {
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

            for (Subjects s : data) {
                PdfPCell subject = new PdfPCell(new Phrase(s.getSubjectName(), normalFont));
                subject.setPadding(headSize);
                marksTable.addCell(subject);

                PdfPCell marks = new PdfPCell(new Phrase(String.valueOf(s.getMarks()), normalFont));
                marks.setPadding(headSize);
                marksTable.addCell(marks);

                PdfPCell Obtmarks = new PdfPCell(
                        new Phrase(String.valueOf(s.getObtmarks()), normalFont));
                Obtmarks.setPadding(headSize);
                marksTable.addCell(Obtmarks);

                PdfPCell percentage = new PdfPCell(
                        new Phrase(String.valueOf(s.getPercentage()), normalFont));
                percentage.setPadding(headSize);
                marksTable.addCell(percentage);

                PdfPCell grade = new PdfPCell(
                        new Phrase(String.valueOf(s.getGrade(s.getPercentage())), normalFont));
                grade.setPadding(headSize);
                marksTable.addCell(grade);

            }

            document.add(marksTable);

        } catch (

        Exception e) {
            e.printStackTrace();
        }
    }

    // --- Totals of Report---
    void ReportTotals(int totalmarks, int totalObtmarks, double percentage, char Grade) {
        try {
            // marks
            Phrase mark = new Phrase();
            mark.add(new Chunk("Total Marks: ", BoldFont));
            mark.add(new Chunk(String.valueOf(totalmarks) + " / ", normalFont));
            mark.add(new Chunk(String.valueOf(totalObtmarks) + " \n\n ", normalFont));

            // percentage
            Phrase perc = new Phrase();
            perc.add(new Chunk("Percentage: ", BoldFont));
            perc.add(new Chunk(String.valueOf(percentage) + "%\n\n", normalFont));

            // final Grade
            Phrase grade = new Phrase();
            grade.add(new Chunk("Grade: ", BoldFont));
            grade.add(new Chunk(Grade + "\n\n", normalFont));

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

            PdfPTable signTable = new PdfPTable(1);
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

    // handle the FULL creation of whole Student Report
    public void handleStudentReport(String StudentName) {
        // creating a file for studentReport
        try {
            createPDF("studentReport.pdf");
            setHeading("Student Report");
        } catch (Exception e) {
            logger.log(Level.WARNING, "(studentReport) Error making PDF & SETTING HEADING.");
            return; // stop if theres an error
        }
        try {
            StudentDAO student = new StudentDAO();
            if (!student.studentExists(StudentName)) {
                System.out.println("Student does not exist.");
                return;
            }

            List<Subjects> data = student.fetchStudentReport(new Student(StudentName));
            // fetching data from database

            addInstitutionHeader("STUDENT REPORT");

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

            lineBreak(); // line break
            sign(); // footer of signatories

            closeDoc(); // closing the doc
        } catch (Exception e) {
            System.err.println("Error making Student Report PDF.");
            e.printStackTrace();
        }
    }

    // create the StudentReport with validation
    void generateStudentReport() {
        StudentDAO student = new StudentDAO(); // student object
        String name = "Ali Khan"; // this is testing for dummy data
        if (!student.studentExists(name)) {
            System.out.println(name + " doesn't exist in Database.");
            return;
        }
        try {
            handleStudentReport(name);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error Generating Student Report.", e);
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
            PdfPTable table = Table(1);
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
            PdfPTable table = Table(2);
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

    // method to write student classes policy
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
            PdfPTable table = Table(2);
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

    // create Table - Testing Table
    public void createTeTable() {
        PdfPTable table = new PdfPTable(1); // 1 columns
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

    // FOR RECEIPT
    // ***********

    // creating info cell
    public PdfPCell createInfoCell(String placeholder, String val) {
        Phrase infoPhrase = new Phrase(); // creating a phrase

        Chunk label = new Chunk(placeholder + ": ",
                FontFactory.getFont(FontFactory.COURIER_BOLD, 10));

        Chunk name = new Chunk(val, FontFactory.getFont(FontFactory.HELVETICA, 10));

        infoPhrase.add(label);
        infoPhrase.add(name);

        PdfPCell info = new PdfPCell(infoPhrase);

        info.setBorder(Rectangle.NO_BORDER);

        return info;
    }

    // Rs - phrase
    public Phrase createPhrase(String label, int value) {
        // Combine label + value into one chunk
        Chunk labelChunk = new Chunk(label + ": ",
                FontFactory.getFont(FontFactory.COURIER_BOLD, 12));
        Chunk valueChunk = new Chunk(String.valueOf(value) + "Rs", new Font(Font.HELVETICA, 11));

        // Wrap inside phrase + paragraph
        Phrase phrase = new Phrase();
        phrase.add(labelChunk);
        phrase.add(valueChunk);

        return phrase;
    }

    public static void main(String[] args) {
        Document document = new Document();

        PdfDisplay test = new PdfDisplay();

        try {
            PdfWriter.getInstance(document, new FileOutputStream("Fee.pdf"));
            document.open();

            String imagePath = "img/logo.jpeg";

            // BASIC RECIEPT HEADER
            // *************

            // LEFT - IMG
            // *************

            PdfPTable table = new PdfPTable(2);
            table.setWidths(new float[] { 1, 4 }); // controls spacing

            Image image = Image.getInstance(imagePath);
            image.scaleAbsolute(70f, 70f);

            PdfPCell imgCell = new PdfPCell();
            imgCell.addElement(image);

            // remove borders
            imgCell.setBorder(Rectangle.NO_BORDER);

            // horizontal + vertical alignment
            imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            imgCell.setVerticalAlignment(Element.ALIGN_CENTER);

            table.addCell(imgCell);

            // RIGHT - INFO
            // *************

            PdfPCell Schoolname = new PdfPCell();
            Paragraph p = new Paragraph("AFAQ School",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 25));
            Paragraph loc = new Paragraph("Chawal Chowk, Bahria Section, Hussain Society, Lahore",
                    FontFactory.getFont(FontFactory.HELVETICA, 9));

            Paragraph n = new Paragraph("Payment Voucher",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));

            Schoolname.addElement(p);
            Schoolname.addElement(n);

            Schoolname.addElement(loc);

            // horizontal + vertical alignment
            Schoolname.setHorizontalAlignment(Element.ALIGN_BASELINE);
            Schoolname.setVerticalAlignment(Element.ALIGN_CENTER);
            Schoolname.setBorder(Rectangle.NO_BORDER);

            table.addCell(Schoolname);

            document.add(table);

            // BASIC INFO SECTION
            // *************
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.addCell(test.createInfoCell("Name", "Ali Khan"));
            infoTable.addCell(test.createInfoCell("ID", "101111"));
            infoTable.addCell(test.createInfoCell("Class", "9th"));
            infoTable.addCell(test.createInfoCell("Session", "Aug, 2025"));
            document.add(infoTable);

            Paragraph sline = new Paragraph(
                    "_____________________________________________________________________________");
            sline.setAlignment(Element.ALIGN_RIGHT);
            sline.setSpacingBefore(0f);
            sline.setSpacingAfter(0f);
            sline.setLeading(0f, 0.2f); // line spacing control document.add(Sline);
            Phrase remarks = new Phrase();
            Chunk label = new Chunk("Remarks: ",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            Chunk mark = new Chunk("MONTHLY FEE",
                    FontFactory.getFont(FontFactory.COURIER, 12, Font.UNDERLINE));
            Chunk right = new Chunk(new VerticalPositionMark());
            Chunk payLabel = new Chunk("Payments(¤)",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            remarks.add(Chunk.NEWLINE); // Adds a line break
            remarks.add(Chunk.NEWLINE); // Adds a line break
            remarks.add(label);
            remarks.add(mark);
            remarks.add(right);
            remarks.add(payLabel);

            document.add(remarks); // adding remarks section

            // PAYMENTS SECTION
            // ********************

            PdfPTable payments = new PdfPTable(1);
            payments.setWidthPercentage(100);

            List<String> labels = Arrays.asList("Tuition Fee", "Exam Fee", "Stationery");
            List<Integer> values = Arrays.asList(5000, 1000, 750);

            Paragraph line = new Paragraph("________________");
            line.setAlignment(Element.ALIGN_RIGHT);
            line.setSpacingBefore(0f);
            line.setSpacingAfter(0f);
            line.setLeading(0f, 0.2f); // line spacing control
            document.add(line);

            int totals = 0;
            for (int ii : values) {
                totals += ii;
            }

            for (int i = 0; i < labels.size(); i++) {
                String labeli = labels.get(i);
                Integer value = values.get(i);

                Paragraph para = new Paragraph(test.createPhrase(labeli, value));

                // Right align the paragraph
                para.setAlignment(Element.ALIGN_RIGHT);

                // Add paragraph
                document.add(para);

                document.add(line);

                // Add spacing between rows if needed
                para.setSpacingAfter(4f);

            }

            document.add(payments);

            Paragraph finals = new Paragraph(test.createPhrase("Total", totals));
            finals.setAlignment(Element.ALIGN_RIGHT);
            document.add(finals);

            // LISTING PROBLEM

            test.lineBreak();

            // Create a list (ordered or unordered)
            com.lowagie.text.List list = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
            // First item
            Phrase policy1 = new Phrase();
            policy1.add(new Chunk(
                    "Late payment amount will be charged after due date and can't be waived. The collection on your behalf will be used for need based scholarships.\n",
                    FontFactory.getFont(FontFactory.HELVETICA, 12)));
            list.add(new ListItem(policy1));

            Phrase policy2 = new Phrase();

            policy2.add(new Chunk(
                    "All Fees are non refundable and can be changed without prior notice.\n",
                    FontFactory.getFont(FontFactory.HELVETICA, 12)));

            list.add(new ListItem(policy2));

            Phrase policy3 = new Phrase();

            policy3.add(new Chunk(
                    "Withholding tax @ 5% leviable effective July 01, 2013 under section 2361 of the ITO, 2001 where annual fee exceeds Rs. 200,000/-",
                    FontFactory.getFont(FontFactory.HELVETICA, 12)));

            list.add(new ListItem(policy3));

            // Finally add the list to the document
            document.add(list);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the document
            document.close();
            closeLog(); // close static log
        }
    }

}
