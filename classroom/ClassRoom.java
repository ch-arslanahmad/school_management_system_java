package classroom;

import java.util.List;

public class ClassRoom {

    private int classID;
    private String className;
    List<Subjects> subjects;

    public ClassRoom(String className) {
        this.className = className;
    }

    public ClassRoom(String className, List<Subjects> subjects) {
        this.className = className;
        this.subjects = subjects;
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
