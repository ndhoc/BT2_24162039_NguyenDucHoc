package lt_web.utils;

import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import lt_web.dao.DataAccessException;

/** Shared JPA bootstrap: one EntityManagerFactory per web application. */
public final class JpaUtil {
    private static final String UNIT = "jpa-hibernate-mysql";
    private static volatile EntityManagerFactory factory;

    private JpaUtil() { }

    private static EntityManagerFactory factory() {
        EntityManagerFactory current = factory;
        if (current == null || !current.isOpen()) {
            synchronized (JpaUtil.class) {
                current = factory;
                if (current == null || !current.isOpen()) {
                    try {
                        current = Persistence.createEntityManagerFactory(UNIT);
                    } catch (RuntimeException e) {
                        throw new DataAccessException(e);
                    }
                    factory = current;
                }
            }
        }
        return current;
    }

    public static <T> T read(Function<EntityManager, T> work) {
        try (EntityManager em = factory().createEntityManager()) {
            return work.apply(em);
        } catch (DataAccessException | IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new DataAccessException(e);
        }
    }

    public static void write(Consumer<EntityManager> work) {
        EntityManager em = factory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            work.accept(em);
            tx.commit();
        } catch (DataAccessException | IllegalArgumentException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw new DataAccessException(e);
        } finally {
            em.close();
        }
    }

    public static void shutdown() {
        EntityManagerFactory current = factory;
        if (current != null && current.isOpen()) current.close();
    }
}
