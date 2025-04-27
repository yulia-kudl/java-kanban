import org.junit.jupiter.api.BeforeEach;
import ru.yandex.kanban.Epic;
import ru.yandex.kanban.TaskStatus;

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
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "wrong status");
    }

}