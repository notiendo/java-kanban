import java.util.List;

public interface TaskManager {
    // Оставляем только нужные методы
    List<Task> getAllTasks();
    void deleteAllTasks();
    Task getTaskById(int id);
    Task createTask(Task task);
    void updateTask(Task task);
    void deleteTaskById(int id);

    List<Epic> getAllEpics();
    void deleteAllEpics();
    Epic getEpicById(int id);  // Оставляем только getEpicById
    Epic createEpic(Epic epic);
    void updateEpic(Epic epic);
    void deleteEpicById(int id);

    List<Subtask> getAllSubtasks();
    void deleteAllSubtasks();
    Subtask getSubtaskById(int id);  // Оставляем только getSubtaskById
    Subtask createSubtask(Subtask subtask);
    void updateSubtask(Subtask subtask);
    void deleteSubtaskById(int id);

    List<Task> getHistory();
    List<Subtask> getSubtasksByEpicId(int epicId);

    // Удаляем эти методы:
    // void getTask();
    // void getSubtask();
    // void getEpic();
}