import org.junit.jupiter.api.Test;
import ru.yandex.kanban.InMemoryHistoryManager;
import ru.yandex.kanban.Task;
import ru.yandex.kanban.TaskStatus;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void checkAddElementsToHistory() {
        InMemoryHistoryManager histMan = new InMemoryHistoryManager();
        Task task0 = new Task("task0", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task1 = new Task("task01", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task2 = new Task("task02", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task3 = new Task("task03", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task4 = new Task("task04", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task5 = new Task("task05", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task6 = new Task("task06", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task7 = new Task("task078", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task8 = new Task("task09", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task9 = new Task("task011", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task10 = new Task("task032", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task11 = new Task("task0reg", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task12 = new Task("task0rewg", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        task0.setId(0);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        task4.setId(4);
        task5.setId(5);
        task6.setId(6);
        task7.setId(7);
        task8.setId(8);
        task9.setId(9);
        task10.setId(10);
        task11.setId(11);
        task12.setId(12);
        assertNotNull(histMan.getHistory(), "history not initialized");
        histMan.add(task0);
        histMan.add(task1);
        assertEquals(histMan.tail.task, task1, "tail is correct");
        assertEquals(histMan.head.task, task0, "head is correct");
        histMan.add(task2);
        histMan.add(task3);
        histMan.add(task3);
        histMan.add(task4);
        histMan.add(task5);
        histMan.add(task6);
        histMan.add(task7);
        histMan.add(task8);
        histMan.add(task9);
        histMan.add(task10);
        histMan.add(task11);
        histMan.add(task12);
        assertEquals(13, histMan.getHistory().size(), "incorrect list size");
        assertEquals(histMan.tail.task, task12, "incorrect last element");
        assertEquals("task0", histMan.getHistory().getFirst().getName(), "incorrect first element");

    }

    @Test
    void checkValuesOfUpdatedElements() {
        InMemoryHistoryManager histMan = new InMemoryHistoryManager();
        Task task = new Task("task0", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        histMan.add(task);
        task.setDescription("new decs");
        assertNotEquals(task.getDescription(), histMan.getHistory().getFirst().getDescription(),
                "element was updated in history");


    }

    @Test
    void checkHistoryRemove() {
        InMemoryHistoryManager histMan = new InMemoryHistoryManager();
        Task task = new Task("task0", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task1 = new Task("task01", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task2 = new Task("task02", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        Task task3 = new Task("task03", TaskStatus.NEW, "task0 desc", Duration.parse("PT30M"));
        task.setId(0);
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        histMan.add(task);
        assertEquals(1, histMan.getHistory().size(), "incorrect size 1");
        assertEquals(1, histMan.size, "linked list size incorrect");
        histMan.add(task1);
        histMan.add(task2);
        histMan.add(task3);
        assertEquals(4, histMan.getHistory().size(), "incorrect size 3");
        histMan.remove(task1.getId());
        assertEquals(3, histMan.getHistory().size(), "incorrect size after delete  element");
        histMan.remove(task.getId());
        assertEquals(histMan.head.task, task2, "incorrect new head");
        assertEquals(histMan.tail.task, task3, "incorrect  tail");
        histMan.remove(task3.getId());
        assertEquals(histMan.head.task, task2, "incorrect new head");
        assertEquals(histMan.tail.task, task2, "incorrect  tail");
        assertEquals(1, histMan.getHistory().size(), "incorrect size ");


    }


}