package display;

// PDF imports
import java.awt.Color; // for cell background color
import java.io.FileOutputStream;
import java.io.IOException;
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
import school.School;

public class PdfDisplay {

    // variables for LOGGing
    private static final Logger logger = Logger.getLogger(PdfDisplay.class.getName());

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "PdfDisplay");
    }

    static final Font Header = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 25);

    // Fonts
    static final Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
    static final Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14,
            Color.DARK_GRAY);
    static final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.BLACK);
    static final Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
    static final Font BoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);

    // PDF document objects
    Paragraph heading;

    // display one column of 'classNames' Only - previously classTable
    public void displayClasses(List<ClassRoom> classes) {
        Document document = null;
        try {
            document = createPDF("Classes-ONLY.pdf");
            addInstitutionHeader("Classes", document);

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
        } finally {
            closeDoc(document);
        }
    }

    // display 2 columns of 'subjects'
    public void displaySubject(List<Subjects> subjects) {
        Document document = null;
        try {
            document = createPDF("Subjects.pdf");
            addInstitutionHeader("Subjects", document);

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
        } finally {
            closeDoc(document);
        }
    }

    public void displayTeacher(List<Teacher> teachers) {
        Document document = null;
        try {
            document = createPDF("Teachers.pdf");
            addInstitutionHeader("Teachers", document);
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

    // display student with classes
    public void displayStudent(List<Student> students) {
        Document document = null;
        try {
            document = createPDF("Students.pdf");
            addInstitutionHeader("Student", document);
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
        } finally {
            closeDoc(document);
        }
    }

    private Document createPDF(String path) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();
        return document;
    }

    // (PDF) - Heading Styles
    private void setHeading(String text, Document document) {
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
    private PdfPCell headCell(String text) {
        PdfPCell head = new PdfPCell(new Phrase(text, bold()));
        head.setPadding(8);
        head.setBorderWidth(0.01f);
        head.setBorderColor(new Color(128, 128, 128));
        return head;

    }

    // (cell) - basic style
    private PdfPCell styleCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(8f);
        cell.setBorderWidth(0.01f);
        cell.setBorderColor(new Color(128, 128, 128));
        return cell;
    }

    // create Table with n number of columns
    private PdfPTable Table(int NofColumn) {
        PdfPTable table = new PdfPTable(NofColumn); // n columns
        table.setWidthPercentage(70);

        // spacing before/after table
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        return table;
    }

    // --- School Name ---
    public void addInstitutionHeader(String title, Document document) {
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
    private void studentInfoReport(String name, String className, int ID, Document document) {
        try {
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(90);
            table.setWidths(new float[] { 2, 1.5f }); // controls spacing
            // Name Row
            addStudentRow(table, "Name", name, "Class", className, BoldFont, normalFont);

            addStudentRow(table, "ID", String.valueOf(ID), "Year", "2025", BoldFont, normalFont);

            document.add(table);

            lineBreak(document);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error printing Student Info: ", e);
        }
    }

    // --- ReportCard Table Header ---
    void tableReport(List<Subjects> data, Document document) {
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
    private void totalsTable(int totalmarks, int totalObtmarks, double percentage, char Grade, Document document) {
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
    private void sign(Document document) {
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
        Document document = null;
        try {

            // creating a file for studentReport
            document = createPDF("studentReport.pdf");
            setHeading("Student Report", document); // * heading

            StudentDAO student = new StudentDAO();
            if (!student.studentExists(StudentName)) {
                System.out.println("Student does not exist.");
                return;
            }

            List<Subjects> data = student.fetchStudentReport(new Student(StudentName));
            // fetching data from database

            addInstitutionHeader("STUDENT REPORT", document);

            studentInfoReport(StudentName, student.fetchStudentClass(StudentName),
                    student.fetchStudentID(StudentName), document); // Writes
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
            tableReport(data, document); // create report table

            totalsTable(totalMarks, ObtMarks, totalPercentage, finalGrade, document); // report totals

            lineBreak(document); // line break
            sign(document); // footer of signatories

        } catch (Exception e) {
            System.err.println("Error generating Student Report PDF.");
            e.printStackTrace();

        } finally { // close the doc
            closeDoc(document);
        }
    }

    // ! create Table - TESTING
    public void createTeTable() {

        Document document = null;

        // add that table
        try {

            document = createPDF("testing.pdf");
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

            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDoc(document);
        }
    }

    public void lineBreak(Document document) {
        try {
            document.add(new Paragraph(" "));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while adding LineBreak", e);
        }
    }

    // (PDF) close PDF writer & 'Doc'
    public void closeDoc(Document document) {
        if (document != null && document.isOpen()) {
            document.close();
            System.out.println("Doc closed...");
        }
    }

    // FOR RECEIPT
    // ***********

    // creating info cell
    private PdfPCell createInfoCell(String placeholder, String val) {
        Phrase infoPhrase = new Phrase(); // creating a phrase

        Chunk label = new Chunk(placeholder + ": ",
                FontFactory.getFont(FontFactory.COURIER_BOLD, 11));

        Chunk name = new Chunk(val, FontFactory.getFont(FontFactory.HELVETICA, 11));

        infoPhrase.add(label);
        infoPhrase.add(name);

        PdfPCell info = new PdfPCell(infoPhrase);

        info.setBorder(Rectangle.NO_BORDER);

        return info;
    }

    // Rs - phrase
    private Phrase createPhrase(String label, int value) {
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

    private com.lowagie.text.List printPolicies() {
        com.lowagie.text.List list = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
        // First item
        Phrase policy1 = new Phrase();
        policy1.add(new Chunk(
                "Late payment amount will be charged after due date and can't be waived. The collection on your behalf will be used for need based scholarships.\n",
                FontFactory.getFont(FontFactory.HELVETICA, 12)));
        list.add(new ListItem(policy1));

        Phrase policy2 = new Phrase();

        policy2.add(
                new Chunk("All Fees are non refundable and can be changed without prior notice.\n",
                        FontFactory.getFont(FontFactory.HELVETICA, 12)));

        list.add(new ListItem(policy2));

        Phrase policy3 = new Phrase();

        policy3.add(new Chunk(
                "Withholding tax @ 5% leviable effective July 01, 2013 under section 2361 of the ITO, 2001 where annual fee exceeds Rs. 200,000/-",
                FontFactory.getFont(FontFactory.HELVETICA, 12)));

        list.add(new ListItem(policy3));
        return list;
    }

    public void handleFeeReciept(String studentName) {
        StudentDAO student = new StudentDAO();
        if (!student.studentExists(studentName)) {
            System.out.println("Student does not exist.");
            return;
        }

        Student std = student.getStudentInfo(studentName); // getting all Student Info

        SchoolDAO school = new SchoolDAO();

        School info = school.getSchoolInfo(); // fetching all School Info

        Document document = null; // creating a doc for reciept

        try {
            document = createPDF(studentName + "_Fee.pdf");

            String imagePath = "img/logo.jpeg"; // logo img path

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
            Paragraph p = new Paragraph(info.getName(), Header);
            Paragraph loc = new Paragraph(info.getlocation(),
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
            infoTable.addCell(createInfoCell("Name", std.getName()));
            infoTable.addCell(createInfoCell("ID", String.valueOf(std.getID())));
            infoTable.addCell(createInfoCell("Class", std.getClassName()));
            infoTable.addCell(createInfoCell("Session", info.getTime()));
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

            ClassDAO fee = new ClassDAO();
            ClassRoom room = fee.getClassFees(std.getClassName());

            Paragraph line = new Paragraph("________________");
            line.setAlignment(Element.ALIGN_RIGHT);
            line.setSpacingBefore(0f);
            line.setSpacingAfter(0f);
            line.setLeading(0f, 0.2f); // line spacing control
            document.add(line);

            int tuition = room.getTuition();
            int exam = room.getPaper();
            int stationary = room.getStationary();
            int totals = tuition + exam + stationary;

            Paragraph fee1 = new Paragraph(createPhrase("Tuition Fee", tuition));
            Paragraph fee2 = new Paragraph(createPhrase("Stationary Fee", stationary));
            Paragraph fee3 = new Paragraph(createPhrase("Exam Fee", exam));

            // Right align the paragraph
            fee1.setAlignment(Element.ALIGN_RIGHT);
            fee2.setAlignment(Element.ALIGN_RIGHT);
            fee3.setAlignment(Element.ALIGN_RIGHT);

            // Add spacing between rows if needed
            fee1.setSpacingAfter(4f);
            fee2.setSpacingAfter(4f);
            fee3.setSpacingAfter(4f);

            // Add paragraph
            document.add(fee1);
            document.add(line);

            document.add(fee2);
            document.add(line);

            document.add(fee3);
            document.add(line);

            Paragraph finals = new Paragraph(createPhrase("Total", totals));
            finals.setAlignment(Element.ALIGN_RIGHT);
            document.add(finals);

            // LISTING PROBLEM

            lineBreak(document);

            // Create a list (ordered or unordered) method

            // Finally add the list to the document
            document.add(printPolicies());

        } catch (Exception e) {
            System.out.println("Error making Student Reciept: ");
            e.printStackTrace();
        } finally {
            closeDoc(document);
        }

    }

}
