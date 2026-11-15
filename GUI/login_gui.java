import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/** One login window, with separate Student and Admin roles. */
public class login_gui extends JFrame {
    private final JComboBox<AuthService.Role> roleBox =
            new JComboBox<>(AuthService.Role.values());
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JLabel demoHint = new JLabel();

    public login_gui() {
        super("Hostel Complaint Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel heading = new JPanel(new GridLayout(2, 1, 2, 3));
        heading.setBorder(BorderFactory.createEmptyBorder(18, 25, 5, 25));
        JLabel title = new JLabel("HOSTEL COMPLAINT MANAGEMENT", JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 17f));
        heading.add(title);
        heading.add(new JLabel("Local desktop demonstration | Choose a role", JLabel.CENTER));
        add(heading, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBorder(BorderFactory.createEmptyBorder(14, 26, 10, 26));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7, 6, 7, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        addRow(fields, c, "Login as:", roleBox);
        c.gridy = 1;
        addRow(fields, c, "Username:", usernameField);
        c.gridy = 2;
        addRow(fields, c, "Password:", passwordField);
        add(fields, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(6, 10));
        footer.setBorder(BorderFactory.createEmptyBorder(0, 28, 22, 28));
        footer.add(demoHint, BorderLayout.NORTH);
        JButton loginButton = new JButton("Log In");
        loginButton.setPreferredSize(new Dimension(150, 35));
        footer.add(loginButton, BorderLayout.SOUTH);
        add(footer, BorderLayout.SOUTH);

        roleBox.addActionListener(e -> updateDemoHint());
        loginButton.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
        getRootPane().setDefaultButton(loginButton);
        updateDemoHint();
        setSize(600, 355);
        setMinimumSize(new Dimension(570, 320));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addRow(JPanel target, GridBagConstraints c, String label, JComponent field) {
        c.gridx = 0;
        c.weightx = 0;
        target.add(new JLabel(label), c);
        c.gridx = 1;
        c.weightx = 1;
        target.add(field, c);
    }

    private void updateDemoHint() {
        demoHint.setText(roleBox.getSelectedItem() == AuthService.Role.ADMIN
                ? "Demo admin: admin / admin123"
                : "Student: IIT2025100 - IIT2025200 | Password: last 3 digits");
    }

    private void attemptLogin() {
        AuthService.Role selectedRole = (AuthService.Role) roleBox.getSelectedItem();
        char[] password = passwordField.getPassword();
        AuthService.Session session = AuthService.authenticate(
                selectedRole, usernameField.getText(), password);
        Arrays.fill(password, '\0');
        if (session == null) {
            JOptionPane.showMessageDialog(this, "Incorrect role, username or password.",
                    "Login Failed", JOptionPane.WARNING_MESSAGE);
            passwordField.setText("");
            return;
        }
        dispose();
        if (session.getRole() == AuthService.Role.ADMIN) {
            new AdminDashboard(session.getUsername());
        } else {
            new hostelroomwashroom(session.getUsername());
        }
    }
}
