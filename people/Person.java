package people;

public abstract class Person {
    protected int id;
    protected String name;
    protected int age;
    protected int classID;
    protected int subjectID;
    protected int studentID;
    protected int teacherID;

    abstract int getID();

    abstract String getName();

    public abstract String toString();

}
