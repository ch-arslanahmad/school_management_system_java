package display;

import java.util.Scanner;
import java.util.logging.*;

public class Input {

    private static final Logger logger = Logger.getLogger(Input.class.getName());
    private static final Scanner scanner = new Scanner(System.in);

    static {
        LogHandler.createLog(logger, "Input");
    }

    public static void close() {
        logger.info("Scanner closed.");
        scanner.close();
    }

    public static String getLowerStrInput() {
        String s = getStrInput();
        return s.toLowerCase();
    }

    public static String getNormalInput() {
        String input;
        do {
            input = getStrInput();
            if (input.isEmpty()) {
                logger.warning("Input is empty, Please type something.");
            }
        } while (input.isEmpty());
        return input;
    }

    public static String getStrInput() {
        return scanner.nextLine().trim();
    }

    public static int getIntInput() throws NumberFormatException {
        return Integer.parseInt(getStrInput());
    }

    public static int getNumInput() {
        while (true) {
            try {
                return getIntInput();
            } catch (NumberFormatException e) {
                System.out.println("Enter valid Integer value: ");
            }
        }
    }

    public static int validateMenuInput(int n) {
        int choice;
        while (true) {
            try {
                System.out.print("Enter your choice(0-" + n + "): ");
                choice = getIntInput();
                if (choice > -1 && choice <= n) {
                    return choice;
                } else {
                    System.out.print("Try again. Only, (0-" + n + ")\t");
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter valid Integer value: ");
            }
        }
    }
}