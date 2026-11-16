package lt_web.repository;

import lt_web.entity.OtpToken;

public interface IOtpRepository {
    OtpToken find(int userId, String purpose);
    void replace(OtpToken token);
    void delete(int userId, String purpose);
    void incrementAttempts(long id);
}
