import java.io.Serializable;

/**
 * Model class representing one Study Material record.
 * Implements Serializable so the custom linked list can be saved/loaded to disk.
 */
public class StudyMaterial implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String subject;
    private String type;       // Notes, Book, Video, Link, PDF, Assignment
    private String priority;   // High, Medium, Low
    private String dateAdded;
    private String description;
    private boolean completed;

    public StudyMaterial(int id, String title, String subject, String type,
                          String priority, String dateAdded, String description) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.type = type;
        this.priority = priority;
        this.dateAdded = dateAdded;
        this.description = description;
        this.completed = false;
    }

    // ---- Getters ----
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public String getType() { return type; }
    public String getPriority() { return priority; }
    public String getDateAdded() { return dateAdded; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }

    // ---- Setters (used when editing a record) ----
    public void setTitle(String title) { this.title = title; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setType(String type) { this.type = type; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setDescription(String description) { this.description = description; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    /** Priority rank used for sorting: High=1 (highest), Medium=2, Low=3 */
    public int priorityRank() {
        switch (priority.toUpperCase()) {
            case "HIGH": return 1;
            case "MEDIUM": return 2;
            default: return 3;
        }
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | Subject: %s | Type: %s | Priority: %s | %s | Added: %s",
                id, title, subject, type, priority,
                completed ? "DONE" : "PENDING", dateAdded);
    }
}
