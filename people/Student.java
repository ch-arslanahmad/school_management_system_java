package people;

import java.util.List;

import classroom.ClassRoom;
import classroom.Subjects;

public class Student extends Person {
    ClassRoom classRoom;

    // Constructor for method listStudent()
    public Student(String name, ClassRoom classRoom) {
        this.name = name;
        this.classRoom = classRoom;
    }

    public Student(String name) {
        this.name = name;
    }

    public Student() {

    }

    public String getClassName() {
        return classRoom.getClassName();
    }

    // Override Methods
    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nID: " + id;
    }

}
