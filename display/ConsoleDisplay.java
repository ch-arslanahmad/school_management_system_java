package display;

// main class - implement of Display.java
public class ConsoleDisplay implements Display {

    @Override
    public void displayf(String value1, String value2) {
        System.out.printf("%-20s | %-20s\n", value1, value2);
    }

    @Override
    public void displayf(String value1, String value2, String value3) {
        System.out.printf("%-20s | %-20s | %-20s\n", value1, value2, value3);
    }

}
