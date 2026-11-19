package lt_web.validation;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class FormValidation {
    private static final Pattern EMAIL = Pattern.compile("[^\\s@]+@[^\\s@]+\\.[^\\s@]+");
    private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9_]{3,50}");
    private static final Pattern PHONE = Pattern.compile("[+]?[0-9]{9,15}");
    private static final Pattern PRICE = Pattern.compile("[0-9]{1,16}(?:[.,][0-9]{1,2})?");

    private FormValidation() { }

    public static void login(String username, String password) {
        requiredText("username", username, 50, "Tên đăng nhập");
        if (password == null || password.isEmpty() || password.length() > 128)
            throw new FieldError("password", "Vui lòng nhập mật khẩu tối đa 128 ký tự.");
    }

    public static String username(String value) {
        String username = clean(value);
        if (!USERNAME.matcher(username).matches())
            throw new FieldError("username", "Tài khoản gồm 3–50 chữ cái, chữ số hoặc dấu gạch dưới.");
        return username;
    }

    public static String email(String value) {
        String email = clean(value);
        if (email.isBlank()) throw new FieldError("email", "Vui lòng nhập email.");
        if (email.length() > 255 || !EMAIL.matcher(email).matches())
            throw new FieldError("email", "Email không hợp lệ.");
        return email;
    }

    public static String phone(String value) {
        String phone = clean(value);
        if (phone.length() > 15 || !PHONE.matcher(phone).matches())
            throw new FieldError("phone", "Nhập ít nhất 9 chữ số, tối đa 15 ký tự kể cả dấu + ở đầu.");
        return phone;
    }

    public static void newPassword(String password, String confirm) {
        if (password == null || password.isBlank() || password.length() < 6 || password.length() > 128)
            throw new FieldError("password", "Mật khẩu phải có 6–128 ký tự.");
        if (!password.equals(confirm))
            throw new FieldError("confirm", "Mật khẩu xác nhận không khớp.");
    }

    public static String requiredText(String field, String value, int max, String label) {
        String text = clean(value);
        if (text.isBlank() || text.length() > max)
            throw new FieldError(field, label + " phải có 1–" + max + " ký tự.");
        return text;
    }

    public static BigDecimal productPrice(String value) {
        String price = clean(value);
        if (!PRICE.matcher(price).matches())
            throw new FieldError("price", "Giá phải từ 0 trở lên, tối đa 16 chữ số nguyên và 2 chữ số thập phân.");
        return new BigDecimal(price.replace(',', '.'));
    }

    public static String description(String value) {
        String text = clean(value);
        if (text.length() > 1000) throw new FieldError("description", "Mô tả tối đa 1000 ký tự.");
        return text.isBlank() ? null : text;
    }

    public static int positiveId(String field, String value, String message) {
        try {
            int id = Integer.parseInt(value);
            if (id > 0) return id;
        } catch (NumberFormatException ignored) { }
        throw new FieldError(field, message);
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    public static final class FieldError extends IllegalArgumentException {
        private final String field;

        public FieldError(String field, String message) {
            super(message);
            this.field = field;
        }

        public String field() { return field; }
    }
}
