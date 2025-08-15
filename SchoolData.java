import java.util.List;

import classroom.ClassRoom;
import classroom.Subjects;
import people.Student;
import people.Teacher;

public class SchoolData {
    private List<ClassRoom> classes;
    private List<Student> students;
    private List<Teacher> teachers;
    private List<Subjects> subjects;

    public SchoolData(List<ClassRoom> classes, List<Student> students) {
        this.classes = classes;
        this.students = students;
    }

    public SchoolData() {

    }

    public List<ClassRoom> getClasses() {
        return classes;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Subjects> getSubjects() {
        return subjects;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

}
