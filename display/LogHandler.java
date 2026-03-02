package display;

import java.io.File;
import java.util.logging.*;

public class LogHandler {

    public static void createLog(final Logger logger, String path) {
        FileHandler fh;
        try {
            /*
             * // so logging is not shown in console
             */
            LogManager.getLogManager().reset();
            String a = "storage/log/" + path + ".txt";
            File file = new File(a);
            // ensure parent directory exists
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            // Create the file if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
            }
            // Use a single, non-rotating FileHandler so only one log file is produced.
            // If rotation is desired in future, switch to the size+count constructor
            fh = new FileHandler(a, true);
            fh.setLevel(Level.ALL);

            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());

            // checking if file exists
            if (file.exists()) {
                logger.info("Log File is created!");
            }

            // moving CloseLog to simply this
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Log File is closed.");
                fh.flush();
                fh.close();
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
