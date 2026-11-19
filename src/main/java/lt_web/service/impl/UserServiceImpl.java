package lt_web.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;

import lt_web.entity.User;
import lt_web.repository.DataAccessException;
import lt_web.repository.IUserRepository;
import lt_web.repository.impl.UserRepository;
import lt_web.service.UserService;
import lt_web.utils.PasswordUtils;
import lt_web.validation.FormValidation;
import lt_web.validation.FormValidation.FieldError;

public class UserServiceImpl implements UserService {
    private final IUserRepository repository = new UserRepository();

    @Override public User get(String username) { return repository.get(username); }
    @Override public User getById(int id) { return repository.getById(id); }
    @Override public User getByEmail(String email) {
        return email == null || email.isBlank() ? null : repository.getByEmail(email.trim());
    }
    @Override public java.util.List<User> all() { return repository.all(); }

    @Override public User login(String username, String password) {
        FormValidation.login(username, password);
        User user = repository.get(username.trim());
        if (user == null || !PasswordUtils.verify(password, user.getPassword()))
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không đúng.");
        if (!user.isActive())
            throw new IllegalStateException("Tài khoản chưa được kích hoạt. Vui lòng nhập mã OTP gửi tới email của bạn.");
        return user;
    }

    @Override public boolean checkExistUsername(String v) { return repository.checkExistUsername(v); }
    @Override public boolean checkExistEmail(String v) { return repository.checkExistEmail(v); }
    @Override public boolean checkExistPhone(String v) { return repository.checkExistPhone(v); }
    @Override public long count() { return repository.count(); }

    @Override public User register(String username, String password, String confirm, String email, String fullname, String phone) {
        username = FormValidation.username(username);
        email = FormValidation.email(email);
        fullname = FormValidation.requiredText("fullname", fullname, 100, "Họ tên");
        phone = FormValidation.phone(phone);
        FormValidation.newPassword(password, confirm);
        if (checkExistUsername(username)) throw new FieldError("username", "Tài khoản đã tồn tại.");
        if (checkExistEmail(email)) throw new FieldError("email", "Email đã tồn tại.");
        if (checkExistPhone(phone)) throw new FieldError("phone", "Số điện thoại đã tồn tại.");
        User user = new User(username, fullname);
        user.setEmail(email); user.setPhone(phone); user.setPassword(PasswordUtils.hash(password));
        user.setRoleid(5); user.setCreatedDate(LocalDate.now()); user.setActive(Boolean.FALSE);
        try { repository.insert(user); }
        catch (DataAccessException e) {
            if (isDuplicate(e)) throw new IllegalArgumentException("Tài khoản, email hoặc số điện thoại đã tồn tại.");
            throw e;
        }
        return user;
    }

    private static boolean isDuplicate(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sql && (sql.getErrorCode() == 2601 || sql.getErrorCode() == 2627))
                return true;
        }
        return false;
    }
}
