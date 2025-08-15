package people;

public abstract class Person {
    protected int id;
    protected String name;

    abstract int getID();

    abstract String getName();

    public abstract String toString();

}
