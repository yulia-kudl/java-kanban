import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {


    private int idCounter;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subtasks;
    protected final TreeSet<Task> tasksSorted;
    public HistoryManager historyManager;


    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.tasksSorted = new TreeSet<>(Comparator.comparing(task -> task.startTime));
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
            return new ArrayList<>();
        }
        return subtasks.values().stream().toList();
    }

    @Override
    public List<Epic> getEpics() {
        if (epics.isEmpty()) {
            return new ArrayList<>();
        }
        return epics.values().stream().toList();
    }

    @Override
    public List<Task> getTasks() {
        if (tasks.isEmpty()) {
            return new ArrayList<>();
        }
        return tasks.values().stream().toList();
    }
    /*Методы для удаления всех типов задач */

    @Override
    public void deleteAllTasks() {
        for (int id : tasks.keySet()) {    //удаляем все таски из истории сначала
            historyManager.remove(id);
            tasksSorted.remove(this.tasks.get(id));
        }
        this.tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (int id : epics.keySet()) {  // удаляем все эпики и подзадачи из истории
            historyManager.remove(id);
        }
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
            tasksSorted.remove(this.subtasks.get(id));
        }
        this.epics.clear();
        this.subtasks.clear(); //если нет эпиков, то подзадачи удаляются
    }

    @Override
    public void deleteAllSubTasks() {
        for (int id : subtasks.keySet()) { //удаляем все сабтаски из истории
            historyManager.remove(id);
            tasksSorted.remove((this.subtasks.get(id)));
        }
        this.subtasks.clear();
        if (this.epics.isEmpty()) {
            return;
        }
        for (Epic epic : epics.values()) {
            epic.status = TaskStatus.NEW;
            epic.getEpicSubTasks().clear(); //во всех эпиках очищаем список ид  подзадач
            updateTimeForEpic(epic.getId());
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
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    /* Создание. Сам объект должен передаваться в качестве параметра */
    @Override
    public void createTask(Task newTask) {
        if ((newTask == null) || (newTask.duration == null)) {
            return;
        }
        if (newTask.startTime != null && checkIntersection(newTask)) {
            return; // пересечение
        }
        generateAndSetId(newTask);
        if (newTask.startTime != null) {
            tasksSorted.add(newTask);
        }
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
        if ((newSubTask == null) || (newSubTask.duration == null)) {
            return;
        }
        int epicId = newSubTask.getEpicId();
        if (!epics.containsKey(epicId)) {
            return;
        }
        if (newSubTask.startTime != null && checkIntersection(newSubTask)) {
            return; // пересечение
        }
        generateAndSetId(newSubTask);
        if (newSubTask.startTime != null) {
            tasksSorted.add(newSubTask);
        }
        subtasks.put(newSubTask.getId(), newSubTask);
        epics.get(epicId).addSubTask(newSubTask);
        updateEpicStatus(epicId);
        if (newSubTask.startTime != null) {
            updateTimeForEpic(epicId);
        }
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
        if (checkTaskBeforeUpdate(task, tasks.get(task.id))) {
            tasks.put(task.getId(), task);
        }
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
        if (checkTaskBeforeUpdate(subTask, subtasks.get(subTask.getId()))) {
            if (subTask.getEpicId() != subtasks.get(subTask.getId()).getEpicId()) {
                int epicOld = subtasks.get(subTask.getId()).getEpicId();
                //подзадача привязывается к другому эпику
                epics.get(epicOld).deleteSubTask(subTask);
                updateEpicStatus(epicOld);
                updateTimeForEpic(epicOld);
            }
            subtasks.put(subTask.getId(), subTask);
            updateEpicStatus(subTask.getEpicId());
            updateTimeForEpic(subTask.getEpicId());
        }
    }

    /*Удаление по идентификатору. */
    @Override
    public void deleteTask(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }
        tasksSorted.remove(tasks.get(id));
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        ArrayList<Integer> subTaskForEpic = epics.get(id).getEpicSubTasks();
        for (int key : subTaskForEpic) { //удаляем подзадачи эпика
            tasksSorted.remove(subtasks.get(key));
            subtasks.remove(key);
            historyManager.remove(key);
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        if (!subtasks.containsKey(id)) {
            return;
        }
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).deleteSubTask(subtasks.get(id));
        tasksSorted.remove(subtasks.get(id));
        subtasks.remove(id);
        historyManager.remove(id);
        updateEpicStatus(epicId);
        updateTimeForEpic(epicId);
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return this.tasksSorted.stream().toList();
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

    protected void updateTimeForEpic(int id) {
        if (epics.get(id).getEpicSubTasks().isEmpty()) {
            epics.get(id).startTime = null;
            epics.get(id).setEndTime(null);
            epics.get(id).duration = null;
            return;
        }

        LocalDateTime epicStartTime = subtasks.values().stream()
                .filter(subTask -> subTask.getEpicId() == id)
                .min(Comparator.comparing(subtask -> subtask.startTime))
                .get().startTime;
        LocalDateTime epicEndTime = subtasks.values().stream()
                .filter(subTask -> subTask.getEpicId() == id)
                .max(Comparator.comparing(Task::getEndTime))
                .get().getEndTime();
        Duration epicDuration = Duration.ofSeconds(subtasks.values().stream()
                .filter(subTask -> subTask.getEpicId() == id)
                .mapToLong(subTask -> subTask.duration.getSeconds())
                .sum());
        epics.get(id).startTime = epicStartTime;
        epics.get(id).setEndTime(epicEndTime);
        epics.get(id).duration = epicDuration;

    }

    private boolean checkIntersection(Task task) {
        return tasksSorted.stream()
                .anyMatch(sortedTask -> check2Periods(sortedTask, task));
    }

    private boolean check2Periods(Task task1, Task task2) {
        boolean startTimeBeforeStart = task1.startTime.isBefore(task2.startTime);
        boolean endTimeBeforeStart = task1.getEndTime().isBefore(task2.startTime);
        boolean startTimeIsBeforeEnd = task1.startTime.isBefore(task2.getEndTime());
// true - есть пересечение, false - нет
        return (startTimeBeforeStart && !endTimeBeforeStart) || (!startTimeBeforeStart && startTimeIsBeforeEnd);
    }

    private boolean checkTaskBeforeUpdate(Task newTask, Task oldTask) {
        if ((newTask.startTime == null) && (oldTask.startTime == null)) { // нет даты начала
            return true;
        }
        if (newTask.startTime == null) {   //удалили время начала у таски: удаляем из отсортированного спискаБ если есть
            tasksSorted.remove(oldTask);
            return true;
        }
        if ((newTask.startTime.isEqual(oldTask.startTime)) && (newTask.duration.equals(oldTask.duration))) {   //проверяем есть ли пересечения у новой таски
            return true; //время начала и продолжительность не поменялись
        }

        tasksSorted.remove(oldTask);

        if (checkIntersection(newTask)) {
            tasksSorted.add(oldTask);
            return false; // время и продолжительность пересекаются с другими задачами
        } else {
            tasksSorted.add(newTask); //все ок, заменили в отсортированном списка
        }
        return true;

    }

}

