# Study Material Manager (Java Swing + Custom Doubly Linked List)

A standalone Java desktop app for college project submission. Manages study
materials (notes, books, videos, links, assignments) with full CRUD, search,
filter, sort, and persistent storage — all backed by a **custom-built doubly
linked list** (no `java.util.LinkedList` used), as required for a Data
Structures project.

## Files

| File                          | Purpose                                              |
|--------------------------------|-------------------------------------------------------|
| `Node.java`                    | Generic doubly linked list node (data, next, prev)    |
| `StudyMaterial.java`           | Model class for one study material record             |
| `StudyMaterialLinkedList.java` | **Core data structure** — the custom linked list       |
| `StudyManagerGUI.java`         | Swing GUI (entry point, `main` method here)            |
| `LogicTest.java`               | Headless test proving the linked list logic works      |
| `StudyMaterialManager.jar`     | Prebuilt runnable jar                                  |

## How to Run (fastest — prebuilt jar)

```bash
java -jar StudyMaterialManager.jar
```

Requires Java 8+ installed (`java -version` to check).

## How to Build From Source

```bash
cd src
javac -d ../bin StudyMaterial.java Node.java StudyMaterialLinkedList.java StudyManagerGUI.java
cd ../bin
java StudyManagerGUI
```

Data is auto-saved to `study_materials.dat` in the working directory on every
add/edit/delete/toggle and on window close, and auto-loaded on startup — so
your data persists between runs.

## Data Structure — Why a Doubly Linked List

`StudyMaterialLinkedList` is a hand-built doubly linked list (`Node<T>` with
`next`/`prev` pointers, plus `head`/`tail`/`size` tracking) that implements:

- **Insertion**: `addFirst()`, `addLast()` — O(1) using head/tail pointers
- **Deletion**: `deleteById()` — O(n) traversal + O(1) unlink, handles
  head/tail/middle cases
- **Traversal**: `toList()` — walks `head → tail` via `next` pointers
- **Search**: `search(keyword)` — linear scan matching title/subject/type/description;
  `findById()`, `filterBySubject()`, `filterByStatus()`
- **Sorting**: `sortByPriority()`, `sortByTitle()`, `sortBySubject()`,
  `sortByDateAdded()` — in-place bubble sort that swaps node **payloads**
  (not pointers), a classic linked-list sorting technique
- **Persistence**: `saveToFile()` / `loadFromFile()` — serializes the list to
  disk with `ObjectOutputStream`/`ObjectInputStream`

## Features (from the GUI)

- Add / Edit / Delete study material
- Mark item Done / Pending
- Live keyword search (title, subject, type, description)
- Filter by subject (dropdown auto-populated from data)
- Sort by Priority, Title, Subject, or Date Added
- Status bar with total / completed / pending counts
- Auto-save + auto-load (no manual save button needed)

## Suggested Viva Talking Points

- Explain `Node<T>` and how `head`/`tail` pointers make `addLast` O(1).
- Walk through `deleteById` and the 3 unlink cases (head, tail, middle).
- Explain why bubble sort swaps `.data` instead of relinking nodes (simpler,
  avoids re-wiring prev/next pointers).
- Discuss time complexity: insert O(1), delete/search O(n), sort O(n²).
- Mention possible extensions: hash map index for O(1) `findById`, merge sort
  for O(n log n) sorting, undo/redo via a second linked list (stack).

## Tested

`LogicTest.java` verifies insert, search, filter, sort-by-priority,
mark-complete, delete, and save/load round-trip — all passing.
