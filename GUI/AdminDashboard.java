import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Local administrator view: all student complaints, searches and status updates. */
public final class AdminDashboard extends JFrame {
    private final String adminUsername;
    private final String[] columns = {"Complaint ID", "Submitted", "Student", "Category",
            "Location", "Issue", "Status"};
    private final DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField searchField = new JTextField(19);
    private final JComboBox<String> statusFilter = new JComboBox<>(
            new String[]{"All statuses", "Pending", "In Progress", "Resolved"});
    private final JLabel totals = new JLabel(" ");
    private final List<ComplaintStore.Entry> allEntries = new ArrayList<>();
    private final List<ComplaintStore.Entry> visibleEntries = new ArrayList<>();

    public AdminDashboard(String username) {
        super("Admin Dashboard - Hostel Complaint Management System");
        adminUsername = username;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBorder(BorderFactory.createEmptyBorder(14, 14, 5, 14));
        JLabel heading = new JLabel("ADMIN DASHBOARD  |  Logged in: " + adminUsername);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 17f));
        top.add(heading, BorderLayout.CENTER);
        JButton logout = new JButton("Log Out");
        logout.addActionListener(e -> { dispose(); new login_gui(); });
        top.add(logout, BorderLayout.EAST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 9));
        filters.add(new JLabel("Search:"));
        filters.add(searchField);
        filters.add(new JLabel("Status:"));
        filters.add(statusFilter);
        filters.add(totals);
        top.add(filters, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {250, 160, 120, 95, 110, 310, 120};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(4, 4));
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 10, 12, 10));
        bottom.add(new JLabel("  Select a row, then view its details or update its status."), BorderLayout.NORTH);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton details = new JButton("View Details");
        JButton pending = new JButton("Mark Pending");
        JButton progress = new JButton("Mark In Progress");
        JButton resolved = new JButton("Mark Resolved");
        JButton refresh = new JButton("Refresh");
        buttons.add(details);
        buttons.add(pending);
        buttons.add(progress);
        buttons.add(resolved);
        buttons.add(refresh);
        bottom.add(buttons, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        details.addActionListener(e -> showDetails());
        pending.addActionListener(e -> updateSelected("Pending"));
        progress.addActionListener(e -> updateSelected("In Progress"));
        resolved.addActionListener(e -> updateSelected("Resolved"));
        refresh.addActionListener(e -> reload());
        statusFilter.addActionListener(e -> filterView());
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterView(); }
            public void removeUpdate(DocumentEvent e) { filterView(); }
            public void changedUpdate(DocumentEvent e) { filterView(); }
        });

        setSize(1070, 640);
        setMinimumSize(new Dimension(820, 490));
        setLocationRelativeTo(null);
        reload();
        setVisible(true);
    }

    private void reload() {
        try {
            allEntries.clear();
            allEntries.addAll(ComplaintStore.all());
            filterView();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unable to load complaint records:\n" + ex.getMessage(),
                    "Storage Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterView() {
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        String filter = (String) statusFilter.getSelectedItem();
        visibleEntries.clear();
        model.setRowCount(0);
        for (ComplaintStore.Entry e : allEntries) {
            if (!"All statuses".equals(filter) && !filter.equals(e.status)) continue;
            String searchable = (e.id + " " + e.username + " " + e.category + " "
                    + e.location + " " + e.issue + " " + e.description).toLowerCase(Locale.ROOT);
            if (!searchable.contains(query)) continue;
            visibleEntries.add(e);
            model.addRow(new Object[]{e.id, e.submitted, e.username,
                    e.category, e.location, e.issue, e.status});
        }
        int pending = 0, inProgress = 0, resolved = 0;
        for (ComplaintStore.Entry e : allEntries) {
            if ("Pending".equals(e.status)) pending++;
            else if ("In Progress".equals(e.status)) inProgress++;
            else if ("Resolved".equals(e.status)) resolved++;
        }
        totals.setText("Total: " + allEntries.size() + "   Pending: " + pending
                + "   In Progress: " + inProgress + "   Resolved: " + resolved);
    }

    private ComplaintStore.Entry selectedComplaint() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select one complaint in the table.");
            return null;
        }
        return visibleEntries.get(row);
    }

    private void showDetails() {
        ComplaintStore.Entry e = selectedComplaint();
        if (e == null) return;
        JTextArea details = new JTextArea(
                "Complaint ID: " + e.id + "\nSubmitted: " + e.submitted
                + "\nStudent: " + e.username + "\nCategory: " + e.category
                + "\nLocation: " + e.location + "\nIssue: " + e.issue
                + "\nStatus: " + e.status + "\n\nDescription:\n" + e.description, 12, 48);
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, new JScrollPane(details), "Complaint Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateSelected(String newStatus) {
        ComplaintStore.Entry e = selectedComplaint();
        if (e == null) return;
        try {
            ComplaintStore.updateStatus(e, newStatus);
            reload();
            JOptionPane.showMessageDialog(this,
                    "Complaint " + e.id + " updated to: " + newStatus);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unable to update complaint:\n" + ex.getMessage(),
                    "Storage Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
