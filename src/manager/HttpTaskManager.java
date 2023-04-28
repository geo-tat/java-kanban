package manager;

import api.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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

    private final Type taskMapType = new TypeToken<Map<Integer, Task>>() {

    }.getType();
    private final Type subtaskMapType = new TypeToken<Map<Integer, Subtask>>() {

    }.getType();

    private final Type epicMapType = new TypeToken<Map<Integer, Epic>>() {
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
        List<Integer> history = new ArrayList<>();
        for (Task t : historyManager.getHistory()) {
            history.add(t.getId());
        }
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
            client.put("history", gson.toJson(history));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFromServer() throws IOException, InterruptedException {
        int loadID = 0;
        try {
            String taskJson = client.load("task");
            if (taskJson != null) {
                Map<Integer, Task> taskMap = gson.fromJson(taskJson, taskMapType);
                tasks.putAll(taskMap);
                for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                    prioritizedTask.add(entry.getValue());
                    loadID = Math.max(loadID, entry.getKey());
                }
            }
            String subJson = (client.load("subtask"));
            if (subJson != null) {
                Map<Integer, Subtask> subtaskMap = gson.fromJson(subJson, subtaskMapType);
                subtasks.putAll(subtaskMap);
                for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                    prioritizedTask.add(entry.getValue());
                    loadID = Math.max(loadID, entry.getKey());
                }
            }
            String epicJson = (client.load("epic"));
            if (epicJson != null) {
                Map<Integer,Epic> epicMap = gson.fromJson(epicJson, epicMapType);
                epics.putAll(epicMap);
                for(Map.Entry<Integer,Epic> entry : epics.entrySet()) {
                    prioritizedTask.add(entry.getValue());
                    loadID = Math.max(loadID, entry.getKey());
                }
            }

            String historyJson = (client.load("history"));
            if (historyJson != null) {
                List<Integer> history = gson.fromJson(historyJson,new TypeToken<List<Integer>>(){}.getType());
                if (history != null) {
                    for (Integer id : history) {
                        if (tasks.containsKey(id)) {
                            historyManager.add(tasks.get(id));
                        }
                        if (subtasks.containsKey(id)) {
                            historyManager.add(subtasks.get(id));
                        }
                        if (epics.containsKey(id)) {
                            historyManager.add(epics.get(id));
                        }
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            System.out.println("Данные на сервере отсутствуют");
        }
        currentID = loadID;
    }
}
