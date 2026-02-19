package classroom;

import java.util.ArrayList;
import java.util.List;

import people.Student;

public class ClassRoom {

    private int classID;
    private String className;
    List<Subjects> subjects = new ArrayList<>();
    List<Student> students = new ArrayList<>();

    // FEES OF CLASS

    int tuition;
    int stationary;
    int paper;

    public ClassRoom(String className) {
        this.className = className;
    }

    public ClassRoom(String className, int tuition, int stationary, int paper) {
        this.className = className;
        this.tuition = tuition;
        this.stationary = stationary;
        this.paper = paper;
    }

    public ClassRoom() {

    }

    public int getClassID() {
        return classID;
    }

    public String getClassName() {
        return className;
    }

    // GET FEES

    public int getTuition() {
        return tuition;
    }

    public int getStationary() {
        return stationary;
    }

    public int getPaper() {
        return paper;
    }

    // Setters

    public void setClassName(String className) {
        this.className = className;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public void setTuition(int tuition) {
        this.tuition = tuition;
    }

    public void setStationary(int stationary) {
        this.stationary = stationary;
    }

    public void setPaper(int paper) {
        this.paper = paper;
    }

    public String toString() {
        return className + " ID is " + classID + ".";
    }

}
