import java.util.Scanner;
public class Student {

    private String name;
    private int age;


    String getName() {
        return name;
    }
    
    int getAge() {
        return age;
    }


    void setDetails(Scanner input) {
        System.out.print("Enter Student name: ");
        name = input.nextLine();
        System.out.print("Enter Student Age: ");
        age = input.nextInt();
    }

    
}
