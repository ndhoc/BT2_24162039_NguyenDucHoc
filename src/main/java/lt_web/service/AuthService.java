package lt_web.service;

import lt_web.entity.User;

public class AuthService {

    private final UserService users = new lt_web.service.impl.UserServiceImpl();

    public User authenticate(String username, String password) {
        return users.login(username, password);
    }
}
