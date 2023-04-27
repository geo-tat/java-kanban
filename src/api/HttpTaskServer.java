package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import config.TaskGsonBuilder;
import manager.Managers;
import manager.TaskManager;
import taskType.Epic;
import taskType.Subtask;
import taskType.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class HttpTaskServer {
    public static final int PORT = 8060;

    private final Gson gson = TaskGsonBuilder.getGson();
    TaskManager manager;
    HttpServer server;


    public HttpTaskServer() throws IOException, InterruptedException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        //  server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks/task", this::taskHandle);
        server.createContext("/tasks/subtask", this::subTaskHandle);
        server.createContext("/tasks/epic", this::epicHandle);
        server.createContext("/tasks/subtask/epic", this::subTaskByEpicIdHandle);
        server.createContext("/tasks/history", this::historyHandle);
        server.createContext("/tasks/prioritized", this::prioritizedHandle);
        manager = Managers.getDefault();

    }

    public void start() {
        server.start();
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер на порту" + PORT + "отключен!");
    }

    private void prioritizedHandle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            List<Task> prioritized = manager.getPrioritizedTasks();
            if (!prioritized.isEmpty()) {
                httpExchange.sendResponseHeaders(200, 0);          // выдан список
                String response = gson.toJson(prioritized);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                httpExchange.sendResponseHeaders(204, -1);          // список пуст
                String response = "Список пуст!";
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
            httpExchange.close();
        } else {
            httpExchange.sendResponseHeaders(405, 0);
            httpExchange.close();
        }
    }

    private void historyHandle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            List<Task> history = manager.getHistory();
            if (!history.isEmpty()) {
                httpExchange.sendResponseHeaders(200, 0);          // выдан список
                String response = gson.toJson(history);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            } else {
                httpExchange.sendResponseHeaders(204, -1);          // выдан список
                String response = "История просмотров задач пуста!";
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }
            httpExchange.close();
        } else {
            httpExchange.sendResponseHeaders(405, 0);
            httpExchange.close();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
    }

    private void taskHandle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String idParameter = uri.getQuery();

        switch (method) {
            case "GET":
                if (idParameter == null) {
                    List<Task> list = manager.getTasks();
                    if (list.isEmpty()) {
                        httpExchange.sendResponseHeaders(204, -1);

                    } else if (list != null) {
                        httpExchange.sendResponseHeaders(200, 0);
                        String response = gson.toJson(list);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    } else {
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                } else {
                    if (idParameter.contains("id=")) {
                        String[] strings = idParameter.split("=");
                        int id = Integer.parseInt(strings[1]);
                        Task task = manager.getTaskById(id);
                        if (task != null) {
                            httpExchange.sendResponseHeaders(200, 0);
                            String response = gson.toJson(task);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                            }
                        } else {
                            String response = "Такого ID не существует";
                            httpExchange.sendResponseHeaders(404, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                            }
                        }
                    } else {
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                }
                httpExchange.close();
                break;
            case "POST":
                InputStream inputStream = httpExchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Task oldTask = gson.fromJson(requestBody, Task.class);
                if (oldTask != null & oldTask.getId() == 0) {
                    Task newTask = manager.createTask(oldTask);
                    String response = gson.toJson(newTask);
                    httpExchange.sendResponseHeaders(201, 0);           // задача создана
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else if (oldTask.getId() != 0) {
                    boolean isUpdated = manager.updateTask(oldTask);
                    if (isUpdated) {
                        String response = gson.toJson(true);
                        httpExchange.sendResponseHeaders(202, 0);        // условие true, задача обновлена
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    } else {
                        String response = gson.toJson(false);
                        httpExchange.sendResponseHeaders(412, 0);    // условие false
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    }
                } else {
                    httpExchange.sendResponseHeaders(400, 0);
                }
                httpExchange.close();
                break;

            case "DELETE":
                if (idParameter == null) {
                    manager.removeAllTasks();
                    String response = "Все задачи удалены!";
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    String[] words = idParameter.split("=");
                    int id = Integer.parseInt(words[1]);
                    Task task = manager.getTaskById(id);
                    if (task == null) {
                        String response = "Задачи с этим ID не существует";
                        httpExchange.sendResponseHeaders(204, -1);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    } else {
                        manager.removeTaskById(id);
                        String response = "Задача удалена!";
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    }
                }
                httpExchange.close();
                break;
            default:
                httpExchange.sendResponseHeaders(501, 0);
                httpExchange.close();
                break;
        }


    }

    private void subTaskHandle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String idParameter = uri.getQuery();
        switch (method) {
            case "GET":
                if (idParameter == null) {
                    List<Subtask> list = manager.getSubtasks();
                    if (list.isEmpty()) {
                        httpExchange.sendResponseHeaders(204, -1);
                    }
                    if (list != null) {
                        httpExchange.sendResponseHeaders(200, 0);
                        String response = gson.toJson(list);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    } else {
                        httpExchange.sendResponseHeaders(204, -1);
                    }
                } else {
                    if (idParameter.contains("id=")) {
                        String[] strings = idParameter.split("=");
                        int id = Integer.parseInt(strings[1]);
                        Subtask subTask = manager.getSubtaskById(id);
                        if (subTask != null) {
                            httpExchange.sendResponseHeaders(200, 0);
                            String response = gson.toJson(subTask);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                            }
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    } else {
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                }
                httpExchange.close();
                break;
            case "POST":
                InputStream inputStream = httpExchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Subtask oldSub = gson.fromJson(requestBody, Subtask.class);
                if (oldSub != null & oldSub.getId() == 0) {
                    Subtask newSub = manager.createSubtask(oldSub);
                    String response = gson.toJson(newSub);
                    httpExchange.sendResponseHeaders(201, 0);           // задача создана
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else if (oldSub.getId() != 0) {
                    boolean isUpdated = manager.updateSubtask(oldSub);
                    if (isUpdated) {
                        String response = gson.toJson(true);
                        httpExchange.sendResponseHeaders(202, 0);        // условие true, задача обновлена
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    } else {
                        String response = gson.toJson(false);
                        httpExchange.sendResponseHeaders(412, 0);    // условие false
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    }
                } else {
                    httpExchange.sendResponseHeaders(400, 0);
                }
                httpExchange.close();
                break;

            case "DELETE":
                if (idParameter == null) {
                    manager.removeAllSubtasks();
                    String response = "Все подзадачи удалены!";
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    String[] words = idParameter.split("=");
                    int id = Integer.parseInt(words[1]);
                    Subtask sub = manager.getSubtaskById(id);
                    if (sub == null) {
                        String response = "Подзадачи с этим ID не существует";
                        httpExchange.sendResponseHeaders(204, -1);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    } else {
                        manager.removeSubtaskById(id);
                        String response = "Подзадача удалена!";
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    }
                }
                httpExchange.close();
                break;
            default:
                httpExchange.sendResponseHeaders(501, 0);
                httpExchange.close();
                break;
        }
    }

    private void epicHandle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String idParameter = uri.getQuery();
        switch (method) {
            case "GET":
                if (idParameter == null) {
                    List<Epic> list = manager.getEpics();
                    if (list.isEmpty()) {
                        httpExchange.sendResponseHeaders(204, -1);
                    }
                    if (list != null) {
                        String response = gson.toJson(list);
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    } else {
                        httpExchange.sendResponseHeaders(204, -1);
                    }
                } else {
                    if (idParameter.contains("id=")) {
                        String[] strings = idParameter.split("=");
                        int id = Integer.parseInt(strings[1]);
                        Epic epic = manager.getEpicById(id);
                        if (epic != null) {
                            httpExchange.sendResponseHeaders(200, 0);
                            String response = gson.toJson(epic);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes(StandardCharsets.UTF_8));
                            }
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    } else {
                        httpExchange.sendResponseHeaders(400, 0);
                    }
                }
                httpExchange.close();
                break;
            case "POST":
                InputStream inputStream = httpExchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                Epic oldEpic = gson.fromJson(requestBody, Epic.class);


                if (oldEpic != null & oldEpic.getId() == 0) {
                    Epic newEpic = manager.createEpic(oldEpic);
                    String response = gson.toJson(newEpic);
                    httpExchange.sendResponseHeaders(201, 0);           // задача создана
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }

                } else if (oldEpic.getId() != 0) {
                    boolean isUpdated = manager.updateEpic(oldEpic);
                    if (isUpdated) {
                        String response = gson.toJson(true);
                        httpExchange.sendResponseHeaders(202, 0);        // условие true, задача обновлена
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    } else {
                        String response = gson.toJson(false);
                        httpExchange.sendResponseHeaders(412, 0);    // условие false
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    }
                } else {
                    httpExchange.sendResponseHeaders(400, 0);
                }
                httpExchange.close();


                break;

            case "DELETE":
                if (idParameter == null) {
                    manager.removeAllEpics();
                    String response = "Все эпики удалены!";
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } else {
                    String[] words = idParameter.split("=");
                    int id = Integer.parseInt(words[1]);
                    Epic epic = manager.getEpicById(id);
                    if (epic == null) {
                        String response = "Эпика с этим ID не существует";
                        httpExchange.sendResponseHeaders(204, -1);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    } else {
                        manager.removeEpicForId(id);
                        String response = "Эпик удален!";
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    }
                }
                httpExchange.close();
                break;
            default:
                httpExchange.sendResponseHeaders(501, 0);
                httpExchange.close();
                break;
        }

    }

    private void subTaskByEpicIdHandle(HttpExchange httpExchange) throws IOException {
        URI uri = httpExchange.getRequestURI();
        String idParameter = uri.getQuery();
        String method = httpExchange.getRequestMethod();
        switch (method) {
            case "GET":
                if (idParameter.contains("id=")) {
                    String[] strings = idParameter.split("=");
                    int id = Integer.parseInt(strings[1]);
                    List<Subtask> list = manager.getSubtasksForEpic(id);
                    if (list != null) {
                        httpExchange.sendResponseHeaders(200, 0);
                        String response = gson.toJson(list);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                    } else {
                        httpExchange.sendResponseHeaders(404, 0); //задачи не существует
                    }
                } else {
                    httpExchange.sendResponseHeaders(400, 0);
                }
                httpExchange.close();
                break;
            case "POST":
            case "DELETE":
                httpExchange.sendResponseHeaders(405, 0);
                String response = "Выполнение запроса невозможно!";
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
                httpExchange.close();
                break;
            default:
                httpExchange.sendResponseHeaders(400, 0);
                httpExchange.close();
                break;
        }
    }


}
