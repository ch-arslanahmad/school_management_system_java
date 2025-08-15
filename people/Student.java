package people;

public class Student extends Person {
    protected int classID;

    public Student(int id, String name, int classID) {
        this.name = name;
        this.id = id;
        this.classID = classID;
    }

    public Student() {

    }

    public int getClassID() {
        return classID;
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
        return "Name: " + name
                + "\nID: " + id
                + "\nClassID: " + classID;
    }

}
