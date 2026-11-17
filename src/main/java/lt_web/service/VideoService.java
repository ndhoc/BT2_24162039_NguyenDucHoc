package lt_web.service;

import java.util.List;

import lt_web.entity.Video;

public interface VideoService {
    public static final int PAGE_SIZE = 6;

    void insert(Video video);
    void update(Video video);
    void delete(int id);
    Video get(int id);
    List<Video> latest(int limit);
    List<Video> page(int page);
    List<Video> pageByUser(int userId, int page);
    List<Video> all();
    long countByUser(int userId);
    long count();
    int pageCount();
    int pageCountByUser(int userId);
}
