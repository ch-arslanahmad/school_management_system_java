/*  current consideration, compile using the following commands (not simple run)

javac -cp ".:sqlite-jdbc-3.50.3.0.jar" editDB.java
java -cp ".:sqlite-jdbc-3.50.3.0.jar" editDB

*/
import java.sql.*;
import java.util.Scanner;

public class editDB {
    public static void main(String[] args) {
        // Step 1: Create a Student object
        Scanner input = new Scanner(System.in);
        Student student = new Student();  // Dummy values
        student.setDetails(input);

        // Step 2: Get values using getters
        String name = student.getName();  // getName from Student.java
        int age = student.getAge();       // getAge from Student.java
        int classId = 1;  // You can later set this dynamically or via a method

        // Step 3: Connect to SQLite database
        Connection conn = null;
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Connect to your database file
            conn = DriverManager.getConnection("jdbc:sqlite:people.db");

            // Step 4: Prepare SQL Insert Query
            String sql = "INSERT INTO Students (Name, Age, ClassID) VALUES (?, ?, ?)";

            // Step 5: Create a PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Step 6: Set values in the query (from student object)
            pstmt.setString(1, name);     // ? for student_name
            pstmt.setInt(2, age);         // ? for age
            pstmt.setInt(3, classId);     // ? for class_id

            // Step 7: Execute the query
            pstmt.executeUpdate();

            // Step 8: Confirmation message
            System.out.println("✅ Student added successfully!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            // Step 9: Close connection if opened
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("Closing error: " + ex.getMessage());
            }
        }
    }
}
