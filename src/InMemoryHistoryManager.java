import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> taskHistory;

    InMemoryHistoryManager() {
        this.taskHistory = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        Task taskCopy = task.copyTask();
        if (this.taskHistory.size() < 10) {
            this.taskHistory.add(taskCopy);
            return;
        }
        this.taskHistory.removeFirst();
        this.taskHistory.add(taskCopy);
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}


