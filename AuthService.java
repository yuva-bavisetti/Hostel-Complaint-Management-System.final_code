import java.util.Arrays;

/** Demo-only role authentication. Do not use predictable passwords for real accounts. */
public final class AuthService {
    public enum Role {
        STUDENT("Student"), ADMIN("Admin");

        private final String label;
        Role(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    /** Information about the successfully signed-in user. */
    public static final class Session {
        private final String username;
        private final Role role;
        private Session(String username, Role role) {
            this.username = username;
            this.role = role;
        }
        public String getUsername() { return username; }
        public Role getRole() { return role; }
    }

    private AuthService() { }

    public static Session authenticate(Role role, String username, char[] password) {
        if (role == null || username == null || password == null) return null;
        String name = username.trim();

        if (role == Role.ADMIN) {
            char[] expected = "admin123".toCharArray();
            boolean valid = "admin".equals(name) && Arrays.equals(password, expected);
            Arrays.fill(expected, '\0');
            return valid ? new Session(name, Role.ADMIN) : null;
        }

        // Only IIT2025100 through IIT2025200 (inclusive) are accepted.
        // Example: username IIT2025147 must have password 147.
        if (!name.matches("IIT2025(1[0-9]{2}|200)")) return null;
        char[] expected = name.substring(name.length() - 3).toCharArray();
        boolean valid = Arrays.equals(password, expected);
        Arrays.fill(expected, '\0');
        return valid ? new Session(name, Role.STUDENT) : null;
    }
}
