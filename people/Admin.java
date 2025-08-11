package people;

// import input
import java.util.Scanner;

// main class
public class Admin {

    // private members
    private String name = "admin";
    private int pin;

    // setting PIN method
    void setPin(Scanner input) {
        while (true) {
            System.out.print("Set Admin PIN: ");
            int a = input.nextInt();
            System.out.println("Write it again: ");
            int b = input.nextInt();
            if (a == b) {
                pin = a;
                System.out.println("Password Added");
            } else {
                System.out.println("Password is not same.");
                break;
            }
        }
    }

    // input PIN
    boolean getPin(Scanner input) {
        // loop 3 times
        for (int i = 0; i < 3; i++) {
            System.out.println("Enter PIN:");
            int a = input.nextInt();
            if (a == pin) {
                return true;
            } else {
                System.out.println("Incorrect Details: PIN.");
            }
        }
        return false;
    }

    // Authorization method
    boolean adminAuth(Scanner input) {
        System.out.print("Enter name: ");
        String a = input.nextLine();
        if (a.equals(name)) {
            if (getPin(input)) {
                return true;
            }
        } else {
            System.out.println("Incorrect Details: Name");
        }
        return false;
    }

}
