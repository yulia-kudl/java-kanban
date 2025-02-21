import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    SubTask subt;

    @BeforeEach
    void setUp() {
        subt = new SubTask("task", TaskStatus.NEW, "desc", 10);
    }

    @Test
    void getEpicId() {
        assertEquals(10, subt.getEpicId(), "incorrect value");
    }

    @Test
    void setEpicId() {
        subt.setEpicId(100);
        assertEquals(100, subt.getEpicId(), "incorrect value");
    }

    @Test
    void copyTask() {
        SubTask subt2 = subt.copyTask();
        assertEquals(subt2.getEpicId(), subt.getEpicId(), "incorrect value");
        assertEquals(subt2.getDescription(), subt.getDescription(), "incorrect value");
        subt2.setStatus(TaskStatus.IN_PROGRESS);
        assertNotEquals(subt2.status, subt.status, "incorrect status value afterupdating");

    }
}