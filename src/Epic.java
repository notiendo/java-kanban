import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }

    @Override
    public Duration getDuration() {
        if (subtaskIds.isEmpty()) {
            return Duration.ZERO;
        }

        long totalMinutes = subtaskIds.stream()
                .mapToLong(id -> {
                    Task subtask = getSubtaskById(id);
                    return subtask != null && subtask.getDuration() != null ?
                            subtask.getDuration().toMinutes() : 0;
                })
                .sum();

        return Duration.ofMinutes(totalMinutes);
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subtaskIds.isEmpty()) {
            return null;
        }

        return subtaskIds.stream()
                .map(this::getSubtaskById)
                .filter(subtask -> subtask != null && subtask.getStartTime() != null)
                .map(Task::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    private Task getSubtaskById(int id) {
        return null;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + endTime +
                '}';
    }
}