import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;
    protected TaskType type;
    protected Duration duration;
    protected LocalDateTime startTime;


    public Task(String name, TaskStatus status, String description, Duration duration) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.type = TaskType.TASK;
        this.duration = duration;
        this.startTime = null;
    }

    public Task(String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime) {
        this(name, status, description, duration);
        this.startTime = startTime;
    }


    public Task copyTask() {
        Task taskCopy = new Task(this.name, this.status, this.description, this.duration, this.startTime);
        taskCopy.id = this.id;

        return taskCopy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration.toString() +
                ", startTime= " + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getEndTime() {
        if ((startTime == null) || (duration == null)) {
            return null;
        }
        return startTime.plus(duration);
    }
}
