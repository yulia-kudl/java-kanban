import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void checkAddElementsToHistory() {
        InMemoryHistoryManager histMan = new InMemoryHistoryManager();
        assertNotNull(histMan.getHistory(), "history not initialized");
        histMan.add(new Task("task0",TaskStatus.NEW, "task0 desc"));
        histMan.add(new Task("task1",TaskStatus.NEW, "task1 desc"));
        histMan.add(new Task("task2",TaskStatus.NEW, "task2 desc"));
        histMan.add(new Task("task3",TaskStatus.NEW, "task3 desc"));
        histMan.add(new Task("task4",TaskStatus.NEW, "task4 desc"));
        histMan.add(new Task("task5",TaskStatus.NEW, "task5 desc"));
        histMan.add(new Task("task6",TaskStatus.NEW, "task6 desc"));
        histMan.add(new Task("task7",TaskStatus.NEW, "task7 desc"));
        histMan.add(new Task("task8",TaskStatus.NEW, "task8 desc"));
        histMan.add(new Task("task9",TaskStatus.NEW, "task9 desc"));
        assertEquals(10, histMan.getHistory().size(), "incorrect list size");
        histMan.add(new Task("task10",TaskStatus.NEW, "task10 desc"));
        assertEquals(10, histMan.getHistory().size(), "incorrect ,max list size");
        histMan.add(new Task("task11",TaskStatus.NEW, "task10 desc"));
        assertEquals("task2", histMan.getHistory().getFirst().name, "incorrect first element");

    }

    @Test
    void checkValuesOfUpdatedElements() {
        InMemoryHistoryManager histMan = new InMemoryHistoryManager();
        Task task = new Task("task0", TaskStatus.NEW, "task0 desc");
        histMan.add(task);
        task.setDescription("new decs");
        assertNotEquals(task.getDescription(), histMan.getHistory().getFirst().getDescription(),
                "element was updated in history");


    }
}