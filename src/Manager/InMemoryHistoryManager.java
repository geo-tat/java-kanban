package Manager;

import TaskType.Task;

import java.util.*;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> customLinkedList = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        Node<Task> node = customLinkedList.history.get(task.getId());
        if (node != null) {
            customLinkedList.removeNode(node);
            customLinkedList.linkLast(task);
        } else {
            customLinkedList.linkLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> getHistory;
        getHistory = customLinkedList.getTasks();
        return getHistory;
    }

    @Override
    public void remove(int id) {
        customLinkedList.removeNode(customLinkedList.history.get(id));
        customLinkedList.history.remove(id);
    }

    public void checkList(LinkedList history) {
        if (history.size() == 10) {
            history.remove(0);
        }
    }

    private class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;
        private final HashMap<Integer, Node<T>> history = new HashMap<>();
        private int size;


        public void linkLast(Task task) {

            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(tail, task, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;

            } else {
                oldTail.next = newNode;
            }

            history.put(task.getId(), newNode);
            size = history.size();
        }

        public ArrayList<Task> getTasks() {
            List<Task> list = new ArrayList<>();
            Node<T> noda = head;
            while (noda != null) {
                list.add(noda.task);
                noda = noda.next;
            }

            return (ArrayList<Task>) list;
        }


        public void removeNode(Node<T> node) {
            if (node != null) {
                final Node<T> next = node.next;
                final Node<T> prev = node.prev;
                node.task = null;
                if (prev == null) {
                    head = next;
                } else {
                    prev.next = next;
                    node.prev = null;
                }

                if (next == null) {
                    tail = prev;
                } else {
                    next.prev = prev;
                    node.next = null;
                }

            }
        }
    }


}

