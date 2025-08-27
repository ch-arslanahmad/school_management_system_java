package classroom;

public class Subjects {

    int subjectID;
    String subjectName;
    int totalGrade;
    ClassRoom classRoom; // className

    // Grades
    int marks;
    int Obtmarks;

    public Subjects(String subjectName, ClassRoom classRoom) {
        this.subjectName = subjectName;
        this.classRoom = classRoom;
    }

    //
    ClassRoom room;

    public Subjects(ClassRoom room, String subjectName, int marks) {
        this.room = room;
        this.subjectName = subjectName;
        this.marks = marks;
    }

    public void setObtmarks(int obtmarks) {
        this.Obtmarks = obtmarks;
    }

    // constructor for studentReport - row by row
    public Subjects(String subjectName, int marks, int Obtmarks) {
        this.subjectName = subjectName;
        this.marks = marks;
        this.Obtmarks = Obtmarks;
    }

    // name only
    public Subjects(String subjectName) {
        this.subjectName = subjectName;
    }

    // marks
    public Subjects(int marks, int Obtmarks) {
        this.marks = marks;
        this.Obtmarks = Obtmarks;
    }

    // individual - BASIC
    public int getMarks() {
        return marks;
    }

    public int getObtmarks() {
        return Obtmarks;
    }

    // percentage in this subject
    public double getPercentage() {
        return (Obtmarks * 100) / marks;
    }

    // generic percentage finder
    public double getPercentage(int marks, int Obtmarks) {
        return (Obtmarks * 100) / marks;
    }

    // grade in this subject
    public char getGrade() {
        double percentage = getPercentage();
        if (percentage >= 90) {
            return 'A';
        } else if (percentage >= 80) {
            return 'B';
        } else if (percentage >= 70) {
            return 'C';
        } else if (percentage >= 60) {
            return 'D';
        } else {
            return 'F';
        }
    }

    // generic grade finder
    public char getGrade(double percentage) {
        if (percentage >= 90) {
            return 'A';
        } else if (percentage >= 80) {
            return 'B';
        } else if (percentage >= 70) {
            return 'C';
        } else if (percentage >= 60) {
            return 'D';
        } else {
            return 'F';
        }
    }

    // Empty Constructors
    public Subjects() {

    }

    public String getClassName() {
        return classRoom.getClassName();
    }

    public int getSubjectID() {
        return subjectID;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String toString() {
        return subjectName + " ID is " + subjectID;
    }

}
