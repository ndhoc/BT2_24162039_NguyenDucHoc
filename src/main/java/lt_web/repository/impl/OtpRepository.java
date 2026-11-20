package lt_web.repository.impl;

import java.time.Instant;

import jakarta.persistence.LockModeType;
import lt_web.entity.OtpToken;
import lt_web.entity.User;
import lt_web.repository.IOtpRepository;
import lt_web.repository.JpaConfig;

public class OtpRepository implements IOtpRepository {

    @Override
    public OtpToken find(int userId, String purpose) {
        return JpaConfig.read(em -> em.createQuery(
                        "select t from OtpToken t where t.user.id = :userId and t.purpose = :purpose", OtpToken.class)
                .setParameter("userId", userId)
                .setParameter("purpose", purpose)
                .getResultStream().findFirst().orElse(null));
    }

    /** Mỗi tài khoản chỉ giữ một mã cho mỗi mục đích; mã cũ bị thay thế. */
    @Override
    public void replace(OtpToken token) {
        int userId = token.getUser().getId();
        JpaConfig.write(em -> {
            User account = em.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
            if (account == null || !account.getEmail().equals(token.getUser().getEmail()))
                throw new IllegalArgumentException("Thông tin tài khoản đã thay đổi. Vui lòng yêu cầu mã OTP mới.");
            em.createQuery("delete from OtpToken t where t.user.id = :userId and t.purpose = :purpose")
                    .setParameter("userId", userId)
                    .setParameter("purpose", token.getPurpose())
                    .executeUpdate();
            token.setUser(account);
            em.persist(token);
        });
    }

    @Override
    public void delete(int userId, String purpose) {
        JpaConfig.write(em -> em.createQuery(
                        "delete from OtpToken t where t.user.id = :userId and t.purpose = :purpose")
                .setParameter("userId", userId)
                .setParameter("purpose", purpose)
                .executeUpdate());
    }

    @Override
    public void incrementAttempts(long id) {
        JpaConfig.write(em -> em.createQuery("update OtpToken t set t.attempts = t.attempts + 1 where t.id = :id")
                .setParameter("id", id)
                .executeUpdate());
    }

    @Override
    public boolean completePasswordReset(int userId, long tokenId, String passwordHash) {
        return JpaConfig.writeResult(em -> {
            User account = em.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
            OtpToken token = em.find(OtpToken.class, tokenId);
            if (account == null || token == null || token.getUser().getId() != userId
                    || !OtpToken.PURPOSE_RESET.equals(token.getPurpose()) || !token.getExpiresAt().isAfter(Instant.now()))
                return false;
            account.setPassword(passwordHash);
            account.setActive(Boolean.TRUE);
            em.remove(token);
            return true;
        });
    }
}
