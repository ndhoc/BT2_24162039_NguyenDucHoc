package lt_web.service;

import lt_web.entity.User;

public class AuthService {

    private final UserService users = new lt_web.service.impl.UserServiceImpl();

    /**
     * @throws IllegalArgumentException khi thông tin đăng nhập sai
     * @throws IllegalStateException khi tài khoản chưa kích hoạt bằng OTP
     */
    public User authenticate(String username, String password) {
        return users.login(username, password);
    }

    /** Tài khoản tồn tại nhưng chưa kích hoạt, dùng để mở lại trang nhập OTP. */
    public User findPending(String username) {
        if (username == null || username.isBlank()) return null;
        User user = users.get(username.trim());
        return user != null && !user.isActive() ? user : null;
    }
}
