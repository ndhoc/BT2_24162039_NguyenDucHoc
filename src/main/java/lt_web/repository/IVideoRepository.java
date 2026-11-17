package lt_web.repository;

import java.util.List;

import lt_web.entity.Video;

public interface IVideoRepository {
    void insert(Video video);
    void update(Video video);
    void delete(int id);
    Video get(int id);
    List<Video> latest(int limit);
    List<Video> page(int offset, int limit);
    List<Video> pageByUser(int userId, int offset, int limit);
    List<Video> all();
    long countByUser(int userId);
    long count();
}
