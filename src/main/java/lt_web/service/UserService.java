package lt_web.service;

import java.util.List;

import lt_web.entity.User;

public interface UserService {
    User login(String username, String password);
    User get(String username);
    User getById(int id);
    User getByEmail(String email);
    List<User> all();
    User register(String username, String password, String confirm, String email, String fullname, String phone);
    boolean checkExistUsername(String username);
    boolean checkExistEmail(String email);
    boolean checkExistPhone(String phone);
    long count();
}
