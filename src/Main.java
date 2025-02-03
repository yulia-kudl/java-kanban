public class Main {

    public static void main(String[] args) {


        TaskManager taskManager= new TaskManager();
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
        SubTask subTask3 = new SubTask("Приготовить тесто", TaskStatus.NEW, "с шоколадом и орехами",
                epic2.getId());

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        subTask3.setStatus(TaskStatus.DONE);

        taskManager.updateTask(task1);
        taskManager.updateTask(task2);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask3);

        System.out.println("-------------------");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        taskManager.deleteTask(task1.getId());
        taskManager.deleteEpic(epic1.getId());

        System.out.println("-------------------");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());


    }
}
