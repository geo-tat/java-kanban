package Manager;

import java.io.File;
import java.nio.file.Path;

public class Managers {


   public static TaskManager getDefault() {

        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {

       return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFile(File path) {
       return new FileBackedTasksManager(path);
    }
}
