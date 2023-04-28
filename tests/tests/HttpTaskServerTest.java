package tests;

import api.HttpTaskServer;
import api.KVServer;
import com.google.gson.Gson;
import config.TaskGsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskType.Epic;
import taskType.Status;
import taskType.Subtask;
import taskType.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {


    HttpTaskServer server1;
    KVServer server;
    HttpClient client;
    Gson gson;

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        server = new KVServer();
        server.start();
        server1 = new HttpTaskServer();
        server1.start();
        gson = TaskGsonBuilder.getGson();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void theEnd() {
        server.stop();
        server1.stop();
    }

    @Test
    public void taskHandle() throws IOException, InterruptedException {
        // создаём экземпляр URI, содержащий адрес нужного ресурса
        URI uri = URI.create("http://localhost:8060/tasks/task");
        Task task1 = new Task(0, "TASK-1", "description", Status.NEW);
        // создаём объект, описывающий HTTP-запрос

        HttpRequest.BodyPublisher taskToServer = HttpRequest.BodyPublishers.ofString(gson.toJson(task1));
        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .POST(taskToServer)    // указываем HTTP-метод запроса
                .uri(uri) // указываем адрес ресурса
                .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола
                .build(); // заканчиваем настройку и создаём ("строим") http-запрос

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        task1.setId(1);                           // Изменяем ID для проверки
        assertEquals(gson.toJson(task1), response.body());

        //   делаем update

        Task updateTask = new Task(1, "TASK-1", "description", Status.IN_PROGRESS);
        HttpRequest.BodyPublisher updateToServer = HttpRequest.BodyPublishers.ofString(gson.toJson(updateTask));
        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(updateToServer)
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> updatedResponse = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(202, updatedResponse.statusCode(), "Задача не обновилась");
        assertTrue(Boolean.parseBoolean(updatedResponse.body()));

        // добавляем еще одну задачу

        Task task2 = new Task(0, "TASK-2", "description", Status.NEW);
        HttpRequest.BodyPublisher task2ToServer = HttpRequest.BodyPublishers.ofString(gson.toJson(task2));
        HttpRequest requestSecondTask = HttpRequest.newBuilder()
                .POST(task2ToServer)
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseTask2 = client.send(requestSecondTask, HttpResponse.BodyHandlers.ofString());

        // делаем GET запрос

        HttpRequest requestAllTasks = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGetTasks = client.send(requestAllTasks, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetTasks.statusCode());

        Task task1Check = new Task(1, "TASK-1", "description", Status.IN_PROGRESS);
        Task task2Check = new Task(2, "TASK-2", "description", Status.NEW);
        List<Task> test = new ArrayList<>(List.of(task1Check, task2Check));
        String testJson = gson.toJson(test);

        assertEquals(testJson, responseGetTasks.body());

        // GET запрос по ID=2
        URI uriID = URI.create("http://localhost:8060/tasks/task?id=2");

        HttpRequest requestTaskById = HttpRequest.newBuilder()
                .GET()
                .uri(uriID)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGetTaskById = client.send(requestTaskById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetTaskById.statusCode());
        String taskCheck = gson.toJson(task2Check);
        assertEquals(taskCheck, responseGetTaskById.body());

        // DELETE запрос по ID=2

        HttpRequest requestRemoveTaskById = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriID)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseRemoveTaskById = client.send(requestRemoveTaskById,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseRemoveTaskById.statusCode(), "Задача не удалилась");

        // DELETE запрос на удаление всех задач

        HttpRequest requestRemoveAllTasks = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseRemoveAllTasks = client.send(requestRemoveAllTasks,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseRemoveAllTasks.statusCode());
    }

    @Test
    public void taskHandleWhenEmptyGET() throws IOException, InterruptedException {
        // Given
        URI uri = URI.create("http://localhost:8060/tasks/task");
        URI uriID = URI.create("http://localhost:8060/tasks/task?id=2");
        // When
        HttpRequest requestAllTasks = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGetTasks = client.send(requestAllTasks, HttpResponse.BodyHandlers.ofString());

        // Then
        assertEquals(204, responseGetTasks.statusCode());

        // When
        HttpRequest requestTaskByID = HttpRequest.newBuilder()
                .GET()
                .uri(uriID)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGetTaskByID = client.send(requestTaskByID, HttpResponse.BodyHandlers.ofString());
        // Then
        assertEquals(404, responseGetTaskByID.statusCode());
    }

    @Test
    public void taskHandleWhenEmptyDELETE() throws IOException, InterruptedException {
        // Given
        URI uri = URI.create("http://localhost:8060/tasks/task");

        // When
        HttpRequest requestRemoveAllTasks = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseRemoveAllTasks = client.send(requestRemoveAllTasks,
                HttpResponse.BodyHandlers.ofString());
        // Then
        assertEquals(200, responseRemoveAllTasks.statusCode());
        // Given
        URI uriID = URI.create("http://localhost:8060/tasks/task?id=2");
        // When
        HttpRequest requestRemoveTaskById = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriID)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseRemoveTaskById = client.send(requestRemoveTaskById,
                HttpResponse.BodyHandlers.ofString());
        // Then
        assertEquals(404, responseRemoveTaskById.statusCode());
    }

    @Test
    public void epicHandle() throws IOException, InterruptedException {
        // POST создание эпика
        // Given
        Subtask sub1 = new Subtask(0, "Subtask-1", "description", Status.NEW, 1);
        Epic epic1 = new Epic(0, "Epic-1", "description", Status.NEW, new ArrayList<>());
        URI uriSub = URI.create("http://localhost:8060/tasks/subtask");
        URI uriEpic = URI.create("http://localhost:8060/tasks/epic");
        // When
        HttpRequest.BodyPublisher epicToServer = HttpRequest.BodyPublishers.ofString(gson.toJson(epic1));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(epicToServer)
                .uri(uriEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Then
        assertEquals(201, response.statusCode());
        epic1.setId(1);
        assertEquals(gson.toJson(epic1), response.body());

        //   делаем update

        Task updateEpic = new Epic(1, "Epic-1", "NEW description", Status.NEW, new ArrayList<>());
        HttpRequest.BodyPublisher updateToServer = HttpRequest.BodyPublishers.ofString(gson.toJson(updateEpic));
        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(updateToServer)
                .uri(uriEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> updatedResponse = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(202, updatedResponse.statusCode(), "Задача не обновилась");
        assertTrue(Boolean.parseBoolean(updatedResponse.body()));

        // добавляем еще одну задачу

        Epic epic2 = new Epic(0, "Epic-2", "description", Status.NEW, new ArrayList<>());
        HttpRequest.BodyPublisher epic2ToServer = HttpRequest.BodyPublishers.ofString(gson.toJson(epic2));
        HttpRequest requestSecondEpic = HttpRequest.newBuilder()
                .POST(epic2ToServer)
                .uri(uriEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseEpic2 = client.send(requestSecondEpic, HttpResponse.BodyHandlers.ofString());

        // делаем GET запрос

        HttpRequest requestAllTasks = HttpRequest.newBuilder()
                .GET()
                .uri(uriEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGetTasks = client.send(requestAllTasks, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetTasks.statusCode());

        Epic epic1Check = new Epic(1, "Epic-1", "NEW description", Status.NEW, new ArrayList<>());
        Epic epic2Check = new Epic(2, "Epic-2", "description", Status.NEW, new ArrayList<>());
        List<Task> test = new ArrayList<>(List.of(epic1Check, epic2Check));
        String testJson = gson.toJson(test);

        assertEquals(testJson, responseGetTasks.body());

        // GET запрос по ID=2
        URI uriID = URI.create("http://localhost:8060/tasks/epic?id=2");

        HttpRequest requestEpicById = HttpRequest.newBuilder()
                .GET()
                .uri(uriID)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGetEpicById = client.send(requestEpicById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetEpicById.statusCode());
        String taskCheck = gson.toJson(epic2Check);
        assertEquals(taskCheck, responseGetEpicById.body());

        // DELETE запрос по ID=2

        HttpRequest requestRemoveEpicById = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriID)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseRemoveEpicById = client.send(requestRemoveEpicById,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseRemoveEpicById.statusCode(), "Задача не удалилась");

        // DELETE запрос на удаление всех задач

        HttpRequest requestRemoveAllEpics = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseRemoveAllEpics = client.send(requestRemoveAllEpics,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseRemoveAllEpics.statusCode());
    }

    @Test
    public void epicHandleWhenEmptyGET() throws IOException, InterruptedException {
        // Given
        URI uri = URI.create("http://localhost:8060/tasks/epic");

        // When
        HttpRequest requestAllEpics = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGetEpics = client.send(requestAllEpics, HttpResponse.BodyHandlers.ofString());

        // Then
        assertEquals(204, responseGetEpics.statusCode());
        // Given
        URI uriID = URI.create("http://localhost:8060/tasks/epic?id=2");

        // When
        HttpRequest requestEpicByID = HttpRequest.newBuilder()
                .GET()
                .uri(uriID)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGetEpicByID = client.send(requestEpicByID, HttpResponse.BodyHandlers.ofString());
        // Then
        assertEquals(404, responseGetEpicByID.statusCode());
    }

    @Test
    public void epicHandleWhenEmptyDELETE() throws IOException, InterruptedException {
        // Given
        URI uri = URI.create("http://localhost:8060/tasks/epic");

        // When
        HttpRequest requestRemoveAllEpics = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseRemoveAllEpics = client.send(requestRemoveAllEpics,
                HttpResponse.BodyHandlers.ofString());
        // Then
        assertEquals(200, responseRemoveAllEpics.statusCode());
        // Given
        URI uriID = URI.create("http://localhost:8060/tasks/epic?id=2");
        // When
        HttpRequest requestRemoveEpicById = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriID)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseRemoveEpicById = client.send(requestRemoveEpicById,
                HttpResponse.BodyHandlers.ofString());
        // Then
        assertEquals(404, responseRemoveEpicById.statusCode());
    }

    @Test
    public void subtaskHandleAndEpicSubsHandle() throws IOException, InterruptedException {
        // Given
        Subtask sub1 = new Subtask(0, "Subtask-1", "description", Status.NEW, 1);
        Epic epic1 = new Epic(0, "Epic-1", "description", Status.NEW, new ArrayList<>());
        URI uriSub = URI.create("http://localhost:8060/tasks/subtask");
        URI uriEpic = URI.create("http://localhost:8060/tasks/epic");
        HttpRequest.BodyPublisher epicToServer = HttpRequest.BodyPublishers.ofString(gson.toJson(epic1));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(epicToServer)
                .uri(uriEpic)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseEpic = client.send(request, HttpResponse.BodyHandlers.ofString());
        // When
        HttpRequest.BodyPublisher subToServer = HttpRequest.BodyPublishers.ofString(gson.toJson(sub1));
        HttpRequest requestSub = HttpRequest.newBuilder()
                .POST(subToServer)
                .uri(uriSub)
                .build();
        HttpResponse<String> response = client.send(requestSub, HttpResponse.BodyHandlers.ofString());

        // Then
        assertEquals(201, response.statusCode());
        sub1.setId(2);
        assertEquals(gson.toJson(sub1), response.body());
        //   делаем update


        Subtask updateSubtask = new Subtask(2, "Subtask-1", "NEW description", Status.NEW, 1);
        HttpRequest.BodyPublisher updateToServer = HttpRequest.BodyPublishers.ofString(gson.toJson(updateSubtask));
        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(updateToServer)
                .uri(uriSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> updatedResponse = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(202, updatedResponse.statusCode(), "Задача не обновилась");
        assertTrue(Boolean.parseBoolean(updatedResponse.body()));

        // добавляем еще одну подзадачу

        Subtask sub2 = new Subtask(0, "Subtask-2", "description", Status.NEW, 1);
        HttpRequest.BodyPublisher sub2ToServer = HttpRequest.BodyPublishers.ofString(gson.toJson(sub2));
        HttpRequest requestSecondSub = HttpRequest.newBuilder()
                .POST(sub2ToServer)
                .uri(uriSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseSub2 = client.send(requestSecondSub, HttpResponse.BodyHandlers.ofString());

        // делаем GET запрос

        HttpRequest requestAllSubtask = HttpRequest.newBuilder()
                .GET()
                .uri(uriSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGetSubtask = client.send(requestAllSubtask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetSubtask.statusCode());

        Subtask sub1Check = new Subtask(2, "Subtask-1", "NEW description", Status.NEW, 1);
        Subtask sub2Check = new Subtask(3, "Subtask-2", "description", Status.NEW, 1);
        List<Task> test = new ArrayList<>(List.of(sub1Check, sub2Check));
        String testJson = gson.toJson(test);

        assertEquals(testJson, responseGetSubtask.body());

        // GET запрос по ID=2
        URI uriID = URI.create("http://localhost:8060/tasks/subtask?id=2");

        HttpRequest requestSubById = HttpRequest.newBuilder()
                .GET()
                .uri(uriID)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGetSubById = client.send(requestSubById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseGetSubById.statusCode());
        String taskCheck = gson.toJson(sub1Check);
        assertEquals(taskCheck, responseGetSubById.body());

        // GET запрос на subTaskByEpic
        URI uriEpicSubs = URI.create("http://localhost:8060/tasks/subtask/epic?id=1");
        HttpRequest requestEpicSubs = HttpRequest.newBuilder()
                .GET()
                .uri(uriEpicSubs)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseEpicSubs = client.send(requestEpicSubs, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseEpicSubs.statusCode());

        assertEquals(testJson, responseEpicSubs.body());


        // DELETE запрос по ID=2

        HttpRequest requestRemoveSubById = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriID)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseRemoveSubById = client.send(requestRemoveSubById,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseRemoveSubById.statusCode(), "Задача не удалилась");

        // DELETE запрос на удаление всех задач

        HttpRequest requestRemoveAllSubs = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriSub)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseRemoveAllSubs = client.send(requestRemoveAllSubs,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseRemoveAllSubs.statusCode());
    }

    @Test
    public void subtaskHandleWhenEmptyGET() throws IOException, InterruptedException {
        // Given
        URI uri = URI.create("http://localhost:8060/tasks/subtask");

        // When
        HttpRequest requestAllSubtasks = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGetSubtasks = client.send(requestAllSubtasks, HttpResponse.BodyHandlers.ofString());

        // Then
        assertEquals(204, responseGetSubtasks.statusCode());
        // Given
        URI uriID = URI.create("http://localhost:8060/tasks/subtask?id=2");

        // When
        HttpRequest requestSubtaskByID = HttpRequest.newBuilder()
                .GET()
                .uri(uriID)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseGetSubtaskByID = client.send(requestSubtaskByID, HttpResponse.BodyHandlers.ofString());
        // Then
        assertEquals(404, responseGetSubtaskByID.statusCode());
    }

    @Test
    public void subtaskHandleWhenEmptyDELETE() throws IOException, InterruptedException {
        // Given
        URI uri = URI.create("http://localhost:8060/tasks/subtask");

        // When
        HttpRequest requestRemoveAllSubs = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseRemoveAllSubs = client.send(requestRemoveAllSubs,
                HttpResponse.BodyHandlers.ofString());
        // Then
        assertEquals(200, responseRemoveAllSubs.statusCode());
        // Given
        URI uriID = URI.create("http://localhost:8060/tasks/subtask?id=2");
        // When
        HttpRequest requestRemoveSubById = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriID)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseRemoveSubById = client.send(requestRemoveSubById,
                HttpResponse.BodyHandlers.ofString());
        // Then
        assertEquals(404, responseRemoveSubById.statusCode());
    }

    @Test
    public void historyAndPriorityHandle() throws IOException, InterruptedException {
        // Given
        Task task1 = new Task(0, "TASK-1", "description", Status.NEW);
        Task task2 = new Task(0, "TASK-2", "New description", Status.NEW);
        Task task3 = new Task(0, "TASK-3", "Super description", Status.NEW);
        URI uriTask = URI.create("http://localhost:8060/tasks/task");
        URI uriHistory = URI.create("http://localhost:8060/tasks/history");
        URI uriTask1 = URI.create("http://localhost:8060/tasks/task?id=1");
        URI uriTask2 = URI.create("http://localhost:8060/tasks/task?id=2");
        URI uriTask3 = URI.create("http://localhost:8060/tasks/task?id=3");

        // Создаем Задачи//

        HttpRequest.BodyPublisher task1Request = HttpRequest.BodyPublishers.ofString(gson.toJson(task1));
        HttpRequest requestTask1 = HttpRequest.newBuilder()
                .POST(task1Request)
                .uri(uriTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response1 = client.send(requestTask1, HttpResponse.BodyHandlers.ofString());

        HttpRequest.BodyPublisher task2Request = HttpRequest.BodyPublishers.ofString(gson.toJson(task2));
        HttpRequest requestTask2 = HttpRequest.newBuilder()
                .POST(task2Request)
                .uri(uriTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response2 = client.send(requestTask2, HttpResponse.BodyHandlers.ofString());

        HttpRequest.BodyPublisher task3Request = HttpRequest.BodyPublishers.ofString(gson.toJson(task3));
        HttpRequest requestTask3 = HttpRequest.newBuilder()
                .POST(task3Request)
                .uri(uriTask)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response3 = client.send(requestTask3, HttpResponse.BodyHandlers.ofString());

        // Вызываем задачи//

        HttpRequest requestTask1ById = HttpRequest.newBuilder()
                .GET()
                .uri(uriTask1)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseTask1 = client.send(requestTask1ById, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestTask2ById = HttpRequest.newBuilder()
                .GET()
                .uri(uriTask2)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseTask2 = client.send(requestTask2ById, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestTask3ById = HttpRequest.newBuilder()
                .GET()
                .uri(uriTask3)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseTask3 = client.send(requestTask3ById, HttpResponse.BodyHandlers.ofString());


        // When
        HttpRequest historyToServer = HttpRequest.newBuilder()
                .uri(uriHistory)
                .GET()
                .build();
        HttpResponse<String> getHistoryResponse = client.send(historyToServer, HttpResponse.BodyHandlers.ofString());
        // Then
        Task task11 = new Task(1, "TASK-1", "description", Status.NEW);
        Task task22 = new Task(2, "TASK-2", "New description", Status.NEW);
        Task task33 = new Task(3, "TASK-3", "Super description", Status.NEW);
        List<Task> historyCheck = new ArrayList<>(List.of(task11, task22, task33));
        String check = gson.toJson(historyCheck);
        assertEquals(200, getHistoryResponse.statusCode());
        assertEquals(check, getHistoryResponse.body());

        URI prioritizedUri = URI.create("http://localhost:8060/tasks/prioritized");
        HttpRequest prioritizedToServer = HttpRequest.newBuilder()
                .uri(prioritizedUri)
                .GET()
                .build();
        HttpResponse<String> prioritizedResponse = client.send(prioritizedToServer, HttpResponse.BodyHandlers.ofString());
        assertNotNull(prioritizedResponse.body());
        assertEquals(200, prioritizedResponse.statusCode());
        assertEquals(check, prioritizedResponse.body());
    }

    @Test
    public void historyHandleWhenEmpty() throws IOException, InterruptedException {
        // Given
        URI uriHistory = URI.create("http://localhost:8060/tasks/history");
        // When
        HttpRequest historyToServer = HttpRequest.newBuilder()
                .uri(uriHistory)
                .GET()
                .build();
        HttpResponse<String> getHistoryResponse = client.send(historyToServer, HttpResponse.BodyHandlers.ofString());
        // Then

        assertEquals(204, getHistoryResponse.statusCode());
    }

}

