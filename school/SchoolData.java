package school;

import java.util.ArrayList;
import java.util.List;

import classroom.ClassRoom;
import classroom.Subjects;
import people.Student;
import people.Teacher;

public class SchoolData {
    private List<ClassRoom> classes;
    private List<Student> students;
    private List<Teacher> teachers;

    Subjects subject;

    public SchoolData() {

    }

    public SchoolData(List<ClassRoom> classes, List<Student> students) {
        this.classes = classes;
        this.students = students;
    }

    /*
     * My Thinking is that: 1. There is one 'ClassRoom', which has one 'Student' 2.
     * While has multiple, 'Subjects', in which each subject aside from its normal
     * details, - It also holds marks, obtained marks. - then from that character
     * grade, percentages are calculated.
     * 
     * 3. The total Obtained marks & marks are in 'Grade' - where total percentages
     * & grade is calculated.
     * 
     * SO NOW:
     */

    ClassRoom room;
    Student student;
    List<Subjects> subjects = new ArrayList<>();

    // for Student-Report
    public SchoolData(List<Subjects> subjects) {
        this.subjects = subjects;
    }

    public SchoolData(Student student, List<Subjects> subjects) {
        this.student = student;
        this.subjects = subjects;
    }

    // returns a className in School Data
    public String getClassName() {
        return room.getClassName();
    }

    // returns a studentName in School Data
    public String getStudentName() {
        return student.getName();
    }

    public int getMarks() {
        return subject.getMarks();
    }

    public int getObtmarks() {
        return subject.getObtmarks();
    }

    public double getPercentage(int totalMarks, int ObtMarks) {
        return subject.getPercentage(totalMarks, ObtMarks);
    }

    // returns grades
    public char getGrade(double percentage) {
        return subject.getGrade(percentage);
    }

    // SOLVED the listing problem, they were returning initilized empty lists of
    // objects even though the intent was to return a list of variables of the
    // object. It is solved via for-each loop.

    // returns a list of classNames in School Data
    public List<ClassRoom> getClasses() {
        return classes;
    }

    public List<Student> getStudents() {
        return students;
    }

    // returns a list of subjects objects (name, marks, obtMarks)
    public List<Subjects> getSubjects() {
        return subjects;
    }

    public List<Teacher> getTeachersName() {
        return teachers;
    }

}
