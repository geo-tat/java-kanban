package Manager;

import TaskType.Task;

class Node<T> {
    public Task task;
    public Node<T> next;
    public Node<T> prev;

    public Node(Node<T> prev, Task task, Node<T> next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }


}
