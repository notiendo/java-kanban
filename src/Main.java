import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW,
                Duration.ofHours(2), LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS,
                Duration.ofHours(1), LocalDateTime.now().plusHours(3));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic.getId(),
                Duration.ofHours(1), LocalDateTime.now().plusHours(5));
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.DONE, epic.getId(),
                Duration.ofHours(2), LocalDateTime.now().plusHours(7));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        System.out.println("Приоритетные задачи:");
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println("- " + task.getName() + " в " + task.getStartTime());
        }

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic.getId());

        System.out.println("\nИстория просмотров:");
        for (Task task : taskManager.getHistory()) {
            System.out.println("- " + task.getName());
        }

        Task overlappingTask = new Task("Пересекающаяся задача", "Описание", Status.NEW,
                Duration.ofHours(2), LocalDateTime.now().plusHours(1));

        try {
            taskManager.createTask(overlappingTask);
            System.out.println("Задача создана успешно");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}