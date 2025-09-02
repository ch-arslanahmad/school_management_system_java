package school;

// IMPORT TIME
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class School {
    private int id;
    private String name;
    private String principal;
    private String location;

    public String getTime() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd,MMM, yyyy"));
    }

    // Constructors
    public School() {
    }

    public School(String name, String principal, String location) {
        this.name = name;
        this.principal = principal;
        this.location = location;
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

    public String getlocation() {
        return location;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }
}