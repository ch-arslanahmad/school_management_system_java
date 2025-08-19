package people;

import classroom.Subjects;

public class Teacher extends Person {
    Subjects subject;

    public Teacher(String name, Subjects subject) {
        this.name = name;
        this.subject = subject;
    }

    public Teacher(String name) {
        this.name = name;
    }

    public Teacher() {

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
