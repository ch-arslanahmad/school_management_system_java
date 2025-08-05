package school_management_system_java.classroom;

import java.util.Scanner;
public interface ClassRoom { 
    
    // method to add classes with subjects
    void addClass(Scanner input);

    // method to collect subject of each class
    void addclassSubjects(Scanner input);

    //
    int addSubject(Scanner input);
}
