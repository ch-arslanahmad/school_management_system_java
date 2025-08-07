import java.sql.*;
import java.util.Scanner;
import java.util.logging.*;

    private static final Logger logger = Logger.getLogger(testing.class.getName());
    private static Filehandler fh;
    
    static {
    // 
    try {
        // so logging is not shown in console
        LogManager.getLogManager().reset();
        String a = "Authlog.txt";
        File file = new File(a);
        // if file does not exist, create it
        if(!(file.exists())) {
            file.createNewFile();
        }
        fh = new FileHandler(a, true);
        logger.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
    } catch (Exception e) {
        e.printStackTrace();
    }
    finally {
        logger.info("Log File is created!");
    }
    }


class testing {
    // method to add student in the database;
    boolean insertClass(Connection conn, Scanner input) {
        Subjects classroom = new Subjects();
        classroom.addClass(input);

        try {
            // writing query in a string variable
            String addClass = "INSERT INTO Class (ClassName) VALUES (?)";
            
            // creating a prepared statement: injects variables instead of a whole query
            PreparedStatement writesql = conn.prepareStatement(addClass);

            // set values in the query
            writesql.setString(1, classroom.getClassroom());

            // execute the query
            writesql.execute();
        } catch (Exception e) {
            System.out.println("Error adding data: ");
            e.printStackTrace();
        }
        return true;
    }
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        testing test = new testing();



        Connection conn = null;

        try {
            // Load JDBC Driver
            Class.forName("org.sqlite.JDBC");
            // Connecting the database file
            conn = DriverManager.getConnection("jdbc:sqlite:people.db");
            // run only if connection is not null
            if(!(conn == null)) {
                // insert class in SQL lite
                test.insertClass(conn, input);
            }
        } catch (Exception e) {
            System.out.print("Errors: ");
            e.printStackTrace();

        }

    }

}