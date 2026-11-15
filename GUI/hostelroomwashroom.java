import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Student dashboard: complaint submission and read-only personal complaint tracking. */
public class hostelroomwashroom extends JFrame {
    private final String currentUser;
    private final JTabbedPane categories = new JTabbedPane();
    private final JTextField locationField = new JTextField(22);
    private final JTextArea descriptionArea = new JTextArea(3, 44);

    public hostelroomwashroom() { this("DemoUser"); }

    public hostelroomwashroom(String username) {
        super("Student Dashboard - Hostel Complaint Management System");
        currentUser = username;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBorder(BorderFactory.createEmptyBorder(12, 12, 2, 12));
        JLabel heading = new JLabel("STUDENT DASHBOARD  |  Logged in: " + currentUser);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 16f));
        top.add(heading, BorderLayout.CENTER);
        JButton logout = new JButton("Log Out");
        logout.addActionListener(e -> { dispose(); new login_gui(); });
        top.add(logout, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        categories.addTab("Hostel", new Hostel_panel());
        categories.addTab("Room", new Room_panel());
        categories.addTab("Washroom", new Washroom_panel());
        add(categories, BorderLayout.CENTER);

        JPanel form = new JPanel(new BorderLayout(8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Submit a new complaint"));
        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        locationPanel.add(new JLabel("Room / Wing / Washroom number:"));
        locationPanel.add(locationField);
        form.add(locationPanel, BorderLayout.NORTH);

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JPanel details = new JPanel(new BorderLayout(6, 6));
        details.add(new JLabel("Additional details (optional):"), BorderLayout.NORTH);
        details.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        form.add(details, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submit = new JButton("Submit Complaint");
        JButton history = new JButton("View My Complaints");
        submit.addActionListener(e -> submitComplaint());
        history.addActionListener(e -> viewMyComplaints());
        actions.add(history);
        actions.add(submit);
        form.add(actions, BorderLayout.SOUTH);
        add(form, BorderLayout.SOUTH);

        setSize(940, 680);
        setMinimumSize(new Dimension(750, 560));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void submitComplaint() {
        String location = locationField.getText().trim();
        String description = descriptionArea.getText().trim();
        String category = categories.getTitleAt(categories.getSelectedIndex());

        List<String> chosenIssues = new ArrayList<>();
        collectVisibleSelections(categories.getSelectedComponent(), chosenIssues);
        if (location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a room, wing or washroom number.");
            return;
        }
        if (chosenIssues.isEmpty() && description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select a complaint checkbox or enter a description first.");
            return;
        }
        String issue = chosenIssues.isEmpty() ? "Other / General" : String.join(", ", chosenIssues);
        try {
            ComplaintStore.Entry saved = ComplaintStore.create(
                    currentUser, category, location, issue, description);
            JOptionPane.showMessageDialog(this,
                    "Complaint submitted successfully!\n\nID: " + saved.id
                    + "\nCategory: " + saved.category + "\nStatus: " + saved.status);
            locationField.setText("");
            descriptionArea.setText("");
            clearVisibleFields(categories.getSelectedComponent());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save complaint:\n" + ex.getMessage(),
                    "Storage Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void collectVisibleSelections(Component component, List<String> results) {
        if (!component.isVisible()) return;
        if (component instanceof JCheckBox) {
            JCheckBox box = (JCheckBox) component;
            if (box.isSelected()) results.add(box.getText().trim());
        } else if (component instanceof JTextField) {
            String customText = ((JTextField) component).getText().trim();
            if (!customText.isEmpty()) results.add(customText);
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                collectVisibleSelections(child, results);
            }
        }
    }

    private void clearVisibleFields(Component component) {
        if (!component.isVisible()) return;
        if (component instanceof JCheckBox) {
            ((JCheckBox) component).setSelected(false);
        } else if (component instanceof JTextField) {
            ((JTextField) component).setText("");
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                clearVisibleFields(child);
            }
        }
    }

    private void viewMyComplaints() {
        JDialog dialog = new JDialog(this, "My Complaints - " + currentUser, true);
        dialog.setLayout(new BorderLayout(8, 8));
        JLabel info = new JLabel("  Your submitted complaints (read-only). Admin updates appear after Refresh.");
        dialog.add(info, BorderLayout.NORTH);

        String[] columns = {"Complaint ID", "Submitted", "Category", "Location", "Issue", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {245, 165, 100, 110, 260, 120};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        List<ComplaintStore.Entry> mine = new ArrayList<>();
        Runnable refresh = () -> {
            try {
                mine.clear();
                model.setRowCount(0);
                for (ComplaintStore.Entry entry : ComplaintStore.all()) {
                    if (!currentUser.equals(entry.username)) continue;
                    mine.add(entry);
                    model.addRow(new Object[]{entry.id, entry.submitted, entry.category,
                            entry.location, entry.issue, entry.status});
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog, "Unable to read complaints:\n" + ex.getMessage(),
                        "Storage Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        refresh.run();

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton details = new JButton("View Details");
        JButton reload = new JButton("Refresh");
        JButton close = new JButton("Close");
        details.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Select one complaint first.");
                return;
            }
            ComplaintStore.Entry item = mine.get(row);
            JOptionPane.showMessageDialog(dialog, "ID: " + item.id + "\nStudent: " + item.username
                    + "\nCategory: " + item.category + "\nLocation: " + item.location
                    + "\nIssue: " + item.issue + "\nDetails: " + item.description
                    + "\nStatus: " + item.status);
        });
        reload.addActionListener(e -> refresh.run());
        close.addActionListener(e -> dialog.dispose());
        actions.add(details);
        actions.add(reload);
        actions.add(close);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setSize(890, 470);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
