package classroom;

import java.util.ArrayList;
import java.util.List;

import people.Student;

public class ClassRoom {

    private Integer classID;
    private String className;
    List<Subjects> subjects = new ArrayList<>();
    List<Student> students = new ArrayList<>();

    // FEES OF CLASS

    Integer tuition; // can be null, if not set.
    Integer stationary;
    Integer paper;

    public ClassRoom(String className) {
        this.className = className;
    }

    public ClassRoom(String className, Integer tuition, Integer stationary, Integer paper) {
        this.className = className;
        this.tuition = tuition;
        this.stationary = stationary;
        this.paper = paper;
    }

    public ClassRoom() {

    }

    // getters

    public Integer getID() {
        return classID;
    }

    public String getName() {
        return className;
    }

    // GET FEES

    public Integer getTuitionFee() {
        return tuition;
    }

    public Integer getStationaryFee() {
        return stationary;
    }

    public Integer getPaperFee() {
        return paper;
    }

    // Setters

    public void setName(String className) {
        this.className = className;
    }

    public void setID(Integer classID) {
        this.classID = classID;
    }

    public void setTuitionFee(Integer tuition) {
        this.tuition = tuition;
    }

    public void setStationaryFee(Integer stationary) {
        this.stationary = stationary;
    }

    public void setPaperFee(Integer paper) {
        this.paper = paper;
    }

    public String toString() {
        return "Class: " + className + "\nID: " + classID + "." + "\nTuition Fee: " + tuition + "\nStationary Fee: "
                + stationary + "\nPaper Fee: " + paper;
    }

}
