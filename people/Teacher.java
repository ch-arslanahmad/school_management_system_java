package people;

import classroom.Subjects;

public class Teacher extends Person {
    private Subjects subject;

    public Teacher(String name, Subjects subject) {
        this.name = name;
        this.subject = subject;
    }

    public Teacher(String name) {
        this.name = name;
    }

    public Teacher() {
    }

    public String getSubjectName() {
        return subject != null ? subject.getSubjectName() : null;
    }

    public Subjects getSubject() {
        return subject;
    }

    public void setSubject(Subjects subject) {
        this.subject = subject;
    }

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

    public void setName(String string) {
        this.name = string;
    }

    public void setID(int int1) {
        this.id = int1;
    }

}
