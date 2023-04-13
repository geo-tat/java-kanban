package manager;

import java.io.File;

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