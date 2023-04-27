package manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Managers {


   public static TaskManager getDefault() throws IOException, InterruptedException {

        return  new HttpTaskManager("http://localhost:8070");                      // new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {

       return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFile() {
       return new FileBackedTasksManager(new File("resources\\history.csv"));
    }

    public static HttpTaskManager getDefaultHttpManager() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:8070");
    }
}
