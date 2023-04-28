import api.HttpTaskServer;
import api.KVServer;
import java.io.IOException;


public class Main {


    public static void main(String[] args) throws IOException, InterruptedException {
/*
        KVServer server = new KVServer();
        server.start();
*/
        HttpTaskServer server1 = new HttpTaskServer();
        server1.start();


    }
}