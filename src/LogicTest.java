import java.util.List;

public class LogicTest {
    public static void main(String[] args) throws Exception {
        StudyMaterialLinkedList list = new StudyMaterialLinkedList();

        list.addLast(new StudyMaterial(list.nextId(), "OS Notes", "Operating Systems", "Notes", "High", "2026-07-01", "Deadlock chapter"));
        list.addLast(new StudyMaterial(list.nextId(), "DBMS Book", "DBMS", "Book", "Medium", "2026-07-02", "Normalization"));
        list.addLast(new StudyMaterial(list.nextId(), "Networking Video", "Computer Networks", "Video", "Low", "2026-07-03", "TCP/IP layers"));
        list.addLast(new StudyMaterial(list.nextId(), "OS Assignment", "Operating Systems", "Assignment", "High", "2026-07-04", "Scheduling algorithms"));

        assertTrue(list.size() == 4, "size after 4 inserts");

        System.out.println("-- Traversal --");
        for (StudyMaterial m : list.toList()) System.out.println(m);

        System.out.println("-- Search 'os' --");
        List<StudyMaterial> results = list.search("os");
        assertTrue(results.size() == 2, "search 'os' should match OS Notes and OS Assignment");
        for (StudyMaterial m : results) System.out.println(m);

        System.out.println("-- Filter subject Operating Systems --");
        List<StudyMaterial> osItems = list.filterBySubject("Operating Systems");
        assertTrue(osItems.size() == 2, "filterBySubject Operating Systems == 2");

        System.out.println("-- Sort by priority --");
        list.sortByPriority();
        for (StudyMaterial m : list.toList()) System.out.println(m);
        assertTrue(list.toList().get(0).priorityRank() == 1, "first item after sort should be High priority");

        System.out.println("-- Toggle complete + delete --");
        StudyMaterial first = list.toList().get(0);
        first.setCompleted(true);
        assertTrue(list.countCompleted() == 1, "1 completed item");

        int idToDelete = list.toList().get(1).getId();
        boolean deleted = list.deleteById(idToDelete);
        assertTrue(deleted, "delete existing id should succeed");
        assertTrue(list.size() == 3, "size after delete == 3");

        System.out.println("-- Save/Load roundtrip --");
        String path = "test_data.dat";
        list.saveToFile(path);
        StudyMaterialLinkedList loaded = new StudyMaterialLinkedList();
        loaded.loadFromFile(path);
        assertTrue(loaded.size() == list.size(), "loaded size matches saved size");
        for (StudyMaterial m : loaded.toList()) System.out.println("Loaded: " + m);

        new java.io.File(path).delete();

        System.out.println("\nALL TESTS PASSED");
    }

    private static void assertTrue(boolean cond, String label) {
        if (!cond) {
            throw new RuntimeException("TEST FAILED: " + label);
        } else {
            System.out.println("PASS: " + label);
        }
    }
}
