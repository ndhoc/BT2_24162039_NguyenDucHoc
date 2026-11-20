package lt_web.utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import jakarta.servlet.http.Part;
import lt_web.validation.FormValidation.FieldError;

public final class ImageStorage {
    public static final long MAX_SIZE = 5L * 1024 * 1024;
    private static final String PRODUCT_PREFIX = "product-image/";

    private ImageStorage() { }

    public static Path directory() {
        return directory("app.upload.dir", "UPLOAD_DIR", "category");
    }

    public static Path productDirectory() {
        return directory("app.upload.product.dir", "UPLOAD_PRODUCT_DIR", "product");
    }

    private static Path directory(String property, String env, String folder) {
        String configured = System.getProperty(property, System.getenv(env));
        Path path = configured == null || configured.isBlank()
                ? Path.of(System.getProperty("catalina.base", "."), "uploads", folder) : Path.of(configured);
        return path.toAbsolutePath().normalize();
    }

    public static String save(Part part) throws IOException {
        return save(part, directory(), "icon");
    }

    public static String saveProduct(Part part) throws IOException {
        String filename = save(part, productDirectory(), "image");
        return filename == null ? null : PRODUCT_PREFIX + filename;
    }

    private static String save(Part part, Path directory, String field) throws IOException {
        BufferedImage image = readUpload(part, field);
        if (image == null) return null;
        String filename = UUID.randomUUID() + ".png";
        Path target = Files.createDirectories(directory).resolve(filename);
        try {
            if (!ImageIO.write(image, "png", target.toFile())) throw new IOException("PNG writer unavailable");
            return filename;
        } catch (IOException | RuntimeException e) {
            try { Files.deleteIfExists(target); }
            catch (IOException cleanupError) { e.addSuppressed(cleanupError); }
            throw e;
        }
    }

    public static BufferedImage readUpload(Part part, String field) throws IOException {
        if (part == null || part.getSize() == 0) return null;
        if (part.getSize() > MAX_SIZE) throw new FieldError(field, "Ảnh tối đa 5 MB.");
        // Read dimensions before decoding to reject oversized compressed images.
        try (InputStream input = part.getInputStream(); ImageInputStream stream = ImageIO.createImageInputStream(input)) {
            var readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) throw new FieldError(field, "Chỉ chấp nhận ảnh PNG hoặc JPEG hợp lệ.");
            var reader = readers.next();
            try {
                String format = reader.getFormatName();
                if (!format.equalsIgnoreCase("png") && !format.equalsIgnoreCase("jpeg"))
                    throw new FieldError(field, "Chỉ chấp nhận ảnh PNG hoặc JPEG.");
                reader.setInput(stream);
                if (reader.getWidth(0) > 4096 || reader.getHeight(0) > 4096)
                    throw new FieldError(field, "Kích thước ảnh tối đa 4096 × 4096 pixel.");
                return reader.read(0);
            } finally { reader.dispose(); }
        } catch (javax.imageio.IIOException e) {
            throw new FieldError(field, "Nội dung ảnh không hợp lệ hoặc bị hỏng.");
        }
    }

    public static boolean isStoredName(String filename) {
        return filename != null && filename.matches("[a-f0-9-]{36}\\.png");
    }

    public static void delete(String filename) throws IOException {
        if (isStoredName(filename)) Files.deleteIfExists(directory().resolve(filename));
    }

    public static void deleteProduct(String imagePath) throws IOException {
        if (imagePath == null || !imagePath.startsWith(PRODUCT_PREFIX)) return;
        String filename = imagePath.substring(PRODUCT_PREFIX.length());
        if (isStoredName(filename)) Files.deleteIfExists(productDirectory().resolve(filename));
    }
}
