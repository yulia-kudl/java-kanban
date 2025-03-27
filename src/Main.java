
public class Main {

    public static void main(String[] args) {

        //  Path file = Files.createFile(Paths.get("C:\\Users\\Юлия\\IdeaProjects\\java-kanban\\out\\test\\lala"));
        // TaskManager taskManager= Managers.getLocal(file.toString());
        //   File file = new File("C:\\Users\\Юлия\\IdeaProjects\\java-kanban\\out\\test\\lala");
//FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(file);

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

        taskManager.deleteAllEpics();
        printAllTasks(taskManager);
/*
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        subTask3.setStatus(TaskStatus.DONE);

        taskManager.updateTask(task1);
        taskManager.updateTask(task2);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());

        printAllTasks(taskManager);

        taskManager.deleteTask(task1.getId());
        taskManager.deleteEpic(epic1.getId());

        printAllTasks(taskManager);

        Task task3 = new Task("Погладить кота", TaskStatus.NEW, "Мурзик скучает");
        Task task4 = new Task("Покормить кота", TaskStatus.NEW, "Мурзик проголодался");
        Epic epic3 = new Epic("Запланировать путешествие", TaskStatus.NEW, "Тайланд на месяц");


        taskManager.createTask(task3);
        taskManager.createTask(task4);
        taskManager.createEpic(epic3);

        SubTask subTask4 = new SubTask("Купить билеты", TaskStatus.NEW, "Аэрофлот", epic3.getId());

        taskManager.createSubTask(subTask4);

        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.getSubTaskById(subTask4.getId());
        taskManager.getEpicTaskById(epic3.getId());


        // printAllTasks(taskManager);
        Task TaskForTest = new Task("task_for_test", TaskStatus.DONE, "for test");

        taskManager.createTask(TaskForTest);
        printAllTasks(taskManager);

*/
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("-------------------");
        System.out.println("Задачи:");
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

        System.out.println("История:");
        for (Task task : manager.getHistoryForTaskManager()) {
               System.out.println(task);
        }
    }
}
