package ru.yandex.kanban;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final String fileName;
    private Path path;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy|HH:mm");

    public FileBackedTaskManager(String fileName) {
        super();
        this.fileName = fileName;
        Path path = Path.of(fileName);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager result = new FileBackedTaskManager(file.toString());
        String fileString = null;
        try {
            fileString = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("ошибка");
        }
        String[] tasksFromFile = fileString.split("\n");
        int counterNew = 0;
        for (int i = 1; i < tasksFromFile.length; i++) {
            Task task = fromString(tasksFromFile[i]);
            assert task != null;
            if (task.getId() > counterNew) {
                counterNew = task.getId();
            }
            switch (task.type) {
                case TaskType.TASK:
                    result.getTasksHashMap().put(task.getId(), task);
                    break;
                case TaskType.EPIC:
                    result.getEpicHashMap().put(task.getId(), (Epic) task);
                    break;
                case TaskType.SUBTASK:
                    result.getSubTasksHashMap().put(task.getId(), (SubTask) task);
                    break;

            }
        }
        for (SubTask sTask : result.getSubTasksHashMap().values()) {
            int epicId = sTask.getEpicId();
            result.getEpicHashMap().get(epicId).getEpicSubTasks().add(sTask.getId());
        }
        result.getEpics().forEach(epic -> result.updateTimeForEpic(epic.getId()));
        result.setIdCounter(++counterNew);

        result.getTasks().stream().filter(task -> task.startTime != null).forEach(result.tasksSorted::add);
        result.getSubtasks().stream().filter(task -> task.startTime != null).forEach(result.tasksSorted::add);
        return result;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();

    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void createTask(Task newTask) {
        super.createTask(newTask);
        save();
    }

    @Override
    public void createEpic(Epic newEpic) {
        super.createEpic(newEpic);
        save();

    }

    @Override
    public void createSubTask(SubTask newSubTask) {
        super.createSubTask(newSubTask);
        save();

    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();

    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();

    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();

    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();

    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();

    }

    private void save() {

        try (Writer fr = new FileWriter(this.fileName)) {
            fr.write("id,type,name,status,description,duration,startTime,epic" + '\n');
            for (Integer id : this.getTasksHashMap().keySet()) {
                String taskString = toString(this.getTasksHashMap().get(id));
                fr.write(taskString);
            }
            for (Integer id : this.getSubTasksHashMap().keySet()) {
                String taskString = toString(this.getSubTasksHashMap().get(id));
                fr.write(taskString);
            }
            for (Integer id : this.getEpicHashMap().keySet()) {
                String taskString = toString(this.getEpicHashMap().get(id));
                fr.write(taskString);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл");
        }

    }

    private String toString(Task task) {
        if (task.type != TaskType.SUBTASK) {
            return task.getId() + "," + task.type + "," + task.name + "," + task.status + ","
                    + task.description + "," + (task.duration == null ? " " : task.duration.toMinutes()) + "," +
                    (task.startTime == null ? " " : task.startTime.format(formatter)) + ", "
                    + '\n';
        }
        SubTask subTask = (SubTask) task;

        return task.getId() + "," + task.type + "," + task.name + "," + task.status + "," + task.description + ","
                + task.duration.toMinutes() + "," + (task.startTime == null ? " " : task.startTime.format(formatter)) + "," + subTask.getEpicId() + '\n';

    }


    private static Task fromString(String value) {
        String[] arrayTask = value.split(",");
        if (TaskType.valueOf(arrayTask[1]) == TaskType.TASK) {
            Task task = new Task(arrayTask[2], TaskStatus.valueOf(arrayTask[3]),
                    arrayTask[4], Duration.parse("PT" + arrayTask[5] + "M"), (Objects.equals(arrayTask[6], " ") ? null : LocalDateTime.parse(arrayTask[6], formatter)));
            task.setId(Integer.parseInt(arrayTask[0]));
            return task;
        }
        if (TaskType.valueOf(arrayTask[1]) == TaskType.EPIC) {
            Task task = new Epic(arrayTask[2], TaskStatus.valueOf(arrayTask[3]), arrayTask[4]);
            task.setId(Integer.parseInt(arrayTask[0]));
            task.setStatus(TaskStatus.valueOf(arrayTask[3]));
            task.startTime = arrayTask[6].equals(" ") ? null : LocalDateTime.parse(arrayTask[6], formatter);
            task.duration = arrayTask[5].equals(" ") ? null : Duration.parse("PT" + arrayTask[5] + "M");
            return task;
        }
        if (TaskType.valueOf(arrayTask[1]) == TaskType.SUBTASK) {
            Task task = new SubTask(arrayTask[2], TaskStatus.valueOf(arrayTask[3]), arrayTask[4],
                    Integer.parseInt(arrayTask[7]), Duration.parse("PT" + arrayTask[5] + "M"),
                    (arrayTask[6].equals(" ") ? null : LocalDateTime.parse(arrayTask[6], formatter)));
            task.setId(Integer.parseInt(arrayTask[0]));
            return task;
        }
        return null;
    }
}
