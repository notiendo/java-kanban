import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final LinkedHashMap<Integer, Task> history = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Task> eldest) {
            return size() > MAX_HISTORY_SIZE;
        }
    };

    @Override
    public void add(Task task) {
        if (task == null) return;

        history.remove(task.getId());
        history.put(task.getId(), task);
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history.values());
    }
}