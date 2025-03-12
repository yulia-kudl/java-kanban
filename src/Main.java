public class Main {

    public static void main(String[] args) {


        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Посмотреть фильм", TaskStatus.NEW, "фильм Гарри Поттер");
        Task task2 = new Task("Прочитать книгу", TaskStatus.NEW, "Алиса в стране чудес");
        Epic epic1 = new Epic("Купить продукты", TaskStatus.NEW, "продукты из Ашана на неделю");
        Epic epic2 = new Epic("Приготовить пирог", TaskStatus.NEW,
                "торт шоколадный с начинкой сникерс");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        SubTask subTask1 = new SubTask("Молоко", TaskStatus.NEW, "Простоквашино", epic1.getId());
        SubTask subTask2 = new SubTask("Хлеб", TaskStatus.NEW, "Зерновой", epic1.getId());
        SubTask subTask3 = new SubTask("Шоколад", TaskStatus.NEW, "с шоколадом и орехами", epic1.getId());

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.getEpicTaskById(epic1.getId());
        taskManager.getEpicTaskById(epic2.getId());
        taskManager.getSubTaskById(subTask2.getId());
        taskManager.getSubTaskById(subTask3.getId());
        taskManager.getSubTaskById(subTask2.getId());
        taskManager.getSubTaskById(subTask1.getId());
        taskManager.getSubTaskById(subTask1.getId());


        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());

        printAllTasks(taskManager);

        taskManager.deleteTask(task1.getId());
        taskManager.deleteEpic(epic1.getId());

        printAllTasks(taskManager);

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("-------------------");
        /*   System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubTasksByEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }
*/
        System.out.println("История:");
        for (Task task : manager.getHistoryForTaskManager()) {
            System.out.println(task);
        }
    }
}
