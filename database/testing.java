import java.sql.*;
import java.util.Scanner;
class testing {
    // method to add student in the database;
    boolean insertClass(Connection conn, Scanner input) {
        Subjects classroom = new Subjects();
        classroom.addClass(input);

        try {
            String addClass = "INSERT INTO Class (SubjectName) VALUES (?)";
            PreparedStatement writesql = conn.prepareStatement(addClass);

            //
            writesql.setString(3, classroom.getClassroom());
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