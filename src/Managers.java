import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        File file = new File("tasks.csv");
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}