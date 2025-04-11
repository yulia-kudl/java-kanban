import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class Epic extends Task {
    private ArrayList<Integer> epicSubTasks;
    private LocalDateTime endTime;

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }


    public Epic(String name, TaskStatus status, String description) {
        super(name, status, description, null, null);
        this.epicSubTasks = new ArrayList<>();
        this.status = TaskStatus.NEW;
        this.type = TaskType.EPIC;
    }

    public ArrayList<Integer> getEpicSubTasks() {
        return epicSubTasks;
    }


    public void addSubTask(SubTask subTask) {
        epicSubTasks.add(subTask.getId());
    }

    public void deleteSubTask(SubTask subTask) {
        epicSubTasks.remove((Integer) subTask.getId());

    }

    public void updateEpic(Epic epic) {
        this.name = epic.name;
        this.description = epic.description;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status + '\'' +
                "epicSubTasks=" + epicSubTasks +
                ", duration=" + (duration == null ? null : duration.toString()) +
                ", startTime= " + (startTime == null ? null : startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))) +
                ", endTime= " + (endTime == null ? null : endTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))) +
                '}';
    }

    public void setEpicSubTasks(ArrayList<Integer> epicSubTasks) {
        this.epicSubTasks = epicSubTasks;
    }

    @Override
    public Epic copyTask() {
        Epic epicCopy = new Epic(this.name, this.status, this.description);
        epicCopy.status = this.status;
        epicCopy.id = this.id;
        epicCopy.epicSubTasks = new ArrayList<>(this.epicSubTasks);
        epicCopy.endTime = this.endTime;
        return epicCopy;
    }
}
