package school_management_system_java.classroom;

import java.util.Scanner;
import java.util.ArrayList;

public class Subjects implements ClassRoom{

    ArrayList <String> subjects = new ArrayList<>();
    int totalSubjects;


    @Override
    public int addSubject(Scanner input) {
        System.out.println("Enter class#");
        int a = input.nextInt();
        if(a == classroom) {
            System.out.println("Enter Subject: ");
            subjects.add(input.nextLine());
            totalSubjects++;
        }
        return totalSubjects;
    }

    @Override
    public void addclassSubjects(Scanner input) {
        for(int i = 0; i <totalSubjects; i++) {
            System.out.println("Enter Subject name: ");
            subjects.add(input.nextLine());
        }
    }

}
