import java.io.FileWriter;
import java.io.IOException;

public class Log {
    private static volatile Log instance;
    private static final String LOG_FILE_PATH = "application.log"; // Log file in the current directory

    private Log() {
        // Ensure that the log file is ready to be written to when the application starts
        clearLogFile();
    }

    public static Log getInstance() {
        if (instance == null) {
            synchronized (Log.class) {
                if (instance == null) {
                    instance = new Log();
                }
            }
        }
        return instance;
    }

    public void addEvent(String event) {
        writeEventToFile(event);
    }

    // Write events directly to the file for real-time logging
    private void writeEventToFile(String event) {
        try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) { // Append mode is true
            writer.append(event).append("\n");
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    // Optionally clear the log file at the start of the application run
    private void clearLogFile() {
        try (FileWriter writer = new FileWriter(LOG_FILE_PATH)) {
            writer.write(""); // Clear the content
        } catch (IOException e) {
            System.err.println("Error clearing the log file: " + e.getMessage());
        }
    }
}
