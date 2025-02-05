import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int idCounter;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subtasks;


    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    /*Методы для получения списка всех типов задач */

    public List<SubTask> getSubtasks() {
        if (subtasks.isEmpty()){
            return null;
        }
        return subtasks.values().stream().toList();
    }

    public List<Epic> getEpics() {
        if (epics.isEmpty()){
            return null;
        }
        return epics.values().stream().toList();
    }

    public List<Task> getTasks() {
        if (subtasks.isEmpty()){
            return null;
        }
        return tasks.values().stream().toList();
    }
    /*Методы для удаления всех типов задач */

    public  void deleteAllTasks() {
        this.tasks.clear();
    }

    public void deleteAllEpics() {
        this.epics.clear();
        this.subtasks.clear(); //если нет эпиков, то подзадачи удаляются
    }

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
    public Task getTaskById(int id) {
        if (tasks.isEmpty()) {
            return null;
        }
        if (!(tasks.containsKey(id))) {
            return null;
        }
        return tasks.get(id);
    }

    public Task getSubTaskById(int id) {
        if (subtasks.isEmpty()) {
            return null;
        }
        if (!(subtasks.containsKey(id))) {
            return null;
        }
        return subtasks.get(id);
    }

    public Task getEpicTaskById(int id) {
        if (epics.isEmpty()) {
            return null;
        }
        if (!(epics.containsKey(id))) {
            return null;
        }
        return epics.get(id);
    }

    /* Создание. Сам объект должен передаваться в качестве параметра */
    public void createTask(Task newTask) {
        if ( newTask == null ) {
            return;
        }
        idCounter++;
        newTask.setId(idCounter);
        tasks.put(newTask.getId(), newTask);
    }

    public void createEpic(Epic newEpic) {
        if ( newEpic == null ) {
            return;
        }
        idCounter++;
        newEpic.setId(idCounter);
        newEpic.setEpicSubTasks(new HashMap<>()); //всегда добавляем эпик без подзадач
        newEpic.updateEpicStatus();
        epics.put(newEpic.getId(), newEpic);
    }

    public void createSubTask(SubTask newSubTask) {
        if (newSubTask == null) {
            return;
        }
        int epicId = newSubTask.getEpicId();
        if (!epics.containsKey(epicId)) {
            return;
        }
        idCounter++;
        newSubTask.setId(idCounter);
        subtasks.put(newSubTask.getId(), newSubTask);
        epics.get(epicId).addSubTask(newSubTask);
    }

    /* Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра. */
    public void updateTask(Task task) {
        if ( task == null) {
            return;
        }
        if (!(tasks.containsKey(task.getId()))) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        if ( epic == null) {
            return;
        }
        if (!(epics.containsKey(epic.getId()))) {
            return;
        }
        epics.get(epic.getId()).updateEpic(epic);
    }

    public void updateSubTask(SubTask subTask) {
        if ( subTask == null) {
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
        }
        subtasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).addSubTask(subTask);
    }

    /*Удаление по идентификатору. */
    public void deleteTask(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        HashMap<Integer,SubTask>  subTaskForEpic = epics.get(id).getEpicSubTasks();
        for (int key : subTaskForEpic.keySet()) { //удаляем подзадачи эпика
            subtasks.remove(key);
        }
        epics.remove(id);
    }

    public void deleteSubTask(int id) {
        if (!subtasks.containsKey(id)) {
            return;
        }
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).deleteSubTask(subtasks.get(id));
        subtasks.remove(id);
    }

    /* Получение списка всех подзадач определённого эпика. */
    public List<SubTask> getSubTasksByEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            return null;
        }
        return epics.get(epicId).getEpicSubTasks().values().stream().toList();
    }
}

