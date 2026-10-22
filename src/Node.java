import java.io.Serializable;

/**
 * Generic node for a doubly linked list.
 */
public class Node<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    T data;
    Node<T> next;
    Node<T> prev;

    public Node(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}
