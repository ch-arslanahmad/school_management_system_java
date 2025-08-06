import java.sql.*;
import java.util.Scanner;
class testing {


    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Student arslan = new Student();
        arslan.setDetails(input);

        String name = arslan.getName();
        int age = arslan.getAge();

        Connection conn = null;

        try {
            
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

}