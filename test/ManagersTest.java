import org.junit.jupiter.api.Test;
import ru.yandex.kanban.HistoryManager;
import ru.yandex.kanban.Managers;
import ru.yandex.kanban.TaskManager;

import static org.junit.jupiter.api.Assertions.*;
class ManagersTest {


    static HistoryManager historyManager = Managers.getDefaultHistory();
    static TaskManager taskManager = Managers.getDefault();

    @Test
    void historyManagerNotNull() {
        assertNotNull(historyManager, "Object not initialized");
    }

    @Test
    void taskManagerNotNull() {
        assertNotNull(taskManager, "object not initialized");
    }

    @Test
    void managerReturnsRightInterface(){
        assertInstanceOf(HistoryManager.class, historyManager, "incorrect hostory interface");
        assertInstanceOf(TaskManager.class, taskManager, "incorrect task interface");
    }

}