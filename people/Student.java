package school_management_system_java.people;
import java.util.Scanner;
public class Student extends Person {

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    String getName() {
        return name;
    }
    
    @Override
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
