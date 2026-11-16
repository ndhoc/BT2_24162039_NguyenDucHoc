package lt_web.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeBodyPart;

/**
 * Gửi email OTP qua SMTP. Khi chưa cấu hình mail.smtp.host, OTP được ghi vào
 * <catalina.base>/logs/otp-dev.log để tiện kiểm thử cục bộ.
 */
public final class MailSender {
    private static final Properties CONFIG = load();
    private static final Session SESSION = createSession();

    private MailSender() { }

    private static Properties load() {
        Properties config = new Properties();
        try (InputStream in = MailSender.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (in != null) config.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được mail.properties", e);
        }
        override(config, "mail.smtp.host", "MAIL_HOST");
        override(config, "mail.smtp.port", "MAIL_PORT");
        override(config, "mail.smtp.username", "MAIL_USERNAME");
        override(config, "mail.smtp.password", "MAIL_PASSWORD");
        override(config, "mail.smtp.from", "MAIL_FROM");
        override(config, "mail.smtp.starttls", "MAIL_STARTTLS");
        override(config, "mail.smtp.auth", "MAIL_AUTH");
        return config;
    }

    private static void override(Properties config, String key, String env) {
        String value = System.getenv(env);
        if (value != null && !value.isBlank()) config.setProperty(key, value.trim());
    }

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", CONFIG.getProperty("mail.smtp.host", ""));
        props.put("mail.smtp.port", CONFIG.getProperty("mail.smtp.port", "587"));
        props.put("mail.smtp.starttls.enable", CONFIG.getProperty("mail.smtp.starttls", "true"));
        props.put("mail.smtp.auth", CONFIG.getProperty("mail.smtp.auth", "true"));
        props.put("mail.smtp.connectiontimeout", CONFIG.getProperty("mail.smtp.timeout", "10000"));
        props.put("mail.smtp.timeout", CONFIG.getProperty("mail.smtp.timeout", "10000"));
        props.put("mail.mime.charset", "UTF-8");
        String username = CONFIG.getProperty("mail.smtp.username", "");
        String password = CONFIG.getProperty("mail.smtp.password", "");
        if (Boolean.parseBoolean(CONFIG.getProperty("mail.smtp.auth", "true"))) {
            return Session.getInstance(props, new Authenticator() {
                @Override protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        }
        return Session.getInstance(props);
    }

    public static boolean isConfigured() {
        return !CONFIG.getProperty("mail.smtp.host", "").isBlank();
    }

    public static void sendOtp(String to, String fullName, String code, String purpose) {
        String subject = "ACTIVATION".equals(purpose)
                ? "Kích hoạt tài khoản Shop 24162039"
                : "Đặt lại mật khẩu Shop 24162039";
        String action = "ACTIVATION".equals(purpose) ? "kích hoạt tài khoản" : "đặt lại mật khẩu";
        if (!isConfigured()) {
            writeDevLog(to, code, purpose);
            return;
        }
        try {
            MimeMessage message = new MimeMessage(SESSION);
            String from = CONFIG.getProperty("mail.smtp.from", "no-reply@shop24162039.local");
            message.setFrom(new InternetAddress(from, "Shop 24162039", "UTF-8"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            message.setSubject(subject, "UTF-8");
            MimeMultipart body = new MimeMultipart("alternative");
            MimeBodyPart plain = new MimeBodyPart();
            plain.setText(text(fullName, code, action), "UTF-8");
            MimeBodyPart rich = new MimeBodyPart();
            rich.setContent(html(fullName, code, action), "text/html; charset=UTF-8");
            body.addBodyPart(plain);
            body.addBodyPart(rich);
            message.setContent(body);
            Transport.send(message);
        } catch (MessagingException | IOException e) {
            throw new IllegalStateException("Không gửi được email OTP. Vui lòng thử lại.", e);
        }
    }

    private static String text(String fullName, String code, String action) {
        return "Xin chào " + fullName + ",\n\n"
                + "Mã OTP để " + action + " của bạn là: " + code + "\n"
                + "Mã có hiệu lực trong 10 phút và chỉ dùng được một lần.\n\n"
                + "Nếu bạn không yêu cầu thao tác này, hãy bỏ qua email.\n"
                + "Shop 24162039";
    }

    private static String html(String fullName, String code, String action) {
        return "<div style=\"font-family:Segoe UI,Arial,sans-serif;color:#1e293b\">"
                + "<h2 style=\"color:#1d4ed8\">Shop 24162039</h2>"
                + "<p>Xin chào <strong>" + escape(fullName) + "</strong>,</p>"
                + "<p>Mã OTP để " + action + " của bạn là:</p>"
                + "<p style=\"font-size:2rem;font-weight:800;letter-spacing:.35em;color:#0f172a\">" + code + "</p>"
                + "<p>Mã có hiệu lực trong <strong>10 phút</strong> và chỉ dùng được một lần.</p>"
                + "<p style=\"color:#64748b\">Nếu bạn không yêu cầu thao tác này, hãy bỏ qua email.</p>"
                + "</div>";
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static void writeDevLog(String to, String code, String purpose) {
        String line = LocalDateTime.now() + " | " + purpose + " | " + to + " | " + code;
        System.out.println("[OTP-DEV] " + line);
        try {
            String base = System.getProperty("catalina.base");
            Path directory = base != null && !base.isBlank()
                    ? Path.of(base, "logs")
                    : Path.of(System.getProperty("java.io.tmpdir"), "shop24162039-logs");
            Files.createDirectories(directory);
            try (OutputStream out = Files.newOutputStream(directory.resolve("otp-dev.log"),
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                 PrintWriter writer = new PrintWriter(out)) {
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("[OTP-DEV] Không ghi được otp-dev.log: " + e.getMessage());
        }
    }
}
