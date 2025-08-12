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

    public String toString() {
        return className + " ID is " + classID + ".";
    }

}
