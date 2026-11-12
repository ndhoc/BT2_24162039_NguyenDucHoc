package lt_web.dao.impl;

import java.time.Instant;

import lt_web.dao.DataAccessException;
import lt_web.dao.UserDao;
import lt_web.model.RememberToken;
import lt_web.model.User;
import lt_web.utils.JpaUtil;

public class UserDaoImpl implements UserDao {

    @Override
    public User get(String username) {
        return JpaUtil.read(em -> em.createQuery(
                        "select u from AppUser u where u.username = :username", User.class)
                .setParameter("username", username)
                .getResultStream().findFirst().orElse(null));
    }

    @Override
    public void insert(User user) {
        JpaUtil.write(em -> em.persist(user));
    }

    private boolean exists(String jpql, String value) {
        return JpaUtil.read(em -> em.createQuery(jpql, Long.class)
                .setParameter("value", value)
                .getSingleResult() > 0);
    }

    @Override
    public boolean checkExistUsername(String value) {
        return exists("select count(u) from AppUser u where u.username = :value", value);
    }

    @Override
    public boolean checkExistEmail(String value) {
        return exists("select count(u) from AppUser u where u.email = :value", value);
    }

    @Override
    public boolean checkExistPhone(String value) {
        return exists("select count(u) from AppUser u where u.phone = :value", value);
    }

    @Override
    public void saveRememberToken(int userId, String tokenHash, Instant expires) {
        JpaUtil.write(em -> em.persist(new RememberToken(tokenHash, em.getReference(User.class, userId), expires)));
    }

    @Override
    public User findByRememberToken(String tokenHash) {
        return JpaUtil.read(em -> em.createQuery(
                        "select t.user from RememberToken t where t.tokenHash = :hash and t.expiresAt > :now", User.class)
                .setParameter("hash", tokenHash)
                .setParameter("now", Instant.now())
                .getResultStream().findFirst().orElse(null));
    }

    @Override
    public void deleteRememberToken(String tokenHash) {
        JpaUtil.write(em -> em.createQuery("delete from RememberToken t where t.tokenHash = :hash")
                .setParameter("hash", tokenHash)
                .executeUpdate());
    }

    @Override
    public long count() {
        return JpaUtil.read(em -> em.createQuery("select count(u) from AppUser u", Long.class).getSingleResult());
    }
}
