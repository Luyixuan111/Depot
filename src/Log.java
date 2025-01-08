import java.io.FileWriter;
import java.io.IOException;

public class Log {
    // Using volatile to ensure visibility of changes across threads
    private static volatile Log instance;
    private StringBuilder logData;

    // Private constructor to prevent instantiation from other classes
    private Log() {
        logData = new StringBuilder();
    }

    // Double-checked locking for thread-safe singleton instance creation
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

    // Method to add events to the log
    public synchronized void addEvent(String event) {
        logData.append(System.currentTimeMillis()).append(" - ").append(event).append("\n");
    }

    // Method to write log data to a file
    public boolean writeToFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) { // Append mode is set to true
            writer.write(logData.toString());
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
            return false;
        }
    }
}
