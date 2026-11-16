package lt_web.repository.impl;

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
            em.createQuery("delete from OtpToken t where t.user.id = :userId and t.purpose = :purpose")
                    .setParameter("userId", userId)
                    .setParameter("purpose", token.getPurpose())
                    .executeUpdate();
            token.setUser(em.getReference(User.class, userId));
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
}
