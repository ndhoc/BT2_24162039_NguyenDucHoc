package lt_web.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ProfileEmailChange")
public class ProfileEmailChange {
    public static final String PURPOSE = "EMAIL_CHANGE";

    @Id
    @Column(name = "user_id")
    private int userId;

    @Column(name = "new_email", nullable = false, length = 255)
    private String email;

    @Column(name = "code_hash", nullable = false, length = 64)
    private String codeHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private int attempts;

    protected ProfileEmailChange() { }

    public ProfileEmailChange(int userId) {
        this.userId = userId;
    }

    public void replace(String email, String codeHash, Instant expiresAt) {
        this.email = email;
        this.codeHash = codeHash;
        this.expiresAt = expiresAt;
        this.attempts = 0;
    }

    public String getEmail() { return email; }
    public String getCodeHash() { return codeHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public int getAttempts() { return attempts; }
    public void recordFailure() { attempts++; }

    public static String hashPurpose(String email) {
        return PURPOSE + ":" + email;
    }
}
