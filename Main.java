import database.DBValidator;
import database.DBmaker;
import display.Input;
import display.MenuHandler;
import web.WebServer;

public class Main {

    public static void main(String[] args) {
        System.out.println("Select\n1. TUI\n2. Web Server");

        int choice = Input.validateMenuInput(2);

        // Exit if user enters 0
        if (choice == 0) {
            System.out.println("Exiting...");
            return;
        }

        boolean runWeb = choice != 1;

        if (runWeb) {
            System.out.println("Starting Web Server");
            WebServer.start();
            return;
        }

        System.out.println("================= INFO =================\n"
                + " At any point, enter [0] to go back or exit \n"
                + " the current menu/input.\n" + "========================================");


        // Check if DB exists, create if not
        if (!DBValidator.DBvalidate()) {
            System.out.println("No database found. Creating new one...");
            boolean success = DBmaker.createDB();
            if (!success) {
                System.err.println("Failed to create database");
            }
        }

        MenuHandler.runMainLoop();
    }

    // DON'T FORGET TO CLOSE DOCUMENT/FILE and other things you opened (if any)

    /*
     * BETTER (& Current) SOLUTION MADE A LAMBDA FUNCTION THAT executes lines to
     * close a logger as soon as the JVM closes the file.
     */

}