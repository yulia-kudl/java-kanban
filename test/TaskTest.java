import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    Task task;

    @BeforeEach
    void setUp() {
        task = new Task("task", TaskStatus.NEW, "desc", Duration.parse("PT30M"), LocalDateTime.now());
    }
    @Test
    void setAndGetId() {
        task.setId(100);
        assertEquals(100, task.getId(), "incorrect value");
    }


    @Test
    void setAndGetDescription() {
        task.setDescription("new");
        assertEquals("new",task.getDescription(), "incorrect value" );
    }


}