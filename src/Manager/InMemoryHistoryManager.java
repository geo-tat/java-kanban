package Manager;

import TaskType.Task;

import java.util.*;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history = new LinkedList<>();


    @Override
    public void add(Task task) {
        checkList(history);
    history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    public void checkList(LinkedList history) {
        if(history.size() == 9) {
            history.remove(0);
        }
    }
}
