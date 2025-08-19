package school;

import java.util.ArrayList;
import java.util.List;

import classroom.ClassRoom;
import classroom.Grade;
import classroom.Subjects;
import people.Student;
import people.Teacher;

public class SchoolData {
    private List<ClassRoom> classes;
    private List<Student> students;
    private List<Teacher> teachers;

    public SchoolData(List<Subjects> subjects) {
        this.subjects = subjects;
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
    List<Subjects> subjects;
    Grade grade; // for totals & for each subject

    // for Student-Report
    public SchoolData(ClassRoom room, Student student, List<Subjects> subjects, Grade grade) {
        this.room = room;
        this.student = student;
        this.subjects = subjects;
        this.grade = grade;
    }

    // returns a className in School Data
    public String getClassName() {
        return room.getClassName();
    }

    // returns a studentName in School Data
    public String getStudentName() {
        return student.getName();
    }

    // total percentage
    public double getFullPercentage() {
        List<Double> percentages = getSubjectPercentage();
        double percentage = 0;
        for (Double p : percentages) {
            percentage += p;
        }
        return percentage;
    }

    // percentage by subject
    public List<Double> getSubjectPercentage() {
        List<Double> percentages = new ArrayList<>();

        for (Subjects p : subjects) {
            percentages.add(p.getPercentage());
        }

        return percentages;
    }

    public char getFullGrade() {
        Subjects g = new Subjects();
        return g.getGrade(getFullPercentage());
    }

    // get Subject grade
    public List<Character> getSubjectGrade() {
        List<Character> grades = new ArrayList<>();

        for (Subjects g : subjects) {
            grades.add(g.getGrade(getFullPercentage()));
        }
        return grades;

    }

    // returns a studentGrade in School Data
    public List<ClassRoom> getClasses() {
        return classes;
    }

    public List<Student> getStudents() {
        return students;
    }

    // returns a list of subjects
    public List<Subjects> getSubjects() {
        return subjects;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

}
