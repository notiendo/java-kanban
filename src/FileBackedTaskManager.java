import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

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

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            if (!file.exists() || file.length() == 0) {
                return manager;
            }

            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                Task task = manager.taskFromString(line);

                if (task instanceof Subtask subtask) {
                    manager.subtasks.put(subtask.getId(), subtask);

                    Epic epic = manager.epics.get(subtask.getEpicId());
                    if (epic != null) {
                        epic.addSubtaskId(subtask.getId());
                    }
                } else if (task instanceof Epic epic) {
                    manager.epics.put(epic.getId(), epic);
                } else {
                    manager.tasks.put(task.getId(), task);
                }

                if (task.getId() >= manager.nextId) {
                    manager.nextId = task.getId() + 1;
                }

                if (task.getStartTime() != null) {
                    manager.prioritizedTasks.add(task);
                }
            }

            for (Epic epic : manager.epics.values()) {
                manager.updateEpicTime(epic);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }

        return manager;
    }

    private void save() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("id,type,name,status,description,epic,duration,startTime");

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
        String durationStr = task.getDuration() != null ?
                String.valueOf(task.getDuration().toMinutes()) : "";
        String startTimeStr = task.getStartTime() != null ?
                task.getStartTime().format(formatter) : "";

        if (task instanceof Subtask subtask) {
            return String.format("%d,SUBTASK,%s,%s,%s,%d,%s,%s",
                    subtask.getId(),
                    escapeCommas(subtask.getName()),
                    subtask.getStatus(),
                    escapeCommas(subtask.getDescription()),
                    subtask.getEpicId(),
                    durationStr,
                    startTimeStr);
        } else if (task instanceof Epic epic) {
            return String.format("%d,EPIC,%s,%s,%s,,%s,%s",
                    epic.getId(),
                    escapeCommas(epic.getName()),
                    epic.getStatus(),
                    escapeCommas(epic.getDescription()),
                    durationStr,
                    startTimeStr);
        } else {
            return String.format("%d,TASK,%s,%s,%s,,%s,%s",
                    task.getId(),
                    escapeCommas(task.getName()),
                    task.getStatus(),
                    escapeCommas(task.getDescription()),
                    durationStr,
                    startTimeStr);
        }
    }

    private Task taskFromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = unescapeCommas(parts[2]);
        Status status = Status.valueOf(parts[3]);
        String description = unescapeCommas(parts[4]);

        Duration duration = null;
        if (parts.length > 6 && !parts[6].isEmpty()) {
            duration = Duration.ofMinutes(Long.parseLong(parts[6]));
        }

        LocalDateTime startTime = null;
        if (parts.length > 7 && !parts[7].isEmpty()) {
            startTime = LocalDateTime.parse(parts[7], formatter);
        }

        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status, duration, startTime);
                task.setId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case "SUBTASK":
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(name, description, status, epicId, duration, startTime);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    private String escapeCommas(String str) {
        return str.replace(",", "\\,");
    }

    private String unescapeCommas(String str) {
        return str.replace("\\,", ",");
    }
}