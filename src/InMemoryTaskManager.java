import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {


    private int idCounter;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subtasks;
    public HistoryManager historyManager;


    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    public int getIdCounter() {
        return idCounter;
    }

    protected void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    protected HashMap<Integer, Task> getTasksHashMap() {
        return this.tasks;
    }

    protected HashMap<Integer, SubTask> getSubTasksHashMap() {
        return this.subtasks;
    }

    protected HashMap<Integer, Epic> getEpicHashMap() {
        return this.epics;
    }

    /*Методы для получения списка всех типов задач */

    @Override
    public List<SubTask> getSubtasks() {
        if (subtasks.isEmpty()) {
            return null;
        }
        return subtasks.values().stream().toList();
    }

    @Override
    public List<Epic> getEpics() {
        if (epics.isEmpty()) {
            return null;
        }
        return epics.values().stream().toList();
    }

    @Override
    public List<Task> getTasks() {
        if (tasks.isEmpty()) {
            return null;
        }
        return tasks.values().stream().toList();
    }
    /*Методы для удаления всех типов задач */

    @Override
    public void deleteAllTasks() {
        this.tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        this.epics.clear();
        this.subtasks.clear(); //если нет эпиков, то подзадачи удаляются
    }

    @Override
    public void deleteAllSubTasks() {
        this.subtasks.clear();
        if (this.epics.isEmpty()) {
            return;
        }
        for (Epic epic : epics.values()) {
            epic.status = TaskStatus.NEW;
            epic.getEpicSubTasks().clear(); //во всех эпиках очищаем хэшмап из подзадач
        }
    }

    /* Получение по идентификатору для всех типов задач */
    @Override
    public Task getTaskById(int id) {
        if (tasks.isEmpty()) {
            return null;
        }
        if (!(tasks.containsKey(id))) {
            return null;
        }
        //addTaskToHistory(tasks.get(id));
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Task getSubTaskById(int id) {
        if (subtasks.isEmpty()) {
            return null;
        }
        if (!(subtasks.containsKey(id))) {
            return null;
        }
        //addTaskToHistory(subtasks.get(id));
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Task getEpicTaskById(int id) {
        if (epics.isEmpty()) {
            return null;
        }
        if (!(epics.containsKey(id))) {
            return null;
        }
        //addTaskToHistory(epics.get(id));
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    /* Создание. Сам объект должен передаваться в качестве параметра */
    @Override
    public void createTask(Task newTask) {
        if (newTask == null) {
            return;
        }
        generateAndSetId(newTask);
        tasks.put(newTask.getId(), newTask);
    }

    @Override
    public void createEpic(Epic newEpic) {
        if (newEpic == null) {
            return;
        }
        generateAndSetId(newEpic);
        newEpic.setEpicSubTasks(new ArrayList<>()); //всегда добавляем эпик без подзадач
        epics.put(newEpic.getId(), newEpic);
        updateEpicStatus(newEpic.getId());

    }

    @Override
    public void createSubTask(SubTask newSubTask) {
        if (newSubTask == null) {
            return;
        }
        int epicId = newSubTask.getEpicId();
        if (!epics.containsKey(epicId)) {
            return;
        }
        generateAndSetId(newSubTask);
        subtasks.put(newSubTask.getId(), newSubTask);
        epics.get(epicId).addSubTask(newSubTask);
        updateEpicStatus(epicId);
    }

    /* Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра. */
    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        if (!(tasks.containsKey(task.getId()))) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        if (!(epics.containsKey(epic.getId()))) {
            return;
        }
        epics.get(epic.getId()).updateEpic(epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask == null) {
            return;
        }
        if (!(subtasks.containsKey(subTask.getId()))) {
            return;
        }
        if (!epics.containsKey(subTask.getEpicId())) {
            return;
        }
        if (subTask.getEpicId() != subtasks.get(subTask.getId()).getEpicId()) {
            //подзадача привязывается к другому эпику
            epics.get(subTask.getEpicId()).deleteSubTask(subTask);
            updateEpicStatus(subTask.getEpicId());
        }
        subtasks.put(subTask.getId(), subTask);
        // epics.get(subTask.getEpicId()).addSubTask(subTask);
        updateEpicStatus(subTask.getEpicId());

    }

    /*Удаление по идентификатору. */
    @Override
    public void deleteTask(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        ArrayList<Integer> subTaskForEpic = epics.get(id).getEpicSubTasks();
        for (int key : subTaskForEpic) { //удаляем подзадачи эпика
            subtasks.remove(key);
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        if (!subtasks.containsKey(id)) {
            return;
        }
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).deleteSubTask(subtasks.get(id));
        subtasks.remove(id);
        updateEpicStatus(epicId);
    }

    /* Получение списка всех подзадач определённого эпика. */
    @Override
    public List<SubTask> getSubTasksByEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            return null;
        }
        List<SubTask> result = new ArrayList<>();
        for (int id : epics.get(epicId).getEpicSubTasks()) {
            result.add(subtasks.get(id));
        }
        return result;
    }

    @Override
    public List<Task> getHistoryForTaskManager() {
        return this.historyManager.getHistory();
    }

    private void generateAndSetId(Task task) {
        idCounter++;
        task.setId(idCounter);
    }

    private void updateEpicStatus(int id) {
        if (epics.get(id).getEpicSubTasks().isEmpty()) {
            epics.get(id).setStatus(TaskStatus.NEW);
            return;
        }
        boolean newTask = false;
        boolean inProgress = false;
        boolean done = false;

        for (int subtaskID : epics.get(id).getEpicSubTasks()) {
            if (subtasks.get(subtaskID).status == TaskStatus.DONE) {
                done = true;
            }
            if (subtasks.get(subtaskID).status == TaskStatus.NEW) {
                newTask = true;
            }
            if (subtasks.get(subtaskID).status == TaskStatus.IN_PROGRESS) {
                inProgress = true;
            }
        }
        if ((newTask) && (!inProgress) && (!done)) {
            epics.get(id).setStatus(TaskStatus.NEW);
            return;
        }
        if ((!newTask) && (!inProgress) && (done)) {
            epics.get(id).setStatus(TaskStatus.DONE);
            return;
        }
        epics.get(id).setStatus(TaskStatus.IN_PROGRESS);
    }

}

