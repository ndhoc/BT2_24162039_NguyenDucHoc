package lt_web.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.servlet.http.Part;
import lt_web.validation.FormValidation.FieldError;

public class CloudinaryAvatarStorage {
    private static final Logger LOG = Logger.getLogger(CloudinaryAvatarStorage.class.getName());
    private static final String AVATAR_PREFIX = "shop-24162039/profiles/";
    private final Cloudinary client;

    public record Avatar(String url, String publicId) { }

    public CloudinaryAvatarStorage() {
        client = configuredClient();
    }

    public Avatar upload(Part file) throws IOException {
        if (file == null || file.getSize() == 0) return null;
        ImageStorage.readUpload(file, "avatar");
        if (client == null)
            throw new FieldError("avatar", "Chưa cấu hình Cloudinary để tải ảnh đại diện.");

        String publicId = AVATAR_PREFIX + UUID.randomUUID();
        try (InputStream input = file.getInputStream()) {
            Map<?, ?> result = client.uploader().upload(input.readAllBytes(), ObjectUtils.asMap(
                    "resource_type", "image", "public_id", publicId, "overwrite", false));
            Object secureUrl = result.get("secure_url");
            if (secureUrl instanceof String url && publicId.equals(result.get("public_id"))
                    && url.length() <= 255 && isHttpsUrl(url))
                return new Avatar(url, publicId);
        } catch (IOException | RuntimeException e) {
            discard(publicId);
            throw new FieldError("avatar", "Không tải được ảnh lên Cloudinary. Vui lòng thử lại.");
        }
        discard(publicId);
        throw new FieldError("avatar", "Cloudinary không trả về đường dẫn ảnh hợp lệ.");
    }

    public void discard(String publicId) {
        if (client == null || publicId == null || !publicId.startsWith(AVATAR_PREFIX)) return;
        try {
            client.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image", "invalidate", true));
        } catch (IOException | RuntimeException e) {
            LOG.warning("Could not remove the obsolete Cloudinary profile image.");
        }
    }

    private static boolean isHttpsUrl(String value) {
        URI url = URI.create(value);
        return "https".equalsIgnoreCase(url.getScheme()) && url.getHost() != null && url.getUserInfo() == null;
    }

    private static Cloudinary configuredClient() {
        try {
            String url = configuredUrl();
            if (url == null || url.isBlank()) return null;
            Cloudinary cloudinary = new Cloudinary(url.trim());
            cloudinary.config.timeout = 10;
            return cloudinary;
        } catch (IOException | RuntimeException e) {
            LOG.warning("Could not read Cloudinary configuration.");
            return null;
        }
    }

    private static String configuredUrl() throws IOException {
        try (InputStream in = CloudinaryAvatarStorage.class.getClassLoader()
                .getResourceAsStream("cloudinary.properties")) {
            if (in != null) {
                String url = readUrl(new InputStreamReader(in, StandardCharsets.UTF_8));
                if (!url.isEmpty()) return url;
            }
        }

        Path file = Path.of(System.getProperty("catalina.base", "."), "conf", "cloudinary.properties");
        if (Files.isRegularFile(file)) {
            try (var reader = Files.newBufferedReader(file)) {
                String url = readUrl(reader);
                if (!url.isEmpty()) return url;
            }
        }
        return System.getenv("CLOUDINARY_URL");
    }

    private static String readUrl(Reader reader) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);
        return properties.getProperty("cloudinary.url", "").trim();
    }
}
