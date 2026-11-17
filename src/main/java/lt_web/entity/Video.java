package lt_web.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Nationalized;

/** Mỗi video thuộc về một tài khoản: quan hệ 1-n giữa [User] và Videos. */
@Entity
@Table(name = "Videos")
public class Video implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private int id;

    @Column(name = "title", nullable = false, length = 255)
    @Nationalized
    private String title;

    @Column(name = "description", length = 1000)
    @Nationalized
    private String description;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Video() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    /** JSTL fmt:formatDate làm việc với java.util.Date. */
    public Date getCreatedAt() { return createdAt == null ? null : Date.from(createdAt); }
    public Instant getCreatedAtInstant() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt == null ? null : createdAt.toInstant(); }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
