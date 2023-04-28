package api;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String apiUrl;
    private final String apiToken;
    private final HttpClient client = HttpClient.newHttpClient();

    public KVTaskClient(String apiUrl) throws IOException, InterruptedException {
        this.apiUrl = apiUrl;
        this.apiToken = register();

    }

    public void put(String key, String json) throws IOException, InterruptedException {
        String url = apiUrl + "/save/" + key + "?API_TOKEN=" + apiToken;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Что то пошло не так! Код: " + response.statusCode());
        }
    }

    public String load(String key) throws IOException, InterruptedException {
        String url = apiUrl + "/load/" + key + "?API_TOKEN=" + apiToken;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
           if(response.statusCode() == 200) {

            return response.body();
        } else {
            return "Что то пошло не так. Код ошибки: " + response.statusCode();
        }

    }

    private String register() throws IOException, InterruptedException {
        String url = apiUrl + "/register";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Что то пошло не так! Код: " + response.statusCode());
        }
        return response.body().trim();
    }
}
