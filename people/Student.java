package people;

import classroom.ClassRoom;

public class Student extends Person {
    ClassRoom room;

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
        return room.getClassName();
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
