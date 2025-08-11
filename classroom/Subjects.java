package classroom;

public class Subjects {

    int subjectID;
    String subjectName;
    int classID;

    public Subjects(
            int subjectID, String subjectName, int classID) {
        this.subjectID = subjectID;
        this.subjectName = subjectName;
        this.classID = classID;
    }

    public Subjects() {

    }

    public int getSubjectID() {
        return subjectID;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getClassID() {
        return classID;
    }

    public String toString() {
        return subjectName + " ID is " + subjectID + " and belongs to classID :" + classID;
    }

}
