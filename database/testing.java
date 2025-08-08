import java.sql.*;
import java.util.Scanner;
import java.util.logging.*;
import java.io.File;


class testing {

    private static final Logger logger = Logger.getLogger(testing.class.getName());
    private static FileHandler fh;
    
    // STATIC block for **LOGGING**
    static {
    // 
    try {
        // so logging is not shown in console
        LogManager.getLogManager().reset();
        String a = "testlog.txt";
        File file = new File(a);
            // if file does not exist, create it
            if(!(file.exists())) {
                file.createNewFile();
            }
        fh = new FileHandler(a, true);
        logger.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
        logger.setLevel(Level.FINE);

        // checking if file exists
        if(file.exists()) {
            logger.info("Log File is created!");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }


    Connection conn;
    // method to setupConnection
    boolean setupConnection(Scanner input) {
        boolean isConnected = false;
        conn = null;
        try {
            // Load JDBC Driver
            Class.forName("org.sqlite.JDBC");
            // Connecting the database file
            conn = DriverManager.getConnection("jdbc:sqlite:people.db");
            // run only if connection is not null
            if(conn != null) {
                isConnected =  true;
                logger.info("Connection Established");
                // insert class in SQL lite
                insertClass(conn, input);
                
            }
            // if connection not made
            else {
                logger.warning("Connection Problem");
            }

        } catch (Exception e) {
            System.out.print("Error establishing connection with database/sqlite: ");
            e.printStackTrace();

            logger.warning("Error establishing connection with database/sqlite");

        }
        finally {
            try {
               conn.close();             
            } catch (Exception e) {
                System.out.println("Connection not properly closed");
                e.printStackTrace();
            }
        }
        return isConnected;

    }


    // method to add student in the database;
    boolean insertClass(Connection conn, Scanner input) {
        Subjects classroom = new Subjects();
        classroom.addClass(input);
        boolean insertedClass = false;

        try {
            // writing query in a string variable
            String addClass = "INSERT INTO Class (ClassName) VALUES (?)";
            
            // creating a prepared statement: injects variables instead of a whole query
            PreparedStatement writesql = conn.prepareStatement(addClass);

            // set values in the query
            writesql.setString(1, classroom.getClassroom());

            // execute the query
            writesql.execute();
            insertedClass = true;

            if(insertedClass) {
                logger.info("Values Successfully Inserted.");
                System.out.println("Successfully entered");
                conn.close();
            }
            else {
                System.out.println("ERROR Values NOT added.");
                logger.warning("ERROR Values NOT added");
            }


        } catch (Exception e) {
            System.out.println("Error adding data: ");
            e.printStackTrace();
            logger.warning("Error adding data into databse");
        }
        return insertedClass;
    }

    // END logging
    void closeLog() {
        fh.close();
    }

    // MAIN METHOD
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        testing test = new testing();

        test.setupConnection(input);
        
        test.closeLog();

    }

}