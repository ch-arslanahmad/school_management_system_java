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
        return room.getName();
    }

    // getter
    @Override
    public int getID() {
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

    public void setID(int id) {
        this.id = id;
    }

    public void setClassRoom(ClassRoom room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nID: " + id;
    }

}
