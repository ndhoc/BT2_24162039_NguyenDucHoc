package lt_web.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/** Mã OTP 6 chữ số; chỉ bản băm SHA-256 được lưu trong cơ sở dữ liệu. */
public final class OtpCodes {
    private static final SecureRandom RANDOM = new SecureRandom();

    private OtpCodes() { }

    public static String generate() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    public static String hash(String code, String purpose, int userId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] value = (purpose + ":" + userId + ":" + code).getBytes(StandardCharsets.UTF_8);
            return HexFormat.of().formatHex(digest.digest(value));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean matches(String code, String hash, String purpose, int userId) {
        if (code == null || hash == null) return false;
        byte[] expected = hash.getBytes(StandardCharsets.UTF_8);
        byte[] actual = hash(code.trim(), purpose, userId).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expected, actual);
    }
}
