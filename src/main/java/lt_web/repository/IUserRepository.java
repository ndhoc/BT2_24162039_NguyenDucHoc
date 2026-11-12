package lt_web.repository;

import java.time.Instant;

import lt_web.entity.User;

public interface IUserRepository {
    User get(String username);
    void insert(User user);
    boolean checkExistUsername(String username);
    boolean checkExistEmail(String email);
    boolean checkExistPhone(String phone);
    void saveRememberToken(int userId, String tokenHash, Instant expires);
    User findByRememberToken(String tokenHash);
    void deleteRememberToken(String tokenHash);
    long count();
}
