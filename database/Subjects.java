import java.util.Scanner;
import java.util.ArrayList;

public class Subjects{

    protected String classroom;
    ArrayList <String> subjects = new ArrayList<>();
    int totalSubjects;

    public void addClass(Scanner input) {
        System.out.print("ClassName: ");
        classroom = input.nextLine();
        // addclassSubjects(input); 
    }

    public String getClassroom() {
        return classroom;
    }

    public int addSubject(Scanner input) {
        System.out.print("Enter class: ");
        String a = input.nextLine();
        if(a == classroom) {
            System.out.print("Enter Subject: ");
            subjects.add(input.nextLine());
            totalSubjects++;
        }
        return totalSubjects;
    }

    public void addclassSubjects(Scanner input) {
        for(int i = 0; i <totalSubjects; i++) {
            System.out.print("Enter Subject name: ");
            subjects.add(input.nextLine());
        }
    }

}
