package school_management_system_java.classroom;

import java.util.Scanner;
public class ClassRoom {

    private String classroom;
    private int n;
    
    // method to add classes
    public void addClass(Scanner input) {
        System.out.println("Class: " + n);
        n++;
        classroom = input.nextLine();
    }

    public String getClassroom() {
        return classroom;
    }


}
