package people;

public class Teacher extends Person {

    public Teacher(int id, String name, int subjectID, int classID) {
        this.id = id;
        this.name = name;
        this.subjectID = subjectID;
        this.classID = classID;
    }

    public Teacher() {

    }

    public int getSubjectID() {
        return subjectID;
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
                + "\nClassID: " + classID
                + "\nSubjectID: " + subjectID;
    }

}
