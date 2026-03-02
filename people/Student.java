package people;

import classroom.ClassRoom;
import classroom.Subjects;
import java.util.ArrayList;
import java.util.List;

public class Student extends Person {
    ClassRoom room;
    List<Subjects> subjects = new ArrayList<>();

    // Constructor for method listStudent()
    public Student(String name, ClassRoom room) {
        this.name = name;
        this.room = room;
    }

    // Student Basic Info
    public Student(String name, int id, ClassRoom room) {
        this.name = name;
        this.id = id;
        this.room = room;
    }

    public Student(String name) {
        this.name = name;
    }

    public Student() {

    }

    public String getClassName() {
        return room != null ? room.getName() : null;
    }

    // getter
    @Override
    public Integer getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public ClassRoom getClassRoom() {
        return room;
    }

    // setter
    public void setName(String name) {
        this.name = name;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public void setClassRoom(ClassRoom room) {
        this.room = room;
    }

    public void addSubject(Subjects subject) {
        subjects.add(subject);
    }

    public List<Subjects> getSubjects() {
        return subjects;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nID: " + id;
    }

}
