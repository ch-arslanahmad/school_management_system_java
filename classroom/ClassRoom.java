package classroom;

import java.util.Scanner;

public class ClassRoom {

    private int classID;
    private String className;

    public ClassRoom(int classID, String className) {
        this.classID = classID;
        this.className = className;
    }

    public ClassRoom() {

    }

    public int getClassID() {
        return classID;
    }

    public String getClassName() {
        return className;
    }

    // method to get classes
    public String setClass(Scanner input) {
        System.out.println("Enter class name: ");
        className = input.nextLine();
        return className;
    }

    public String toString() {
        return className + " ID is " + classID + ".";
    }

}
