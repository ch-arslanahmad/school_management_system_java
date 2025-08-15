package display;

// PDF imports
import java.awt.Color; // for cell background color
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.*;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;

import classroom.ClassRoom;
import classroom.Subjects;
import database.Database;
import database.DAO.ClassDAO;
import database.DAO.SubjectDAO;

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
    public void closeDoc() {
        try {
            if (document != null) {
                document.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (Exception e) {
            System.out.println("Errors while Closing PDF writer or Doc: ");
            e.printStackTrace();
        }
    }

    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {
        testPDF pdf = new testPDF();

        try {
            pdf.subjectTable();
            pdf.closeDoc();
            Database.closeCon();
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("Logging Test Complete");
        closeLog();

    }
}
