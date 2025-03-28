import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(epic.status, TaskStatus.IN_PROGRESS, "wrong status");
    }

}