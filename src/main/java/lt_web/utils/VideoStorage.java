package lt_web.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import jakarta.servlet.http.Part;

/** Lưu tệp video do người dùng tải lên bằng Multipart, kiểm tra chữ ký tệp. */
public final class VideoStorage {
    public static final long MAX_SIZE = 50L * 1024 * 1024;
    private static final String STORED_PATTERN = "[a-f0-9-]{36}\\.(mp4|webm|ogg)";

    private VideoStorage() { }

    public static Path directory() throws IOException {
        String configured = System.getProperty("app.upload.video.dir", System.getenv("UPLOAD_VIDEO_DIR"));
        Path path = configured == null || configured.isBlank()
                ? Path.of(System.getProperty("catalina.base", "."), "uploads", "video") : Path.of(configured);
        return Files.createDirectories(path.toAbsolutePath().normalize());
    }

    /**
     * Đọc chữ ký tệp để xác định định dạng thật, không tin vào phần mở rộng
     * hay Content-Type do trình duyệt gửi lên.
     */
    public static String save(Part part) throws IOException {
        if (part == null || part.getSize() == 0) return null;
        if (part.getSize() > MAX_SIZE) throw new IllegalArgumentException("Video tối đa 50 MB.");
        try (InputStream raw = part.getInputStream()) {
            java.io.BufferedInputStream input = new java.io.BufferedInputStream(raw);
            input.mark(16);
            byte[] head = new byte[12];
            int read = input.read(head);
            input.reset();
            String extension = extensionOf(head, read);
            if (extension == null)
                throw new IllegalArgumentException("Chỉ chấp nhận video MP4, WebM hoặc OGG hợp lệ.");
            String filename = UUID.randomUUID() + "." + extension;
            Path target = directory().resolve(filename);
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        }
    }

    private static String extensionOf(byte[] head, int length) {
        if (length >= 12 && head[4] == 'f' && head[5] == 't' && head[6] == 'y' && head[7] == 'p') return "mp4";
        if (length >= 4 && Byte.toUnsignedInt(head[0]) == 0x1A
                && Byte.toUnsignedInt(head[1]) == 0x45
                && Byte.toUnsignedInt(head[2]) == 0xDF
                && Byte.toUnsignedInt(head[3]) == 0xA3) return "webm";
        if (length >= 4 && head[0] == 'O' && head[1] == 'g' && head[2] == 'g' && head[3] == 'S') return "ogg";
        return null;
    }

    public static boolean isValidName(String filename) {
        return filename != null && filename.matches(STORED_PATTERN);
    }

    public static void delete(String filename) throws IOException {
        if (isValidName(filename)) Files.deleteIfExists(directory().resolve(filename));
    }

    public static String contentType(String filename) {
        if (filename == null) return "application/octet-stream";
        if (filename.endsWith(".webm")) return "video/webm";
        if (filename.endsWith(".ogg")) return "video/ogg";
        return "video/mp4";
    }
}
