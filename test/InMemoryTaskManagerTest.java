import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
     InMemoryTaskManager taskMan;
     Task task0;
     Task task1;
     Epic epic0;
     Epic epic1;
    @BeforeEach
    void setUp() {
        taskMan = new InMemoryTaskManager();
        task0 = new Task("task0", TaskStatus.NEW, "desc0");
        task1 = new Task("task1", TaskStatus.NEW, "desc1");
        epic0 = new Epic("epic0", TaskStatus.NEW, "desc0 epic");
        epic1 = new Epic("epic1", TaskStatus.NEW, "desc1 epic");
    }

    @Test
    void checkAddandGetSubTasks() {

        assertTrue(taskMan.getSubtasks().isEmpty(), "getSubtasks is not empty");
        taskMan.createEpic(epic0);
        SubTask subt =new SubTask("subT0", TaskStatus.NEW, "desc0 subT", epic0.getId());
        taskMan.createSubTask(subt);
        assertEquals(1, taskMan.getSubtasks().size(), "incorrect size");
        assertEquals("desc0 subT", taskMan.getSubtasks().getFirst().getDescription(), "incorrectDescription");
        assertEquals(subt, taskMan.getSubTaskById(subt.getId()), "subTaskNotFound");

    }


    @Test
    void heckAddandGetEpics() {
        assertTrue(taskMan.getEpics().isEmpty(), "Epics is not empty");
        taskMan.createEpic(epic0);
        assertEquals(1, taskMan.getEpics().size(), "incorrect size");
        assertEquals("desc0 epic", taskMan.getEpics().getFirst().getDescription(), "incorrectDescription");
        assertEquals(epic0, taskMan.getEpicTaskById(epic0.getId()), "Epic not found");

    }

    @Test
    void checkAddAndGetTasks() {
        assertTrue(taskMan.getTasks().isEmpty(), "Tasks is not empty");
        taskMan.createTask(task0);
        assertEquals(1, taskMan.getTasks().size(), "incorrect size");
        assertEquals("desc0", taskMan.getTasks().getFirst().getDescription(), "incorrectDescription");
        assertEquals(task0, taskMan.getTaskById(task0.getId()), "Task not found");

    }

    @Test
    void deleteAllTasks() {
        taskMan.createTask(task0);
        taskMan.createTask(task1);
        taskMan.getTaskById(task0.getId());
        taskMan.getTaskById(task1.getId());
        assertEquals(2, taskMan.getTasks().size(), "incorrect size");
        assertEquals(2, taskMan.historyManager.getHistory().size(), "incorrect size");
        taskMan.deleteAllTasks();
        assertEquals(0, taskMan.historyManager.getHistory().size(), "history not empty");
        assertNull(taskMan.getTaskById(task0.getId()), "deleted Task was found");
        assertTrue(taskMan.getTasks().isEmpty(), "Tasks are not empty");

    }

    @Test
    void deleteAllEpics() {
        taskMan.createEpic(epic0);
        taskMan.createEpic(epic1);
        assertEquals(2, taskMan.getEpics().size(), "incorrect size");
        SubTask subt =new SubTask("subT0", TaskStatus.NEW, "desc0 subT", epic0.getId());
        taskMan.createSubTask(subt);
        assertEquals(subt, taskMan.getSubTasksByEpic(epic0.getId()).getFirst(), "subTask wasnt added to Epic");
        assertEquals(1, taskMan.getSubtasks().size(), "incorrect subtask size");
        taskMan.getEpicTaskById(epic0.getId());
        taskMan.getEpicTaskById(epic1.getId());
        taskMan.getSubTaskById(subt.getId());
        assertEquals(3, taskMan.historyManager.getHistory().size(), "history wrong size");
        taskMan.deleteAllEpics();
                assertEquals(0, taskMan.historyManager.getHistory().size(), "history not empty");
        assertNull(taskMan.getEpicTaskById(epic0.getId()), "deleted epic  was found");
        assertEquals(0, taskMan.getEpics().size(), "Epics are not empty");
        assertEquals(0, taskMan.getSubtasks().size(), "Subtasks are not empty");
    }

    @Test
    void deleteAllSubTasks() {
        taskMan.createEpic(epic0);
        taskMan.createEpic(epic1);
        SubTask subt0 =new SubTask("subT0", TaskStatus.IN_PROGRESS, "desc0 subT", epic0.getId());
        SubTask subt1 = new SubTask("subT1", TaskStatus.IN_PROGRESS, "desc", epic1.getId());
        taskMan.createSubTask(subt0);
        taskMan.createSubTask(subt1);
        assertEquals(2, taskMan.getSubtasks().size(), "incorrect subtask size");
        assertEquals(TaskStatus.IN_PROGRESS, epic0.status, "epic status wasn't updated");
        taskMan.getSubTaskById(subt0.getId());
        taskMan.getSubTaskById(subt1.getId());
        assertEquals(2, taskMan.historyManager.getHistory().size(), "history wrong size");

        taskMan.deleteAllSubTasks();
        assertEquals(0, taskMan.historyManager.getHistory().size(), "history wrong size");

        assertNull(taskMan.getSubTaskById(subt1.getId()), "deleted subtask  was found");
        assertEquals(0, taskMan.getSubtasks().size(), "Subtasks are not empty");
    }

    @Test
    void updateTask() {
        taskMan.createTask(task0);
        assertEquals(task0.getDescription(), taskMan.getTaskById(task0.getId()).getDescription(),
                "description not the same after adding");
        task0.setDescription("new description");
        taskMan.updateTask(task0);
        assertEquals(task0.getDescription(), taskMan.getTaskById(task0.getId()).getDescription(),
                "description not the same after updating");
    }

    @Test
    void updateEpic() {
        taskMan.createEpic(epic0);
        assertEquals(epic0.getDescription(), taskMan.getEpicTaskById(epic0.getId()).getDescription(),
                "description not the same after adding");
        epic0.setDescription("new description");
        taskMan.updateEpic(epic0);
        assertEquals(epic0.getDescription(), taskMan.getEpicTaskById(epic0.getId()).getDescription(),
                "description not the same after updating");
    }

    @Test
    void updateSubTask() {
        taskMan.createEpic(epic0);
        SubTask subt0 =new SubTask("subT0", TaskStatus.IN_PROGRESS, "desc0 subT", epic0.getId());
        taskMan.createSubTask(subt0);
        assertEquals(subt0.getDescription(), taskMan.getSubTaskById(subt0.getId()).getDescription(),
                "description not the same after adding");
        subt0.setDescription("new description");
        taskMan.updateSubTask(subt0);
        assertEquals(subt0.getDescription(), taskMan.getSubTaskById(subt0.getId()).getDescription(),
                "description not the same after updating");
        subt0.setStatus(TaskStatus.DONE);
        taskMan.updateSubTask(subt0);
        assertEquals(TaskStatus.DONE, epic0.status,
                "epic STATUS wasn't updated after updating");

    }

    @Test
    void deleteTask() {
        taskMan.createTask(task0);
        assertEquals(task0, taskMan.getTaskById(task0.getId()), "added task wasnt found");
        taskMan.deleteTask(task0.getId());
        assertNull(taskMan.getTaskById(task0.getId()), "deleted task was found");

    }

    @Test
    void deleteEpic() {
        taskMan.createEpic(epic0);
        assertEquals(epic0, taskMan.getEpicTaskById(epic0.getId()), "added epic wasnt found");
        SubTask subt = new SubTask("subt", TaskStatus.NEW, "desc", epic0.getId());
        taskMan.createSubTask(subt);
        assertEquals(subt, taskMan.getSubTasksByEpic(epic0.getId()).getFirst(), "added subtask for epic wasnt found");
        assertEquals(1, taskMan.getHistoryForTaskManager().size(), "history size is ok");
        taskMan.deleteEpic(epic0.getId());
       assertNull(taskMan.getEpicTaskById(epic0.getId()), "deleted task was found");
        assertNull(taskMan.getSubTaskById(subt.getId()), "subtask of deleted task was found");

    }

    @Test
    void deleteSubTask() {
        taskMan.createEpic(epic0);
        SubTask subt = new SubTask("subt", TaskStatus.IN_PROGRESS, "desc", epic0.getId());
        taskMan.createSubTask(subt);
        assertEquals(subt, taskMan.getSubTasksByEpic(epic0.getId()).getFirst(), "added subtask for epic wasnt found");
        assertEquals(TaskStatus.IN_PROGRESS, epic0.status, "epic status wasnt changed");
        taskMan.deleteSubTask(subt.getId());
        assertNull(taskMan.getSubTaskById(subt.getId()), "deleted subtask was found");
        assertEquals(TaskStatus.NEW, epic0.status, "epic status havent changed after deleting subtask");


    }

    @Test
    void getHistoryForTaskManager() {
        assertEquals(0, taskMan.getHistoryForTaskManager().size(), "history not empty");
        taskMan.createTask(task0);
        taskMan.createTask(task1);
        taskMan.createEpic(epic0);
        taskMan.createEpic(epic1);
        taskMan.getTaskById(task0.getId());
        taskMan.getTaskById(task1.getId());
        taskMan.getTaskById(task1.getId());
        taskMan.getEpicTaskById(epic0.getId());
        taskMan.getEpicTaskById(epic1.getId());
        taskMan.getEpicTaskById(epic1.getId());
        taskMan.getEpicTaskById(epic1.getId());
        assertEquals(4, taskMan.getHistoryForTaskManager().size(), "incorrect history size");
        assertEquals(epic0.getId(), taskMan.getHistoryForTaskManager().get(2).getId(), "incorrect value in history");
    }

}