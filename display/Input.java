package display;

import java.io.File;
import java.util.Scanner;
import java.util.logging.*;

import database.DBmaker;

public class Input {

    private static final Logger logger = Logger.getLogger(Input.class.getName());
    private static FileHandler fh;

    // STATIC block for **LOGGING**

    static {
        //
        try {
            // so logging is not shown in console
            // LogManager.getLogManager().reset();
            String a = "log/Input.txt";
            File file = new File(a);
            // if file does not exist, create it
            if (!(file.exists())) {
                file.createNewFile();
            }
            fh = new FileHandler(a, 1024 * 1024, 1, true); // path, size, n of files, append or not
            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
            logger.setLevel(Level.FINE);

            // checking if file exists
            if (file.exists()) {
                logger.info("Log File is created!");
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                closeLog();
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void close() {
        scanner.close();
    }
}
