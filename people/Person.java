package people;

public abstract class Person {
    protected Integer id;
    protected String name;

    abstract Integer getID();

    abstract String getName();

    abstract void setID(Integer id);

    abstract void setName(String name);

    public abstract String toString();

}
