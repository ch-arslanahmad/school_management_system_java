package classroom;

public class Subjects {

    int subjectID;
    String subjectName;
    int classID;
    String className;

    public Subjects(String subjectName, String className) {
        this.subjectName = subjectName;
        this.className = className;
    }

    public Subjects() {

    }

    public int getSubjectID() {
        return subjectID;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getClassName() {
        return className;
    }

    public int getClassID() {
        return classID;
    }

    public String toString() {
        return subjectName + " ID is " + subjectID + " and belongs to classID :" + classID;
    }

}
