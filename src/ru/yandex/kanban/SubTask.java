package ru.yandex.kanban;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, TaskStatus status, String description, int epicId, Duration duration) {
        super(name, status, description, duration);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public SubTask(String name, TaskStatus status, String description, int epicId, Duration duration,
                   LocalDateTime startDate) {
        super(name, status, description, duration, startDate);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public SubTask copyTask() {
        SubTask taskCopy = new SubTask(this.name, this.status, this.description, this.getEpicId(), this.duration, this.startTime);
        taskCopy.id = this.id;
        return taskCopy;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration.toString() +
                ", startTime= " + (startTime == null ? null : startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))) +
                '}';
    }
}
