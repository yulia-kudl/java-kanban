import java.util.HashMap;

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

    public HashMap<Integer, SubTask> getSubtasks() {
        if (subtasks.isEmpty()){
            System.out.println("Список подзадач пуст");
        }
        return subtasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        if (epics.isEmpty()){
            System.out.println("Список эпиков пуст");
        }
        return epics;
    }

    public HashMap<Integer, Task> getTasks() {
        if (subtasks.isEmpty()){
            System.out.println("Список задач пуст");
        }
        return tasks;
    }
    /*Методы для удаления всех типов задач */

    public  void deleteAllTasks() {
        this.tasks.clear();
        System.out.println("Задачи удалены!");
    }

    public void deleteAllEpics() {
        this.epics.clear();
        this.subtasks.clear(); //если нет эпиков, то подзадачи удаляются
        System.out.println("Эпики удалены");
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
        System.out.println("Подзадачи удалены!");
    }

    /* Получение по идентификатору для всех типов задач */
    public Task getTaskById(int id) {
        if (tasks.isEmpty()) {
            System.out.println("Задач в системе нет!");
            return null;
        }
        if (!(tasks.containsKey(id))) {
            System.out.println("Задачи с таким ID нет");
            return null;
        }
        return tasks.get(id);
    }

    public Task getSubTaskById(int id) {
        if (subtasks.isEmpty()) {
            System.out.println("Подзадач в системе нет!");
            return null;
        }
        if (!(subtasks.containsKey(id))) {
            System.out.println("Подзадачи с таким ID нет");
            return null;
        }
        return subtasks.get(id);
    }

    public Task getEpicTaskById(int id) {
        if (epics.isEmpty()) {
            System.out.println("Эпиков в системе нет!");
            return null;
        }
        if (!(epics.containsKey(id))) {
            System.out.println("Эпика с таким ID нет");
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
        tasks.put(idCounter, newTask);
    }

    public void createEpic(Epic newEpic) {
        if ( newEpic == null ) {
            return;
        }
        idCounter++;
        newEpic.setId(idCounter);
        newEpic.setEpicSubTasks(new HashMap<>()); //всегда добавляем эпик без подзадач
        newEpic.updateEpicStatus();
        epics.put(idCounter, newEpic);
    }

    public void createSubTask(SubTask newSubTask) {
        if (newSubTask == null) {
            return;
        }
        int epicId = newSubTask.getEpicId();
        if (!epics.containsKey(epicId)) {
            System.out.println("У подзадачи некорректный эпик");
            return;
        }
        idCounter++;
        newSubTask.setId(idCounter);
        subtasks.put(idCounter, newSubTask);
        epics.get(epicId).addSubTask(newSubTask);
    }

    /* Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра. */
    public void updateTask(Task task) {
        if ( task == null) {
            return;
        }
        if (!(tasks.containsKey(task.getId()))) {
            System.out.println("Такой задачи нет");
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        if ( epic == null) {
            return;
        }
        if (!(epics.containsKey(epic.getId()))) {
            System.out.println("Такого эпика нет");
            return;
        }
        epics.get(epic.getId()).updateEpic(epic);
    }

    public void updateSubTask(SubTask subTask) {
        if ( subTask == null) {
            return;
        }
        if (!(subtasks.containsKey(subTask.getId()))) {
            System.out.println("Такой подзадачи нет");
            return;
        }
        if (!epics.containsKey(subTask.getEpicId())) {
            System.out.println("Эпика привязан некорректно(такого эпика нет)");
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
            System.out.println("Подзадачи с таким ID нет");
            return;
        }
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпика с таким ID нет");
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
            System.out.println("Подзадачи с таким ID нет");
            return;
        }
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).deleteSubTask(subtasks.get(id));
        subtasks.remove(id);
    }

    /* Получение списка всех подзадач определённого эпика. */
    public HashMap<Integer, SubTask> getSubTasksByEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Такого эпика нет");
            return null;
        }
        return epics.get(epicId).getEpicSubTasks();
    }
}

