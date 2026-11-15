import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Demonstration file-based complaint storage. Not a production database.
 * One human-readable .properties file is saved per complaint.
 */
public final class ComplaintStore {
    private static final Path DIR = Paths.get("complaints");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ComplaintStore() { }

    public static class Entry {
        public final String id;
        public final String submitted;
        public final String username;
        public final String category;
        public final String location;
        public final String issue;
        public final String description;
        public String status;

        Entry(Properties p) {
            id = p.getProperty("id", "");
            submitted = p.getProperty("submitted", "");
            username = p.getProperty("username", "");
            category = p.getProperty("category", "");
            location = p.getProperty("location", "");
            issue = p.getProperty("issue", "");
            description = p.getProperty("description", "");
            status = p.getProperty("status", "Pending");
        }

        Properties toProperties() {
            Properties p = new Properties();
            p.setProperty("id", id);
            p.setProperty("submitted", submitted);
            p.setProperty("username", username);
            p.setProperty("category", category);
            p.setProperty("location", location);
            p.setProperty("issue", issue);
            p.setProperty("description", description);
            p.setProperty("status", status);
            return p;
        }
    }

    public static Entry create(String username, String category, String location,
                               String issue, String description) throws IOException {
        Files.createDirectories(DIR);
        String id;
        do {
            id = "CMP-" + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + "-"
                    + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (Files.exists(fileFor(id)));

        Properties p = new Properties();
        p.setProperty("id", id);
        p.setProperty("submitted", LocalDateTime.now().format(DATE_FORMAT));
        p.setProperty("username", username);
        p.setProperty("category", category);
        p.setProperty("location", location);
        p.setProperty("issue", issue);
        p.setProperty("description", description);
        p.setProperty("status", "Pending");
        Entry entry = new Entry(p);
        save(entry);
        return entry;
    }

    public static List<Entry> all() throws IOException {
        Files.createDirectories(DIR);
        List<Entry> result = new ArrayList<>();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(DIR, "*.properties")) {
            for (Path file : files) {
                Properties p = new Properties();
                try (InputStream in = Files.newInputStream(file)) {
                    p.load(in);
                }
                result.add(new Entry(p));
            }
        }
        result.sort(Comparator.comparing((Entry e) -> e.submitted).reversed());
        return result;
    }

    public static void updateStatus(Entry entry, String newStatus) throws IOException {
        if (!"Pending".equals(newStatus) && !"In Progress".equals(newStatus)
                && !"Resolved".equals(newStatus)) {
            throw new IllegalArgumentException("Invalid complaint status");
        }
        if (!Files.exists(fileFor(entry.id))) {
            throw new IOException("Complaint file was not found");
        }
        entry.status = newStatus;
        save(entry);
    }

    private static void save(Entry entry) throws IOException {
        Files.createDirectories(DIR);
        try (OutputStream out = Files.newOutputStream(fileFor(entry.id))) {
            entry.toProperties().store(out, "Hostel Complaint Management - demo record");
        }
    }

    private static Path fileFor(String id) {
        if (!id.matches("CMP-[A-Za-z0-9-]+")) {
            throw new IllegalArgumentException("Invalid complaint ID");
        }
        return DIR.resolve(id + ".properties");
    }
}
