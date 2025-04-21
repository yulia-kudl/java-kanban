import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy|HH:mm");


    @Test
    void loadFromEmptyFile() {
        File file = null;
        try {
            file = File.createTempFile("test_load", ".tmp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        assertNotNull(taskManager, "Object not initialized");
        assertNotNull(taskManager.getTasksHashMap(), "Tasks not initialized");
        assertNotNull(taskManager.getEpicHashMap(), "Epics not initialized");
        assertNotNull(taskManager.getSubTasksHashMap(), "SubTasks not initialized");
        assertEquals(0, taskManager.getTasksHashMap().size(), "Tasks not empty");
        assertEquals(0, taskManager.getEpicHashMap().size(), "Tasks not empty");
        assertEquals(0, taskManager.getSubTasksHashMap().size(), "Tasks not empty");
        assertEquals(1, taskManager.getIdCounter(), "Counter incorrect");

    }

    @Test
    void loadFromFile() {
        File file = null;
        try {
            file = File.createTempFile("test_load", ".tmp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Writer fr = new FileWriter(file.toString())) {
            fr.write("id,type,name,status,description,duration,startTime,epic" + '\n');
            fr.write("1,TASK,Task1,NEW,Description task1,90,01_02_2025|07:00, " + '\n');
            fr.write("2,TASK,Task2,DONE,Description task2,170,02_04_2024|09:30, " + '\n');
            fr.write("3,EPIC,Epic3,IN_PROGRESS,Description epic3,70,01_01_2025|08:59, " + '\n');
            fr.write("4,EPIC,Epic4,DONE,Description epic4,90,01_03_2025|06:45, " + '\n');
            fr.write("5,SUBTASK,Sub Task5,DONE,Description sub task5,50,01_01_2025|08:59,3" + '\n');
            fr.write("7,SUBTASK,Sub Task7,NEW,Description sub task7,20,02_03_2025|12:00,3" + '\n');
            fr.write("6,SUBTASK,Sub Task6,DONE,Description sub task6,90,01_03_2025|06:45,4" + '\n');
            fr.write("8,TASK,TASK NO START,NEW,Description task no start,90, " + '\n');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(3, taskManager.getTasksHashMap().size(), "Tasks not empty");
        assertEquals(2, taskManager.getEpicHashMap().size(), "Tasks not empty");
        assertEquals(3, taskManager.getSubTasksHashMap().size(), "Tasks not empty");
        assertEquals(9, taskManager.getIdCounter(), "Counter incorrect");

        assertEquals("Description task1", taskManager.getTaskById(1).getDescription(), "descr not equal task1");
        assertEquals("Task1", taskManager.getTaskById(1).name, "name not equal task1");
        assertEquals(TaskStatus.NEW, taskManager.getTaskById(1).status, "status not equal task1");
        assertEquals(Duration.parse("PT90M"), taskManager.getTaskById(1).duration, "wrong duration");
        assertEquals(LocalDateTime.parse("01_02_2025|07:00", formatter), taskManager.getTaskById(1).startTime, "wrong start time");


        assertEquals("Description task2", taskManager.getTaskById(2).getDescription(), "descr not equal task2");
        assertEquals("Task2", taskManager.getTaskById(2).name, "name not equal task2");
        assertEquals(TaskStatus.DONE, taskManager.getTaskById(2).status, "status not equal task2");
        assertEquals(Duration.parse("PT170M"), taskManager.getTaskById(2).duration, "wrong duration");
        assertEquals(LocalDateTime.parse("02_04_2024|09:30", formatter), taskManager.getTaskById(2).startTime, "wrong start time");


        assertEquals("Description epic3", taskManager.getEpicTaskById(3).getDescription(), "descr not equal epic3");
        assertEquals("Epic3", taskManager.getEpicTaskById(3).name, "name not equal epic3");
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicTaskById(3).status, "status not equal epic3");
        Epic epic = (Epic) taskManager.getEpicTaskById(3);
        // assertEquals(2, epic.getEpicSubTasks().size(), "wrong number of subtasks epic 3");
        assertTrue(epic.getEpicSubTasks().contains((Integer) 5), "wrong subtask epic 3/5");
        assertTrue(epic.getEpicSubTasks().contains((Integer) 7), "wrong subtask epic 3/7");
        assertEquals(Duration.parse("PT70M"), epic.duration, "wrong duration");
        assertEquals(LocalDateTime.parse("01_01_2025|08:59", formatter), epic.startTime,
                "wrong start time");
        assertEquals(LocalDateTime.parse("02_03_2025|12:20", formatter), epic.getEndTime(),
                "wrong end time for epic");



        assertEquals("Description epic4", taskManager.getEpicTaskById(4).getDescription(), "descr not equal epic4");
        assertEquals("Epic4", taskManager.getEpicTaskById(4).name, "name not equal epic4");
        assertEquals(TaskStatus.DONE, taskManager.getEpicTaskById(4).status, "status not equal epic4");
        epic = (Epic) taskManager.getEpicTaskById(4);
        assertEquals(1, epic.getEpicSubTasks().size(), "wrong number of subtasks epic 4");
        assertTrue(epic.getEpicSubTasks().contains((Integer) 6), "wrong subtask epic 3/5");
        assertEquals(Duration.parse("PT90M"), epic.duration, "wrong duration epic2");
        assertEquals(LocalDateTime.parse("01_03_2025|06:45", formatter), epic.startTime,
                "wrong start time epic4");



        assertEquals("Description sub task5", taskManager.getSubTaskById(5).getDescription(), "descr not equal task5");
        assertEquals("Sub Task5", taskManager.getSubTaskById(5).name, "name not equal sub task5");
        assertEquals(TaskStatus.DONE, taskManager.getSubTaskById(5).status, "status not equal sub task5");
        SubTask subtask = (SubTask) taskManager.getSubTaskById(5);
        assertEquals(3, subtask.getEpicId(), "wrong epic id for subtask 5 ");
        assertEquals(Duration.parse("PT50M"), subtask.duration, "wrong duration epic2");
        assertEquals(LocalDateTime.parse("01_01_2025|08:59", formatter), subtask.startTime,
                "wrong start time subtask5");

        assertEquals(5, taskManager.getPrioritizedTasks().size(), "wrong prioritized size");
        assertEquals(taskManager.getPrioritizedTasks().getFirst(), taskManager.getTaskById(2),"wrong prioritized first");
        assertEquals(taskManager.getPrioritizedTasks().getLast(), taskManager.getSubTaskById(7),"wrong prioritized last");
                assertFalse(taskManager.getPrioritizedTasks().contains(taskManager.getTaskById(8)), "wrong  prioritezid element");


        taskManager.createTask(new Task("newTask", TaskStatus.NEW, "desc new", Duration.parse("PT1M"), LocalDateTime.now()));
        assertEquals(4, taskManager.getTasksHashMap().size(), "new task was added");
    }

    @Test
    void SaveToFile() {
        File file = null;
        try {
            file = File.createTempFile("test_load", ".tmp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file.toString());
        Task task1 = new Task("task1", TaskStatus.NEW, "desc1", Duration.parse("PT10M"), LocalDateTime.of(2023, 2, 3, 15, 45));
        Task task2 = new Task("task2", TaskStatus.NEW, "desc2", Duration.parse(("PT20M")), LocalDateTime.of(2025, 2, 3, 15, 45));
        SubTask subTask4 = new SubTask("stask4", TaskStatus.IN_PROGRESS, "desc1 epic", 3,
                Duration.parse("PT180M"), LocalDateTime.of(2024, 2, 3, 19, 45));
        Epic epic3 = new Epic("epic3", TaskStatus.DONE, "desc epic3");


        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        taskManager.createSubTask(subTask4);

        String fileString;
        try {
            fileString = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("ошибка");
        }
        String[] str = fileString.split("\n");

        assertEquals("id,type,name,status,description,duration,startTime,epic", str[0], "head is wrong");
        assertEquals("1,TASK,task1,NEW,desc1,10,03_02_2023|15:45, ", str[1], "task1 is wrong");
        assertEquals("2,TASK,task2,NEW,desc2,20,03_02_2025|15:45, ", str[2], "task2 is wrong");
        assertEquals("4,SUBTASK,stask4,IN_PROGRESS,desc1 epic,180,03_02_2024|19:45,3", str[3], "subtask is wrong");
        assertEquals("3,EPIC,epic3,IN_PROGRESS,desc epic3,180,03_02_2024|19:45, ", str[4], "epic is wrong");
    }


}