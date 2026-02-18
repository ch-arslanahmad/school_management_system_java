package classroom;

public class Subjects {

    int subjectID;
    String subjectName;
    int totalGrade;
    ClassRoom classRoom; // className

    // Grades
    int marks = 100; // default total marks for each subject
    int obtMarks;
    double percentage;
    String grade;

    public Subjects(String subjectName, ClassRoom classRoom) {
        this.subjectName = subjectName;
        this.classRoom = classRoom;
    }

    // for method listClassSubjectswithMarks()
    public Subjects(String subjectName, int obtMarks, ClassRoom classRoom) {
        this.subjectName = subjectName;
        setObtMarks(obtMarks); // sets obtMarks, percentage and grade
        this.classRoom = classRoom;
    }

    ClassRoom room;

    public Subjects(ClassRoom room, String subjectName, int marks) {
        this.room = room;
        this.subjectName = subjectName;
        this.marks = marks;
    }

    public void setObtMarks(int obtMarks) {
        this.obtMarks = obtMarks;
        this.percentage = findPercentage(this.marks, this.obtMarks);
        this.grade = findGrade(this.percentage);
    }

    // constructor for studentReport - row by row
    public Subjects(String subjectName, int marks, int obtMarks) {
        this.subjectName = subjectName;
        this.marks = marks;
        this.obtMarks = obtMarks;
        this.percentage = findPercentage(marks, obtMarks);
        this.grade = findGrade(this.percentage);
    }

    // name only
    public Subjects(String subjectName) {
        this.subjectName = subjectName;
    }

    // marks
    public Subjects(int marks, int obtMarks) {
        this.marks = marks;
        this.obtMarks = obtMarks;
    }

    // individual - BASIC
    public int getMarks() {
        return marks;
    }

    public int getObtMarks() {
        return obtMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    // generic percentage finder
    private double findPercentage(int marks, int obtMarks) {
        return (obtMarks * 100.0) / marks;
    }

    // generic grade finder
    public static String findGrade(double percentage) {
        if (percentage >= 90) {
            return "A";
        } else if (percentage >= 80) {
            return "B";
        } else if (percentage >= 70) {
            return "C";
        } else if (percentage >= 60) {
            return "D";
        } else {
            return "F";
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
