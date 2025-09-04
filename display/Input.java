package display;

import java.util.Scanner;
import java.util.logging.*;

public class Input {

    private static final Logger logger = Logger.getLogger(Input.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**
    static {
        LogHandler.createLog(logger, "Input");
    }

    public static void closeLog() {
        logger.info("Logger Closed.");
        fh.flush();
        fh.close();
    }

    private final Scanner scanner = new Scanner(System.in);

    public String getLowerStrInput() {
        return scanner.nextLine().toLowerCase().trim();
    }

    public String getNormalLowerInput() {
        String input;
        do {
            input = getStrInput();
            if (input.isEmpty()) {
                logger.warning("Input is empty, Please type something.");
            }
        } while (input.isEmpty());
        return input;
    }

    // get String input
    public String getStrInput() {
        return scanner.nextLine().trim();
    }

    public String getNormalInput() {
        String input;
        do {
            input = getStrInput();
            if (input.isEmpty()) {
                logger.warning("Input is empty, Please type something.");
            }
        } while (input.isEmpty());
        return input;
    }

    // INTEGER input leaves a NEWLINE character which can make the next STRING input
    // take the newline as input. so i think better approach is get input in String
    // and PARSE IT INTO INTEGER

    public int getIntInput() throws NumberFormatException {
        int number = Integer.parseInt(getStrInput());
        return number; // return integer successfully
    }

    // VALIDATION of Show Input
    public int validateMenuInput(int n, Input input) {
        int choice;
        // loop to only stop when valid input given
        while (true) {
            try {
                System.out.print("Enter your choice(0-" + n + "): ");
                choice = input.getIntInput();
                if (choice > -1 && choice <= n) {
                    return choice; // ... returns choice
                } else {
                    System.out.print("Try again. Only, (0-" + n + ")\t");
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter valid Integer value: ");
                e.printStackTrace();
            }
        }
    }

    public void close() {
        scanner.close();
    }
}
