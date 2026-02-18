package school;

import java.util.List;

import classroom.ClassRoom;
import classroom.Subjects;
import people.Student;
import people.Teacher;

public class SchoolData {
    private List<ClassRoom> classes;
    private List<Subjects> subjects;
    private List<Student> students;
    private List<Teacher> teachers;

    Subjects subject;

    public SchoolData() {

    }

    /*
     * public SchoolData(List<ClassRoom> classes, List<Student> students) {
     * this.classes = classes; this.students = students; }
     */

    ClassRoom room;
    Student student;

    // studentsClass() method
    public SchoolData(ClassRoom room, Student student) {
        this.room = room;
        this.student = student;
    }

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

    public int getObtMarks() {
        return subject.getObtMarks();
    }

    public double getPercentage() {
        return subject.getPercentage();
    }

    // returns grades
    public String getGrade(double percentage) {
        return Subjects.findGrade(percentage);
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
