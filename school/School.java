package school;

public class School {
    private int id;
    private String name;
    private String principal;

    // Constructors
    public School() {
    }

    public School(String name, String principal) {
        this.name = name;
        this.principal = principal;
    }

    public School(String name) {
        this.name = name;
    }

    // Getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }
}