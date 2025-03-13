import java.util.List;

public interface TaskManager {
    List<SubTask> getSubtasks();

    List<Epic> getEpics();

    List<Task> getTasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    /* Получение по идентификатору для всех типов задач */
    Task getTaskById(int id);

    Task getSubTaskById(int id);

    Task getEpicTaskById(int id);

    /* Создание. Сам объект должен передаваться в качестве параметра */
    void createTask(Task newTask);

    void createEpic(Epic newEpic);

    void createSubTask(SubTask newSubTask);

    /* Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра. */
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    /*Удаление по идентификатору. */
    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubTask(int id);

    /* Получение списка всех подзадач определённого эпика. */
    List<SubTask> getSubTasksByEpic(int epicId);

    List<Task> getHistoryForTaskManager();

}
