import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    InMemoryTaskManager taskMan;
    Task task0;
    Task task1;
    Task task2;
    Task task3;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;
    SubTask subTask4;
    Epic epic0;
    Epic epic1;
    LocalDateTime date1 = LocalDateTime.of(2025, 1, 26, 2, 45);
    LocalDateTime date2 = LocalDateTime.of(2023, 3, 5, 7, 50);
    LocalDateTime date3 = LocalDateTime.of(2023, 3, 5, 8, 9);
    LocalDateTime date4 = LocalDateTime.of(2025, 3, 5, 8, 9);
    Duration dur1 = Duration.parse("PT30M");
    Duration dur2 = Duration.parse("PT40M");

    @BeforeEach
    void setUp() {
        taskMan = new InMemoryTaskManager();
        task0 = new Task("task0", TaskStatus.NEW, "desc0", Duration.parse("PT30M"), date1);
        task1 = new Task("task1", TaskStatus.NEW, "desc1", Duration.parse("PT30M"), date2);
        task2 = new Task("task2", TaskStatus.NEW, "desc2", Duration.parse("PT30M"), date3);
        task3 = new Task("task3", TaskStatus.NEW, "desc3", Duration.parse("PT30M"));
        epic0 = new Epic("epic0", TaskStatus.NEW, "desc0 epic");
        epic1 = new Epic("epic1", TaskStatus.NEW, "desc1 epic");

        subTask1 = new SubTask("subtask 1", TaskStatus.NEW, "desc subt1",1, Duration.parse("PT30M"), date2);
        subTask2 = new SubTask("subtask 2", TaskStatus.NEW,  "desc subt2",1, Duration.parse("PT30M"), date3);
        subTask3 = new SubTask("subtask 3", TaskStatus.NEW, "desc subt3",1, Duration.parse("PT30M"));
        subTask4 = new SubTask("subtask 4", TaskStatus.NEW, "desc subt4",1, Duration.parse("PT30M"), date1);
    }




    @Test
    void createTaskIntersectionOrWithoutStartDate() {
        taskMan.createTask(task1);
        taskMan.createTask(task2);
        taskMan.createTask(task3);

        assertEquals(2, taskMan.getTasks().size(), "incorrect task size");
        assertFalse(taskMan.getTasks().contains(task2), "intersection ");
        assertEquals(1, taskMan.getPrioritizedTasks().size(), "wrong prioritized list");
    }


    @Test
    void createSubTaskIntersectionOrWithoutStartDate() {
        taskMan.createEpic(epic0);
        taskMan.createSubTask(subTask1);
        taskMan.createSubTask(subTask2);
        taskMan.createSubTask(subTask3);

        assertEquals(2, taskMan.getSubtasks().size(), "incorrect subtask size");
        assertFalse(taskMan.getSubtasks().contains(subTask2), "intersection ");
        assertEquals(1, taskMan.getPrioritizedTasks().size(), "wrong prioritized list");
    }


    @Test
    void updateTaskTime() {
        taskMan.createTask(task0);
        taskMan.createTask(task1);
        Task task3 = task0.copyTask();
        task3.startTime = date3;// intersection
        taskMan.updateTask(task3);

        assertEquals(date1, taskMan.getTaskById(1).startTime, "time was updated with intersection");

        task3.startTime = date4;
        taskMan.updateTask(task3);

        assertEquals(date4, taskMan.getTaskById(1).startTime, "time was not updated");

    }

    @Test
    void updateEpic() {
    }

    @Test
    void updateSubTaskTime() {
        taskMan.createEpic(epic0);
        taskMan.createSubTask(subTask1);
        taskMan.createSubTask(subTask4);

        SubTask subTask10 = subTask4.copyTask();
        subTask10.startTime = date3;// intersection
        taskMan.updateSubTask(subTask10);

       assertEquals(date1, taskMan.getSubTaskById(3).startTime, "time was updated with intersection");

        subTask10.startTime = date4;
        taskMan.updateSubTask(subTask10);

        assertEquals(date4, taskMan.getSubTaskById(3).startTime, "time was not updated");


    }

    @Test
    void deleteTaskFromPrioritezid() {
        taskMan.createTask(task0);
        taskMan.createTask(task1);

        assertEquals(2, taskMan.getTasks().size(), "wrong size");
        assertEquals(2, taskMan.getPrioritizedTasks().size(), "wrong prioritezid size");

        taskMan.deleteTask(task1.getId());
        assertEquals(1, taskMan.getTasks().size(), "wrong size");
        assertEquals(1, taskMan.getPrioritizedTasks().size(), "wrong prioritezid size");


    }


}