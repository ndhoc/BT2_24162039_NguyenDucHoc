package lt_web.service.impl;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import lt_web.entity.Video;
import lt_web.repository.IVideoRepository;
import lt_web.repository.impl.VideoRepository;
import lt_web.service.VideoService;

public class VideoServiceImpl implements VideoService {
    private final IVideoRepository repository = new VideoRepository();

    private void validate(Video video, boolean requireFile) {
        String title = video.getTitle() == null ? "" : video.getTitle().trim();
        if (title.isEmpty() || title.length() > 255) throw new IllegalArgumentException("Tiêu đề video phải có 1–255 ký tự.");
        video.setTitle(title);
        if (video.getDescription() != null) {
            String description = video.getDescription().trim();
            if (description.length() > 1000)
                throw new IllegalArgumentException("Mô tả tối đa 1000 ký tự.");
            video.setDescription(description.isEmpty() ? null : description);
        }
        if (requireFile && (video.getFileName() == null || video.getFileName().isBlank()))
            throw new IllegalArgumentException("Vui lòng chọn tệp video.");
        if (video.getUser() == null) throw new IllegalArgumentException("Video phải thuộc về một tài khoản.");
        if (video.getCreatedAt() == null) video.setCreatedAt(Date.from(Instant.now()));
    }

    @Override public void insert(Video video) { validate(video, true); repository.insert(video); }
    @Override public void update(Video video) { validate(video, false); repository.update(video); }
    @Override public void delete(int id) { repository.delete(id); }
    @Override public Video get(int id) { return repository.get(id); }
    @Override public List<Video> latest(int limit) {
        if (limit <= 0) return List.of();
        return repository.latest(limit);
    }
    @Override public List<Video> all() { return repository.all(); }
    @Override public long countByUser(int userId) { return repository.countByUser(userId); }
    @Override public long count() { return repository.count(); }

    @Override
    public List<Video> page(int page) {
        int safePage = Math.max(1, page);
        return repository.page((safePage - 1) * PAGE_SIZE, PAGE_SIZE);
    }

    @Override
    public int pageCount() {
        long total = repository.count();
        return (int) Math.max(1, (total + PAGE_SIZE - 1) / PAGE_SIZE);
    }

    @Override
    public List<Video> pageByUser(int userId, int page) {
        int safePage = Math.max(1, page);
        return repository.pageByUser(userId, (safePage - 1) * PAGE_SIZE, PAGE_SIZE);
    }

    @Override
    public int pageCountByUser(int userId) {
        long total = repository.countByUser(userId);
        return (int) Math.max(1, (total + PAGE_SIZE - 1) / PAGE_SIZE);
    }
}
