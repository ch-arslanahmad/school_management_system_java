package classroom;

public class Subjects {

    private Integer subjectID;
    private String subjectName;
    private ClassRoom room;
    private int totalMarks = 100;
    private Integer Marks;
    private Double percentage;
    private String grade;

    public Subjects() {
    }

    public Subjects(ClassRoom room, String subjectName) {
        this.subjectName = subjectName;
        this.room = room;
    }

    public Subjects(ClassRoom room, String subjectName, Integer Marks) {
        this.room = room;
        this.subjectName = subjectName;
        this.Marks = Marks;
    }

    public Subjects(String subjectName) {
        this.subjectName = subjectName;
    }

    public Subjects(String subjectName, int Marks) {
        this.subjectName = subjectName;
        this.Marks = Marks;
        this.percentage = findPercentage(totalMarks, Marks);
        this.grade = findGrade(this.percentage);
    }

    public Subjects(String subjectName, int Marks, int obtainedMarks) {
        this.subjectName = subjectName;
        this.Marks = obtainedMarks;
        this.percentage = findPercentage(Marks, obtainedMarks);
        this.grade = findGrade(this.percentage);
    }

    public Subjects(int Marks) {
        this.Marks = Marks;
        this.percentage = findPercentage(totalMarks, Marks);
        this.grade = findGrade(this.percentage);
    }

    private Double findPercentage(int marks, int obtMarks) {
        if (marks == 0) return 0.0;
        return (obtMarks * 100.0) / marks;
    }

    public static String findGrade(double percentage) {
        if (percentage >= 90) return "A";
        else if (percentage >= 80) return "B";
        else if (percentage >= 70) return "C";
        else if (percentage >= 60) return "D";
        else return "F";
    }

    public void setObtMarks(int Marks) {
        this.Marks = Marks;
        this.percentage = findPercentage(totalMarks, Marks);
        this.grade = findGrade(this.percentage);
    }

    // getters
    public Integer getObtainedMarks() {
        return Marks;
    }

    public Integer getObtMarks() {
        return Marks;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public Double getPercentage() {
        return percentage;
    }

    public String getClassName() {
        return room != null ? room.getName() : null;
    }

    public Integer getID() {
        return subjectID;
    }

    public String getName() {
        return subjectName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public Integer getMarks() {
        return Marks;
    }

    public ClassRoom getClassRoom() {
        return room;
    }

    // setters
    public void setID(int subjectID) {
        this.subjectID = subjectID;
    }

    public void setName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setClassName(String className) {
        if (this.room == null) {
            this.room = new ClassRoom();
        }
        this.room.setName(className);
    }

    public void setClassRoom(ClassRoom room) {
        this.room = room;
    }

    public void setObtainedMarks(Integer Marks) {
        this.Marks = Marks;
        this.percentage = findPercentage(totalMarks, Marks);
        this.grade = findGrade(this.percentage);
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public void setMarks(Integer Marks) {
        this.Marks = Marks;
    }

    public String toString() {
        return subjectName + " ID is " + subjectID;
    }

}
