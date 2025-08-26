import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    private void save() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("id,type,name,status,description,epic");

            for (Task task : getAllTasks()) {
                lines.add(taskToString(task));
            }

            for (Epic epic : getAllEpics()) {
                lines.add(taskToString(epic));
            }

            for (Subtask subtask : getAllSubtasks()) {
                lines.add(taskToString(subtask));
            }

            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    private String taskToString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%d",
                    subtask.getId(),
                    subtask.getName(),
                    subtask.getStatus(),
                    subtask.getDescription(),
                    subtask.getEpicId());
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            return String.format("%d,EPIC,%s,%s,%s,",
                    epic.getId(),
                    epic.getName(),
                    epic.getStatus(),
                    epic.getDescription());
        } else {
            return String.format("%d,TASK,%s,%s,%s,",
                    task.getId(),
                    task.getName(),
                    task.getStatus(),
                    task.getDescription());
        }
    }

    private Task taskFromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case "SUBTASK":
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                Task task = manager.taskFromString(line);

                if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    manager.subtasks.put(subtask.getId(), subtask);

                    Epic epic = manager.epics.get(subtask.getEpicId());
                    if (epic != null) {
                        epic.addSubtaskId(subtask.getId());
                    }
                } else if (task instanceof Epic) {
                    Epic epic = (Epic) task;
                    manager.epics.put(epic.getId(), epic);
                } else {
                    manager.tasks.put(task.getId(), task);
                }

                if (task.getId() >= manager.nextId) {
                    manager.nextId = task.getId() + 1;
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }

        return manager;
    }

    public static void main(String[] args) {
        try {
            File tempFile = File.createTempFile("tasks", ".csv");

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

            Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
            Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
            manager.createTask(task1);
            manager.createTask(task2);

            Epic epic = new Epic("Эпик 1", "Описание эпика 1");
            manager.createEpic(epic);

            Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic.getId());
            Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.DONE, epic.getId());
            manager.createSubtask(subtask1);
            manager.createSubtask(subtask2);

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            System.out.println("Задачи после загрузки:");
            System.out.println("Обычные задачи: " + loadedManager.getAllTasks().size());
            System.out.println("Эпики: " + loadedManager.getAllEpics().size());
            System.out.println("Подзадачи: " + loadedManager.getAllSubtasks().size());

            tempFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}