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
                replySuccess(httpExchange, gson.toJson(prioritized), 200);              // выдан список
            } else {
                replyError(httpExchange, "Список пуст!", 204);
            }
            httpExchange.close();
        } else {
            replyError(httpExchange, "Используйте только GET метод", 501);
            httpExchange.close();
        }
    }

    private void historyHandle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            List<Task> history = manager.getHistory();
            if (!history.isEmpty()) {
                replySuccess(httpExchange, gson.toJson(history), 200);                  // выдан список
            } else {
                replyError(httpExchange, "История просмотров задач пуста!", 204);

            }
            httpExchange.close();
        } else {
            replyError(httpExchange, "Используйте только GET метод", 501);
            httpExchange.close();
        }
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
                        replyError(httpExchange, "Список пуст!", 204);
                    } else {
                        replySuccess(httpExchange, gson.toJson(list), 200);
                    }
                } else {
                    if (idParameter.contains("id=")) {
                        String[] strings = idParameter.split("=");
                        int id = Integer.parseInt(strings[1]);
                        Task task = manager.getTaskById(id);
                        if (task != null) {
                            replySuccess(httpExchange, gson.toJson(task), 200);
                        } else {
                            replyError(httpExchange, "Такого ID не существует", 404);
                        }
                    } else {
                        replyError(httpExchange, "Неправильно составлен запрос", 400);

                    }
                }
                httpExchange.close();
                break;
            case "POST":
                InputStream inputStream = httpExchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Task oldTask = gson.fromJson(requestBody, Task.class);
                if (oldTask != null && oldTask.getId() == 0) {
                    Task newTask = manager.createTask(oldTask);
                    replySuccess(httpExchange, gson.toJson(newTask), 201);            // задача создана
                } else if (oldTask.getId() != 0) {
                    boolean isUpdated = manager.updateTask(oldTask);
                    if (isUpdated) {
                        replySuccess(httpExchange, gson.toJson(true), 202);
                    } else {
                        replyError(httpExchange, gson.toJson(false), 202);       // пришел false
                    }
                } else {
                    replyError(httpExchange, "Неправильно составлен запрос", 400);
                }
                httpExchange.close();
                break;

            case "DELETE":
                if (idParameter == null) {
                    manager.removeAllTasks();
                    replySuccess(httpExchange, "Все задачи удалены.", 200);
                } else {
                    String[] words = idParameter.split("=");
                    int id = Integer.parseInt(words[1]);
                    Task task = manager.getTaskById(id);
                    if (task == null) {
                        replyError(httpExchange, "Задачи с этим ID не существует!", 404);
                    } else {
                        manager.removeTaskById(id);
                        replySuccess(httpExchange, "Задача удалена.", 200);
                    }
                }
                httpExchange.close();
                break;
            default:
                replyError(httpExchange, "Реализация находится в разработке.", 501);
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
                        replyError(httpExchange, "Список пуст!", 204);
                    }
                    replySuccess(httpExchange, gson.toJson(list), 200);
                } else {
                    if (idParameter.contains("id=")) {
                        String[] strings = idParameter.split("=");
                        int id = Integer.parseInt(strings[1]);
                        Subtask subTask = manager.getSubtaskById(id);
                        if (subTask != null) {
                            replySuccess(httpExchange, gson.toJson(subTask), 200);
                        } else {
                            replyError(httpExchange, "Задачи с этим ID не существует!", 404);
                        }
                    } else {
                        replyError(httpExchange, "Неправильно составлен запрос", 400);
                    }
                }
                httpExchange.close();
                break;
            case "POST":
                InputStream inputStream = httpExchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Subtask oldSub = gson.fromJson(requestBody, Subtask.class);
                if (oldSub != null && oldSub.getId() == 0) {
                    Subtask newSub = manager.createSubtask(oldSub);
                    replySuccess(httpExchange, gson.toJson(newSub), 201);              // задача создана
                } else if (oldSub.getId() != 0) {
                    boolean isUpdated = manager.updateSubtask(oldSub);
                    if (isUpdated) {
                        replySuccess(httpExchange, gson.toJson(true), 202);     // условие true задача обновлена
                    } else {
                        replySuccess(httpExchange, "Не получилось обновить задачу.", 202);    // условие false
                    }
                } else {
                    replyError(httpExchange, "Неправильно составлен запрос", 400);
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
                        replyError(httpExchange, "Подзадачи с этим ID не существует!", 404);
                    } else {
                        manager.removeSubtaskById(id);
                        replySuccess(httpExchange, "Подзадача удалена!", 200);
                    }
                }
                httpExchange.close();
                break;
            default:
                replyError(httpExchange, "Реализация находится в разработке.", 501);
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
                        replyError(httpExchange, "Список пуст!", 204);
                    }
                    replySuccess(httpExchange, gson.toJson(list), 200);
                } else {
                    if (idParameter.contains("id=")) {
                        String[] strings = idParameter.split("=");
                        int id = Integer.parseInt(strings[1]);
                        Epic epic = manager.getEpicById(id);
                        if (epic != null) {
                            replySuccess(httpExchange, gson.toJson(epic), 200);
                        } else {
                            replyError(httpExchange, "Эпика с этим ID не существует!", 404);
                        }
                    } else {
                        replyError(httpExchange, "Неправильно составлен запрос", 400);
                    }
                }
                httpExchange.close();
                break;
            case "POST":
                InputStream inputStream = httpExchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Epic oldEpic = gson.fromJson(requestBody, Epic.class);
                if (oldEpic != null && oldEpic.getId() == 0) {
                    Epic newEpic = manager.createEpic(oldEpic);
                    replySuccess(httpExchange, gson.toJson(newEpic), 201);        // эпик создан
                } else if (oldEpic.getId() != 0) {
                    boolean isUpdated = manager.updateEpic(oldEpic);
                    if (isUpdated) {
                        replySuccess(httpExchange, gson.toJson(true), 202);
                    } else {
                        replySuccess(httpExchange, "Не удалось обновить эпик", 202);
                    }
                } else {
                    replyError(httpExchange, "Неправильно составлен запрос", 400);
                }
                httpExchange.close();
                break;

            case "DELETE":
                if (idParameter == null) {
                    manager.removeAllEpics();
                    replySuccess(httpExchange, "Все эпики удалены.", 200);
                } else {
                    String[] words = idParameter.split("=");
                    int id = Integer.parseInt(words[1]);
                    Epic epic = manager.getEpicById(id);
                    if (epic == null) {
                        replyError(httpExchange, "Задачи с этим ID не существует!", 404);
                    } else {
                        manager.removeEpicForId(id);
                        replySuccess(httpExchange, "Эпик удален.", 200);

                    }
                }
                httpExchange.close();
                break;
            default:
                replyError(httpExchange, "Реализация находится в разработке.", 501);
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
                        replySuccess(httpExchange, gson.toJson(list), 200);
                    } else {
                        replyError(httpExchange, "У данного эпика нет подзадач", 204);
                    }
                } else {
                    replyError(httpExchange, "Неправильно составлен запрос", 400);
                }
                httpExchange.close();
                break;
            case "POST":
            case "DELETE":
                replyError(httpExchange, "Выполнение запроса невозможно!", 405);
                httpExchange.close();
                break;
            default:
                replyError(httpExchange, "Неправильно составлен запрос", 400);
                httpExchange.close();
                break;
        }
    }

    private void replySuccess(HttpExchange httpExchange, String text, int code) throws IOException {
        replyText(httpExchange, text, code, "application/json");
    }

    private void replyError(HttpExchange httpExchange, String text, int code) throws IOException {
        replyText(httpExchange, text, code, "text/plain");
    }

    private void replyText(HttpExchange httpExchange, String text, int code, String type) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", type);
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(bytes);
        }
    }


}
