import java.util.ArrayList;


public class Epic extends Task {
    private ArrayList<Integer> epicSubTasks;

    public Epic(String name, TaskStatus status, String description) {
        super(name, status, description);
        this.epicSubTasks = new ArrayList<>();
        this.status = TaskStatus.NEW;
        this.type = TaskType.EPIC;
    }

    public ArrayList<Integer> getEpicSubTasks() {
        return epicSubTasks;
    }



    public void addSubTask(SubTask subTask) {
        epicSubTasks.add(subTask.getId());
        //updateEpicStatus();
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
        return epicCopy;
    }
}
