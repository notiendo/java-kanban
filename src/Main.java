public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание", Status.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic.getId());

        System.out.println("\nИстория просмотров:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.getTaskById(task1.getId());

        System.out.println("\nИстория после повторного просмотра:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}