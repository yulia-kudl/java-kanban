public class Node {
    public Task task;
    public Node next;
    public Node prev;

    Node(Task task) {
        this.task = task;
        this.next = null;
        this.prev = null;
    }
}
