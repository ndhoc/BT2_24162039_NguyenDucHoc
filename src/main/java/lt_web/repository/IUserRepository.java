package lt_web.repository;

import java.time.Instant;
import java.util.List;

import lt_web.entity.User;

public interface IUserRepository {
    User get(String username);
    User getById(int id);
    User getByEmail(String email);
    List<User> all();
    void insert(User user);
    void update(User user);
    boolean checkExistUsername(String username);
    boolean checkExistEmail(String email);
    boolean checkExistPhone(String phone);
    void saveRememberToken(int userId, String tokenHash, Instant expires);
    User findByRememberToken(String tokenHash);
    void deleteRememberToken(String tokenHash);
    long count();
}
