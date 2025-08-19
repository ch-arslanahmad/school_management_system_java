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

    // constructor for studentReport
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

    public int getMarks() {
        return marks;
    }

    public int getObtmarks() {
        return Obtmarks;
    }

    public double getPercentage() {
        return (Obtmarks * 100) / marks;
    }

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
