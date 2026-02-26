package classroom;

public class Subjects {

    private Integer subjectID;
    private String subjectName;
    private Integer classID;
    private String className;
    private int totalMarks = 100;
    private Integer obtainedMarks;
    private Double percentage;
    private String grade;

    public Subjects() {
    }

    public Subjects(Integer classID, String subjectName) {
        this.classID = classID;
        this.subjectName = subjectName;
    }

    public Subjects(Integer classID, String subjectName, Integer obtainedMarks) {
        this.classID = classID;
        this.subjectName = subjectName;
        this.obtainedMarks = obtainedMarks;
    }

    public Subjects(String subjectName) {
        this.subjectName = subjectName;
    }

    public Subjects(String subjectName, int obtainedMarks) {
        this.subjectName = subjectName;
        this.obtainedMarks = obtainedMarks;
        this.percentage = findPercentage(totalMarks, obtainedMarks);
        this.grade = findGrade(this.percentage);
    }

    public Subjects(String subjectName, int totalMarks, int obtainedMarks) {
        this.subjectName = subjectName;
        this.totalMarks = totalMarks;
        this.obtainedMarks = obtainedMarks;
        this.percentage = findPercentage(totalMarks, obtainedMarks);
        this.grade = findGrade(this.percentage);
    }

    public Subjects(int obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
        this.percentage = findPercentage(totalMarks, obtainedMarks);
        this.grade = findGrade(this.percentage);
    }

    private Double findPercentage(int marks, int obtMarks) {
        if (marks == 0)
            return 0.0;
        return (obtMarks * 100.0) / marks;
    }

    public static String findGrade(double percentage) {
        if (percentage >= 90)
            return "A";
        else if (percentage >= 80)
            return "B";
        else if (percentage >= 70)
            return "C";
        else if (percentage >= 60)
            return "D";
        else
            return "F";
    }

    public void setObtMarks(int obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
        this.percentage = findPercentage(totalMarks, obtainedMarks);
        this.grade = findGrade(this.percentage);
    }

    // getters

    public Integer getObtMarks() {
        return obtainedMarks;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public Double getPercentage() {
        return percentage;
    }

    public String getGrade() {
        return grade;
    }

    public Integer getID() {
        return subjectID;
    }

    public String getName() {
        return subjectName;
    }

    public Integer getClassID() {
        return classID;
    }

    public String getClassName() {
        return className;
    }

    public ClassRoom getClassRoom() {
        if (classID == null)
            return null;
        ClassRoom room = new ClassRoom();
        room.setID(classID);
        return room;
    }

    // setters

    public void setID(int subjectID) {
        this.subjectID = subjectID;
    }

    public void setName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setClassID(Integer classID) {
        this.classID = classID;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setClassRoom(ClassRoom room) {
        this.classID = room != null ? room.getID() : null;
    }

    public void setObtMarks(Integer obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
        this.percentage = findPercentage(totalMarks, obtainedMarks);
        this.grade = findGrade(this.percentage);
    }


    public String toString() {
        return subjectName + " ID is " + subjectID;
    }
}
