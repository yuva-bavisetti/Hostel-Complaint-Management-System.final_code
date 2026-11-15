import java.util.Arrays;
import java.util.List;

/** Automated smoke tests, run from the separate test_workspace folder. */
public class ProjectSelfTest {
    private static int count = 0;

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError("FAILED: " + description);
        count++;
        System.out.println("PASS: " + description);
    }

    public static void main(String[] args) throws Exception {
        check(AuthService.authenticate(AuthService.Role.STUDENT, "v", new char[]{'v'}) != null,
                "student login succeeds");
        check(AuthService.authenticate(AuthService.Role.STUDENT, "v", new char[]{'n', 'o'}) == null,
                "bad student password rejected");
        check(AuthService.authenticate(AuthService.Role.ADMIN, "admin", "admin123".toCharArray()) != null,
                "admin login succeeds");
        check(AuthService.authenticate(AuthService.Role.STUDENT, "admin", "admin123".toCharArray()) == null,
                "admin credentials cannot log in as student");
        check(AuthService.authenticate(AuthService.Role.ADMIN, "v", new char[]{'v'}) == null,
                "student credentials cannot log in as admin");

        ComplaintStore.Entry a = ComplaintStore.create("v", "Room", "A-101",
                "Fan", "Fan does not rotate");
        ComplaintStore.Entry b = ComplaintStore.create("saana", "Washroom", "W-02",
                "Tap", "Tap is leaking");
        check(!a.id.equals(b.id), "unique complaint IDs");
        check("Pending".equals(a.status) && "Pending".equals(b.status),
                "new complaints are pending");
        List<ComplaintStore.Entry> loaded = ComplaintStore.all();
        check(loaded.stream().anyMatch(e -> e.id.equals(a.id)), "complaints load from disk");
        check(loaded.stream().anyMatch(e -> e.id.equals(b.id)), "multiple students stored");
        ComplaintStore.updateStatus(a, "In Progress");
        check(ComplaintStore.all().stream().anyMatch(
                e -> e.id.equals(a.id) && "In Progress".equals(e.status)),
                "status In Progress persists");
        ComplaintStore.updateStatus(a, "Resolved");
        check(ComplaintStore.all().stream().anyMatch(
                e -> e.id.equals(a.id) && "Resolved".equals(e.status)),
                "status Resolved persists");
        boolean rejected = false;
        try { ComplaintStore.updateStatus(a, "Invalid"); }
        catch (IllegalArgumentException expected) { rejected = true; }
        check(rejected, "invalid status rejected");
        check("v".equals(a.username), "student identity stored with complaint");
        System.out.println("\nALL " + count + " TESTS PASSED");
    }
}
