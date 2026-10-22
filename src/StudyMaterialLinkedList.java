import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Doubly Linked List implementation that stores StudyMaterial nodes.
 * This is the core data structure for the project (built from scratch,
 * NOT java.util.LinkedList) — demonstrates: insertion, deletion, traversal,
 * search, and sorting on a linked list.
 */
public class StudyMaterialLinkedList implements Serializable {
    private static final long serialVersionUID = 1L;

    private Node<StudyMaterial> head;
    private Node<StudyMaterial> tail;
    private int size;
    private int idCounter; // auto-increment id generator

    public StudyMaterialLinkedList() {
        head = null;
        tail = null;
        size = 0;
        idCounter = 1;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    /** Generates the next unique id and reserves it. */
    public int nextId() { return idCounter++; }

    // ---------------------------------------------------------------
    // INSERTION
    // ---------------------------------------------------------------

    /** Insert a new node at the end of the list -> O(1) with tail pointer */
    public void addLast(StudyMaterial material) {
        Node<StudyMaterial> node = new Node<>(material);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        size++;
        if (material.getId() >= idCounter) {
            idCounter = material.getId() + 1;
        }
    }

    /** Insert a new node at the beginning of the list -> O(1) */
    public void addFirst(StudyMaterial material) {
        Node<StudyMaterial> node = new Node<>(material);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
        if (material.getId() >= idCounter) {
            idCounter = material.getId() + 1;
        }
    }

    // ---------------------------------------------------------------
    // DELETION
    // ---------------------------------------------------------------

    /** Delete node by id -> O(n) traversal, O(1) unlink */
    public boolean deleteById(int id) {
        Node<StudyMaterial> current = head;
        while (current != null) {
            if (current.data.getId() == id) {
                unlink(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    private void unlink(Node<StudyMaterial> node) {
        Node<StudyMaterial> before = node.prev;
        Node<StudyMaterial> after = node.next;

        if (before == null) {
            head = after;       // deleting head
        } else {
            before.next = after;
        }

        if (after == null) {
            tail = before;      // deleting tail
        } else {
            after.prev = before;
        }

        node.prev = null;
        node.next = null;
        size--;
    }

    /** Clear entire list */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
        idCounter = 1;
    }

    // ---------------------------------------------------------------
    // SEARCH / TRAVERSAL
    // ---------------------------------------------------------------

    /** Find a single node by its unique id */
    public StudyMaterial findById(int id) {
        Node<StudyMaterial> current = head;
        while (current != null) {
            if (current.data.getId() == id) return current.data;
            current = current.next;
        }
        return null;
    }

    /**
     * Search by keyword across title, subject, and description
     * (case-insensitive substring match). Returns matching items.
     */
    public List<StudyMaterial> search(String keyword) {
        List<StudyMaterial> results = new ArrayList<>();
        if (keyword == null) return results;
        String key = keyword.toLowerCase().trim();
        Node<StudyMaterial> current = head;
        while (current != null) {
            StudyMaterial m = current.data;
            if (m.getTitle().toLowerCase().contains(key)
                    || m.getSubject().toLowerCase().contains(key)
                    || m.getDescription().toLowerCase().contains(key)
                    || m.getType().toLowerCase().contains(key)) {
                results.add(m);
            }
            current = current.next;
        }
        return results;
    }

    /** Filter by exact subject match (case-insensitive) */
    public List<StudyMaterial> filterBySubject(String subject) {
        List<StudyMaterial> results = new ArrayList<>();
        Node<StudyMaterial> current = head;
        while (current != null) {
            if (current.data.getSubject().equalsIgnoreCase(subject)) {
                results.add(current.data);
            }
            current = current.next;
        }
        return results;
    }

    /** Filter by completion status */
    public List<StudyMaterial> filterByStatus(boolean completed) {
        List<StudyMaterial> results = new ArrayList<>();
        Node<StudyMaterial> current = head;
        while (current != null) {
            if (current.data.isCompleted() == completed) {
                results.add(current.data);
            }
            current = current.next;
        }
        return results;
    }

    /** Full traversal -> returns all items in list order */
    public List<StudyMaterial> toList() {
        List<StudyMaterial> results = new ArrayList<>();
        Node<StudyMaterial> current = head;
        while (current != null) {
            results.add(current.data);
            current = current.next;
        }
        return results;
    }

    // ---------------------------------------------------------------
    // SORTING  (in-place, swapping the .data payload of each node —
    // classic linked-list bubble sort, O(n^2), fine for a study-list size)
    // ---------------------------------------------------------------

    public void sortByPriority() {
        bubbleSort((a, b) -> a.priorityRank() - b.priorityRank());
    }

    public void sortByTitle() {
        bubbleSort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
    }

    public void sortBySubject() {
        bubbleSort((a, b) -> a.getSubject().compareToIgnoreCase(b.getSubject()));
    }

    public void sortByDateAdded() {
        bubbleSort((a, b) -> a.getDateAdded().compareTo(b.getDateAdded()));
    }

    private interface Comparator2 {
        int compare(StudyMaterial a, StudyMaterial b);
    }

    private void bubbleSort(Comparator2 cmp) {
        if (head == null) return;
        boolean swapped;
        do {
            swapped = false;
            Node<StudyMaterial> current = head;
            while (current != null && current.next != null) {
                if (cmp.compare(current.data, current.next.data) > 0) {
                    StudyMaterial temp = current.data;
                    current.data = current.next.data;
                    current.next.data = temp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }

    // ---------------------------------------------------------------
    // STATISTICS
    // ---------------------------------------------------------------

    public int countCompleted() {
        int count = 0;
        Node<StudyMaterial> current = head;
        while (current != null) {
            if (current.data.isCompleted()) count++;
            current = current.next;
        }
        return count;
    }

    public int countPending() {
        return size - countCompleted();
    }

    // ---------------------------------------------------------------
    // FILE PERSISTENCE (serialize the whole list to disk)
    // ---------------------------------------------------------------

    public void saveToFile(String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            List<StudyMaterial> snapshot = toList();
            oos.writeInt(idCounter);
            oos.writeInt(snapshot.size());
            for (StudyMaterial m : snapshot) {
                oos.writeObject(m);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(String filePath) throws IOException, ClassNotFoundException {
        File f = new File(filePath);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            clear();
            int savedCounter = ois.readInt();
            int count = ois.readInt();
            for (int i = 0; i < count; i++) {
                StudyMaterial m = (StudyMaterial) ois.readObject();
                addLast(m);
            }
            idCounter = savedCounter;
        }
    }
}
