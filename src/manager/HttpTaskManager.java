package manager;

import api.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import config.TaskGsonBuilder;
import taskType.Epic;
import taskType.Subtask;
import taskType.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class HttpTaskManager extends FileBackedTasksManager {
    KVTaskClient client;
    Gson gson = TaskGsonBuilder.getGson();
    public Type taskListType = new TypeToken<List<Task>>() {
    }.getType();

    public Type taskMapType = new TypeToken<Map<Integer, Task>>() {

    }.getType();
    public Type subtaskMapType = new TypeToken<Map<Integer, Subtask>>() {

    }.getType();

    public Type epicMapType = new TypeToken<Map<Integer, Epic>>() {
    }.getType();

    public Type priorityType = new TypeToken<TreeSet<Task>>() {
    }.getType();

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        super(null);
        client = new KVTaskClient(url);
        loadFromServer();
    }

    @Override
    public void save() {


        Map<Integer, Task> taskMap = new HashMap<>(tasks);
        Map<Integer, Subtask> subtaskMap = new HashMap<>(subtasks);
        Map<Integer, Epic> epicMap = new HashMap<>(epics);
        Set<Task> taskTreeSet = new TreeSet<>(prioritizedTask);
        List<Task> history = new ArrayList<>(historyManager.getHistory());
        try {
            client.put("task", gson.toJson(taskMap));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            client.put("subtask", gson.toJson(subtaskMap));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            client.put("epic", gson.toJson(epicMap));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            client.put("prioritized", gson.toJson(taskTreeSet));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            client.put("history", gson.toJson(history));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFromServer() throws IOException, InterruptedException {
        String taskJson = gson.fromJson(client.load("task"), taskMapType);
        if(taskJson !=null) {
            tasks = gson.fromJson(client.load("task"), taskMapType);
        }
        String subJson = gson.fromJson(client.load("task"), subtaskMapType);
        if(subJson !=null) {
            subtasks = gson.fromJson(client.load("task"), subtaskMapType);
        }
        String epicJson = gson.fromJson(client.load("epic"), epicMapType);
        if(epicJson !=null) {
            epics = gson.fromJson(client.load("epic"), epicMapType);
        }
        String prioritizedJson = gson.fromJson(client.load("prioritized"), priorityType);
        if( prioritizedJson !=null) {
            prioritizedTask = gson.fromJson(client.load("prioritized"), priorityType);
        }

        HistoryManager manager = Managers.getDefaultHistory();
        List<Task> history = gson.fromJson(client.load("history"), taskListType);
       if(history !=null) {
           for (Task task : history) {
               manager.add(task);
           }
       }

    }
}
