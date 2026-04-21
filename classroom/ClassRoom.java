package classroom;

public class ClassRoom {

    private Integer classID;
    private String className;
    private Integer tuition;
    private Integer stationary;
    private Integer paper;


    public ClassRoom() {
    }

    public ClassRoom(String className) {
        this.className = className;
    }

    public ClassRoom(Integer classID, String className) {
        this.classID = classID;
        this.className = className;
    }

    public ClassRoom(String className, Integer tuition, Integer stationary, Integer paper) {
        this.className = className;
        this.tuition = tuition;
        this.stationary = stationary;
        this.paper = paper;
    }

    public ClassRoom(Integer classID, String className, Integer tuition, Integer stationary, Integer paper) {
        this.classID = classID;
        this.className = className;
        this.tuition = tuition;
        this.stationary = stationary;
        this.paper = paper;
    }


    public boolean isEmpty() {
        return className == null;
    }

    public Integer getID() {
        return classID;
    }

    public String getName() {
        return className;
    }

    public Integer getTuitionFee() {
        return tuition;
    }

    public Integer getStationaryFee() {
        return stationary;
    }

    public Integer getPaperFee() {
        return paper;
    }

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
        return "Class: " + className + "\nID: " + classID + ".\nTuition Fee: " + tuition + "\nStationary Fee: "
                + stationary + "\nPaper Fee: " + paper;
    }
}
