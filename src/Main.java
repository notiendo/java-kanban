public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager();

        // Создаем задачи
        Task task1 = new Task("Задача 1", "описание первой задачи", Status.NEW);
        Task task2 = new Task("Задача 2", "описание второй задачи", Status.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Тестирую Эпис", "Его описание эпика");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Тестирую первый субтаск", "Описание первого субтаска", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Тестирую второй субтаск", "Описание второго субтаска", Status.NEW, epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Второй эпик", "Описани вторго эпика");
        taskManager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Третий субтаск в другой задаче", "Описание третьего субтаска", Status.NEW, epic2.getId());
        taskManager.createSubtask(subtask3);

        System.out.println("Все задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nВсе эпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nВсе подзадачи:");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        task1.setStatus(Status.DONE);
        taskManager.updateTask(task1);

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);

        System.out.println("\nПосле изменения статусов:");
        System.out.println("Все задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nВсе эпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nВсе подзадачи:");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic1.getId());

        System.out.println("\nПосле удаления:");
        System.out.println("Все задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\nВсе эпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nВсе подзадачи:");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }
    }
}