package classroom;

import java.util.List;

import people.Student;

public class ClassRoom {

    private int classID;
    private String className;
    List<Subjects> subjects;
    Subjects subject;
    Student student;

    public ClassRoom(String className) {
        this.className = className;
    }

    public ClassRoom(String className, List<Subjects> subjects) {
        this.className = className;
        this.subjects = subjects;
    }

    // for listAll() method in ClassDAO
    public ClassRoom(String className, Subjects subject, Student student) {
        this.className = className;
        this.subject = subject;
        this.student = student;
    }

    // FEES OF CLASS

    int tuition;
    int stationary;
    int paper;

    public ClassRoom(int tuition, int stationary, int paper) {
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

    public String toString() {
        return className + " ID is " + classID + ".";
    }

}
