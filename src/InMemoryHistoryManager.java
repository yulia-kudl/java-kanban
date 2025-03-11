import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
        private final HashMap<Integer, Node> idMap;
        private final List<Node> taskHistoryList;
        public Node head;
        public Node tail;
        public int size;

        InMemoryHistoryManager() {
            idMap = new HashMap<>();
            taskHistoryList = new LinkedList<>();
        }
        @Override
        public void add(Task task) {
            Task taskCopy = task.copyTask();
            if ( idMap.containsKey(task.getId())) {
                // delete old task history
                remove(task.getId());
            }
            Node nodeToAdd = new Node(taskCopy);
            linkLast(nodeToAdd);
            idMap.put(taskCopy.getId(), nodeToAdd);
        }

        @Override
        public List<Task> getHistory() {
            List<Task> historyList = new ArrayList<>();
            for (Node iterator = head; iterator!= null; iterator = iterator.next){
                historyList.add(iterator.task);
            }
            return historyList;
        }

        @Override
        public  void remove(int id) {
            if ( !idMap.containsKey(id)) {
                return;
            }
            Node nodeToDelete = idMap.get(id);
                 idMap.remove(id);
                removeNode(nodeToDelete);
        }

        private void removeNode(Node node) {
            // 1 node in list

            if ( this.size == 1) {
                taskHistoryList.remove(node);
                head = null;
                tail = null;
                size = 0;
                return;
            }
            // nodeToDelete is the first element
            if ( head.equals(node) ) {
                head = head.next;
                head.prev = null;
                size--;
                return;
            }
            // nodeToDelete is the last element
            if ( tail == node) {
                tail = node.prev;
                tail.next = null;
                size--;
                return;
            }
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }

        private void linkLast(Node node) {
            if ( size == 0) {
                taskHistoryList.add(node);
                head = node;
                tail = node;
                size++;
                return;
            }
            taskHistoryList.add(node);
            node.prev = tail;
            tail.next = node;
            tail = node;
            size++;
        }

}
