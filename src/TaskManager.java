import java.util.List;
import java.util.Optional;

public interface TaskManager {

    List<Task> getAllTasks();

    void deleteAllTasks();

    Optional<Task> getTaskById(int id);

    Task createTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int id);


    List<Epic> getAllEpics();

    void deleteAllEpics();


    Optional<Epic> getEpicById(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    List<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Optional<Subtask> getSubtaskById(int id);

    Subtask createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    List<Task> getHistory();

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getPrioritizedTasks();

    boolean hasTimeOverlap(Task task1, Task task2);

    boolean isTimeOverlappingWithExisting(Task task);
}