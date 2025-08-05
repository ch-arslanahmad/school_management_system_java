package school_management_system_java.people;

import java.util.Scanner;

public class Teacher extends Person{


    @Override
    String getName() {
        return name;
    }

    @Override
    int getAge() {
        return age;
    }

    void setDetails(Scanner input) {
        System.out.print("Enter Teacher name: ");
        name = input.nextLine();
        System.out.print("Enter Teacher Age: ");
        age = input.nextInt();
    }
    
}
