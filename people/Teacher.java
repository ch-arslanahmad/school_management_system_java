package people;

public class Teacher extends Person {
    protected int subjectID;

    public Teacher(int id, String name, int subjectID, int classID) {
        this.id = id;
        this.name = name;
        this.subjectID = subjectID;
    }

    public Teacher(String name) {

    }

    public Teacher(String name, int classID) {

    }

    public Teacher() {

    }

    public int getSubjectID() {
        return subjectID;
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
        return "Name: " + name + "\nID: " + id + "\nSubjectID: " + subjectID;
    }

}
