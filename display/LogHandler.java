package display;

import java.io.File;
import java.util.logging.*;

public class LogHandler {

    public static void createLog(final Logger logger, String path) {
        FileHandler fh;
        try {
            /*
             * // so logging is not shown in console LogManager.getLogManager().reset();
             */
            String a = "log/" + path + ".txt";
            File file = new File(a);
            // if file does not exist, create it
            if (!(file.exists())) {
                file.createNewFile();
            }
            fh = new FileHandler(a, 1024 * 1024, 1, true); // path, size, n of files, append or not
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