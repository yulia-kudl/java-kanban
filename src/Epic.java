import java.util.HashMap;


public class Epic extends Task {
    private HashMap<Integer,SubTask> epicSubTasks;

    public Epic(String name, TaskStatus status, String description) {
        super(name, status, description);
        this.epicSubTasks = new HashMap<>();
        this.status = TaskStatus.NEW;
    }

    @Override
    public void setStatus(TaskStatus status) {
    }

    public HashMap<Integer, SubTask> getEpicSubTasks() {
        return epicSubTasks;
    }

    public void updateEpicStatus() {
        if (epicSubTasks.isEmpty()) {
            this.status = TaskStatus.NEW;
            return;
        }
        boolean newTask = false;
        boolean inProgress = false;
        boolean done = false;

        for (SubTask subTask : epicSubTasks.values()) {
            if (subTask.status == TaskStatus.DONE) {
                done = true;
            }
            if (subTask.status == TaskStatus.NEW) {
                newTask = true;
            }
            if (subTask.status == TaskStatus.IN_PROGRESS) {
                inProgress = true;
            }
        }
        if ((newTask) && (!inProgress) && (!done)) {
            this.status = TaskStatus.NEW;
            return;
        }
        if ((!newTask) && (!inProgress) && (done)) {
            this.status = TaskStatus.DONE;
            return;
        }
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void addSubTask(SubTask subTask) {
        epicSubTasks.put(subTask.getId(), subTask);
        updateEpicStatus();
    }

    public void deleteSubTask(SubTask subTask) {
        epicSubTasks.remove(subTask.getId());
        updateEpicStatus();
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
                '}';
    }

    public void setEpicSubTasks(HashMap<Integer, SubTask> epicSubTasks) {
        this.epicSubTasks = epicSubTasks;
    }

    @Override
    public Epic copyTask() {
        Epic epicCopy = new Epic(this.name,this.status, this.description);
        epicCopy.status = this.status;
        epicCopy.epicSubTasks = new HashMap<>(this.epicSubTasks);
        epicCopy.id = this.id;
        return epicCopy;
    }
}
