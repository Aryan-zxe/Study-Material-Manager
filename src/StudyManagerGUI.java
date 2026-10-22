import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Study Material Manager - standalone Swing desktop app.
 * Backed by a custom-built doubly linked list (StudyMaterialLinkedList).
 */
public class StudyManagerGUI extends JFrame {

    private static final String DATA_FILE = "study_materials.dat";
    private final StudyMaterialLinkedList list = new StudyMaterialLinkedList();

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JComboBox<String> subjectFilterBox;
    private JLabel statusLabel;

    private static final String[] TYPES = {"Notes", "Book", "Video", "Link", "PDF", "Assignment"};
    private static final String[] PRIORITIES = {"High", "Medium", "Low"};
    private static final String[] COLUMNS = {"ID", "Title", "Subject", "Type", "Priority", "Status", "Date Added", "Description"};

    public StudyManagerGUI() {
        super("Study Material Manager");
        loadData();
        buildUI();
        refreshTable();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
                dispose();
                System.exit(0);
            }
        });
        setSize(1050, 620);
        setLocationRelativeTo(null);
    }

    // ---------------------------------------------------------------
    // UI CONSTRUCTION
    // ---------------------------------------------------------------

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JLabel title = new JLabel("Study Material Manager", SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        panel.add(title, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchField = new JTextField(18);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        subjectFilterBox = new JComboBox<>();
        subjectFilterBox.addItem("All Subjects");
        JButton filterBtn = new JButton("Filter");

        searchBtn.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());
        clearBtn.addActionListener(e -> { searchField.setText(""); refreshTable(); });
        filterBtn.addActionListener(e -> doSubjectFilter());

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);
        searchPanel.add(new JLabel("  Subject:"));
        searchPanel.add(subjectFilterBox);
        searchPanel.add(filterBtn);

        panel.add(searchPanel, BorderLayout.EAST);
        return panel;
    }

    private JScrollPane buildTablePanel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(35);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(7).setPreferredWidth(220);
        return new JScrollPane(table);
    }

    private JPanel buildBottomPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton toggleBtn = new JButton("Mark Done/Pending");
        JButton sortPriorityBtn = new JButton("Sort: Priority");
        JButton sortSubjectBtn = new JButton("Sort: Subject");
        JButton sortTitleBtn = new JButton("Sort: Title");
        JButton sortDateBtn = new JButton("Sort: Date");
        JButton refreshBtn = new JButton("Show All");

        addBtn.addActionListener(e -> showAddEditDialog(null));
        editBtn.addActionListener(e -> {
            StudyMaterial sel = getSelectedMaterial();
            if (sel != null) showAddEditDialog(sel);
            else warn("Select a row to edit.");
        });
        deleteBtn.addActionListener(e -> doDelete());
        toggleBtn.addActionListener(e -> doToggleStatus());
        sortPriorityBtn.addActionListener(e -> { list.sortByPriority(); refreshTable(); });
        sortSubjectBtn.addActionListener(e -> { list.sortBySubject(); refreshTable(); });
        sortTitleBtn.addActionListener(e -> { list.sortByTitle(); refreshTable(); });
        sortDateBtn.addActionListener(e -> { list.sortByDateAdded(); refreshTable(); });
        refreshBtn.addActionListener(e -> { searchField.setText(""); refreshTable(); });

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(toggleBtn);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPanel.add(sortPriorityBtn);
        buttonPanel.add(sortSubjectBtn);
        buttonPanel.add(sortTitleBtn);
        buttonPanel.add(sortDateBtn);
        buttonPanel.add(refreshBtn);

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));

        outer.add(buttonPanel, BorderLayout.NORTH);
        outer.add(statusLabel, BorderLayout.SOUTH);
        return outer;
    }

    // ---------------------------------------------------------------
    // ACTIONS
    // ---------------------------------------------------------------

    private void showAddEditDialog(StudyMaterial existing) {
        JTextField titleField = new JTextField(existing != null ? existing.getTitle() : "", 20);
        JTextField subjectField = new JTextField(existing != null ? existing.getSubject() : "", 20);
        JComboBox<String> typeBox = new JComboBox<>(TYPES);
        JComboBox<String> priorityBox = new JComboBox<>(PRIORITIES);
        JTextArea descArea = new JTextArea(existing != null ? existing.getDescription() : "", 4, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        if (existing != null) {
            typeBox.setSelectedItem(existing.getType());
            priorityBox.setSelectedItem(existing.getPriority());
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormRow(panel, gc, row++, "Title:", titleField);
        addFormRow(panel, gc, row++, "Subject:", subjectField);
        addFormRow(panel, gc, row++, "Type:", typeBox);
        addFormRow(panel, gc, row++, "Priority:", priorityBox);
        gc.gridx = 0; gc.gridy = row; gc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Description:"), gc);
        gc.gridx = 1; gc.gridy = row;
        panel.add(new JScrollPane(descArea), gc);

        String dialogTitle = existing != null ? "Edit Study Material" : "Add Study Material";
        int result = JOptionPane.showConfirmDialog(this, panel, dialogTitle,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String titleTxt = titleField.getText().trim();
            String subjectTxt = subjectField.getText().trim();
            if (titleTxt.isEmpty() || subjectTxt.isEmpty()) {
                warn("Title and Subject are required.");
                return;
            }
            String type = (String) typeBox.getSelectedItem();
            String priority = (String) priorityBox.getSelectedItem();
            String desc = descArea.getText().trim();

            if (existing != null) {
                existing.setTitle(titleTxt);
                existing.setSubject(subjectTxt);
                existing.setType(type);
                existing.setPriority(priority);
                existing.setDescription(desc);
            } else {
                String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                StudyMaterial m = new StudyMaterial(list.nextId(), titleTxt, subjectTxt,
                        type, priority, today, desc);
                list.addLast(m);
            }
            saveData();
            refreshTable();
        }
    }

    private void addFormRow(JPanel panel, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0;
        panel.add(new JLabel(label), gc);
        gc.gridx = 1; gc.gridy = row; gc.weightx = 1;
        panel.add(field, gc);
    }

    private void doDelete() {
        StudyMaterial sel = getSelectedMaterial();
        if (sel == null) { warn("Select a row to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete \"" + sel.getTitle() + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            list.deleteById(sel.getId());
            saveData();
            refreshTable();
        }
    }

    private void doToggleStatus() {
        StudyMaterial sel = getSelectedMaterial();
        if (sel == null) { warn("Select a row to update."); return; }
        sel.setCompleted(!sel.isCompleted());
        saveData();
        refreshTable();
    }

    private void doSearch() {
        String key = searchField.getText().trim();
        if (key.isEmpty()) { refreshTable(); return; }
        List<StudyMaterial> results = list.search(key);
        populateTable(results);
        statusLabel.setText("Found " + results.size() + " result(s) for \"" + key + "\"");
    }

    private void doSubjectFilter() {
        String selected = (String) subjectFilterBox.getSelectedItem();
        if (selected == null || selected.equals("All Subjects")) {
            refreshTable();
            return;
        }
        List<StudyMaterial> results = list.filterBySubject(selected);
        populateTable(results);
        statusLabel.setText("Showing subject: " + selected + " (" + results.size() + " item(s))");
    }

    private StudyMaterial getSelectedMaterial() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        return list.findById(id);
    }

    // ---------------------------------------------------------------
    // TABLE / DATA HELPERS
    // ---------------------------------------------------------------

    private void refreshTable() {
        populateTable(list.toList());
        refreshSubjectFilterBox();
        statusLabel.setText(String.format("Total: %d | Completed: %d | Pending: %d",
                list.size(), list.countCompleted(), list.countPending()));
    }

    private void populateTable(List<StudyMaterial> items) {
        tableModel.setRowCount(0);
        for (StudyMaterial m : items) {
            tableModel.addRow(new Object[]{
                    m.getId(), m.getTitle(), m.getSubject(), m.getType(),
                    m.getPriority(), m.isCompleted() ? "Done" : "Pending",
                    m.getDateAdded(), m.getDescription()
            });
        }
    }

    private void refreshSubjectFilterBox() {
        String currentSelection = (String) subjectFilterBox.getSelectedItem();
        subjectFilterBox.removeAllItems();
        subjectFilterBox.addItem("All Subjects");
        java.util.Set<String> subjects = new java.util.TreeSet<>();
        for (StudyMaterial m : list.toList()) subjects.add(m.getSubject());
        for (String s : subjects) subjectFilterBox.addItem(s);
        if (currentSelection != null) subjectFilterBox.setSelectedItem(currentSelection);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Notice", JOptionPane.WARNING_MESSAGE);
    }

    // ---------------------------------------------------------------
    // PERSISTENCE
    // ---------------------------------------------------------------

    private void loadData() {
        try {
            list.loadFromFile(DATA_FILE);
        } catch (Exception e) {
            // First run or corrupted file - start fresh
        }
    }

    private void saveData() {
        try {
            list.saveToFile(DATA_FILE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save data: " + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------------------------------------------------
    // ENTRY POINT
    // ---------------------------------------------------------------

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            StudyManagerGUI app = new StudyManagerGUI();
            app.setVisible(true);
        });
    }
}
