import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
/// ;'
class EpicTest {

    Epic epic;

    @BeforeEach
    void setUp() {
        epic = new Epic("epic", TaskStatus.DONE, "desc");
    }

    @org.junit.jupiter.api.Test
    void setStatus() {
        epic.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(epic.status, TaskStatus.NEW, "wrong status");
    }
//Test  for epic status
    void testEpicStatus() {
        assertEquals(TaskStatus.NEW, epic.status, "incorrect first status");
        SubTask subt = new SubTask("subt", TaskStatus.IN_PROGRESS, "dec", 10);
        epic.addSubTask(subt);
        assertEquals(TaskStatus.IN_PROGRESS, epic.status, "incorrect status after adding subtask");
        subt.setStatus(TaskStatus.NEW);
        assertEquals(TaskStatus.NEW, epic.status, "incorrect status after updating subtask");
        subt.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, epic.status, "incorrect status after updating subtask");

    }


}