package lt_web.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lt_web.utils.VideoStorage;

/** Phục vụ video upload hoặc video demo đóng gói trong WAR, hỗ trợ Range để tua. */
@WebServlet("/video-file")
public class VideoFileServlet extends HttpServlet {
    private static final String DEMO_DIRECTORY = "/videos/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("fname");
        if (!VideoStorage.isValidName(name)) { resp.sendError(404); return; }
        VideoSource source = findSource(name);
        if (source == null) { resp.sendError(404); return; }

        resp.setContentType(VideoStorage.contentType(name));
        resp.setHeader("Accept-Ranges", "bytes");
        resp.setHeader("X-Content-Type-Options", "nosniff");

        long size = source.size();
        String range = req.getHeader("Range");
        long start = 0;
        long end = size - 1;
        if (range != null && range.startsWith("bytes=")) {
            String spec = range.substring(6).split(",")[0].trim();
            int dash = spec.indexOf('-');
            try {
                if (dash > 0) {
                    start = Long.parseLong(spec.substring(0, dash));
                    if (dash < spec.length() - 1) end = Long.parseLong(spec.substring(dash + 1));
                } else if (dash == 0 && spec.length() > 1) {
                    long suffix = Long.parseLong(spec.substring(1));
                    start = Math.max(0, size - suffix);
                }
            } catch (NumberFormatException ignored) {
                start = 0;
                end = size - 1;
            }
            if (start < 0 || start >= size || end < start) {
                resp.setHeader("Content-Range", "bytes */" + size);
                resp.sendError(416);
                return;
            }
            end = Math.min(end, size - 1);
            resp.setStatus(206);
            resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + size);
        }
        resp.setContentLengthLong(end - start + 1);
        try (InputStream in = source.open()) {
            in.skipNBytes(start);
            byte[] buffer = new byte[64 * 1024];
            long remaining = end - start + 1;
            var out = resp.getOutputStream();
            while (remaining > 0) {
                int read = in.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                if (read < 0) break;
                out.write(buffer, 0, read);
                remaining -= read;
            }
        }
    }

    /** File do người dùng upload được ưu tiên; WAR cung cấp dữ liệu demo khi clone repo mới. */
    private VideoSource findSource(String name) throws IOException {
        Path uploaded = VideoStorage.directory().resolve(name);
        if (Files.isRegularFile(uploaded)) return new VideoSource(uploaded, null);
        try (InputStream bundled = getServletContext().getResourceAsStream(DEMO_DIRECTORY + name)) {
            return bundled == null ? null : new VideoSource(null, bundled.readAllBytes());
        }
    }

    private static final class VideoSource {
        private final Path path;
        private final byte[] bundled;

        private VideoSource(Path path, byte[] bundled) {
            this.path = path;
            this.bundled = bundled;
        }

        private long size() throws IOException {
            return path != null ? Files.size(path) : bundled.length;
        }

        private InputStream open() throws IOException {
            return path != null ? Files.newInputStream(path) : new ByteArrayInputStream(bundled);
        }
    }
}
