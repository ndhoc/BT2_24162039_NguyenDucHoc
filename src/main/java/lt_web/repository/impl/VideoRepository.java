package lt_web.repository.impl;

import java.util.List;

import lt_web.entity.Video;
import lt_web.repository.IVideoRepository;
import lt_web.repository.JpaConfig;

public class VideoRepository implements IVideoRepository {

    @Override
    public void insert(Video video) {
        JpaConfig.write(em -> em.persist(video));
    }

    @Override
    public void update(Video video) {
        JpaConfig.write(em -> em.merge(video));
    }

    @Override
    public void delete(int id) {
        JpaConfig.write(em -> {
            Video managed = em.find(Video.class, id);
            if (managed == null) throw new IllegalArgumentException("Video không còn tồn tại.");
            em.remove(managed);
        });
    }

    @Override
    public Video get(int id) {
        return JpaConfig.read(em -> em.find(Video.class, id));
    }

    @Override
    public List<Video> latest(int limit) {
        return JpaConfig.read(em -> em.createQuery(
                        "select v from Video v order by v.createdAt desc, v.id desc", Video.class)
                .setMaxResults(limit)
                .getResultList());
    }

    @Override
    public List<Video> page(int offset, int limit) {
        return JpaConfig.read(em -> em.createQuery(
                        "select v from Video v order by v.createdAt desc, v.id desc", Video.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList());
    }

    @Override
    public List<Video> pageByUser(int userId, int offset, int limit) {
        return JpaConfig.read(em -> em.createQuery(
                        "select v from Video v where v.user.id = :userId order by v.createdAt desc, v.id desc", Video.class)
                .setParameter("userId", userId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList());
    }

    @Override
    public List<Video> all() {
        return JpaConfig.read(em -> em.createQuery(
                "select v from Video v order by v.createdAt desc, v.id desc", Video.class).getResultList());
    }

    @Override
    public long countByUser(int userId) {
        return JpaConfig.read(em -> em.createQuery(
                        "select count(v) from Video v where v.user.id = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult());
    }

    @Override
    public long count() {
        return JpaConfig.read(em -> em.createQuery("select count(v) from Video v", Long.class).getSingleResult());
    }
}
