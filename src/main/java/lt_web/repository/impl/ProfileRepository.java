package lt_web.repository.impl;

import java.time.Instant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lt_web.entity.OtpToken;
import lt_web.entity.ProfileEmailChange;
import lt_web.entity.User;
import lt_web.repository.JpaConfig;
import lt_web.service.AccountVerificationService;
import lt_web.utils.OtpCodes;
import lt_web.validation.FormValidation.FieldError;

public class ProfileRepository {
    private static final int MAX_ATTEMPTS = 5;

    public record SavedProfile(User account, String replacedAvatarId) { }
    public record EmailConfirmation(User account, String error) { }

    public boolean phoneInUse(int userId, String phone) {
        return JpaConfig.read(em -> phoneInUse(em, userId, phone));
    }

    public SavedProfile update(int userId, String fullName, String phone, String avatarUrl, String avatarId) {
        return JpaConfig.writeResult(em -> {
            User account = lockAccount(em, userId);
            if (phoneInUse(em, userId, phone)) throw new FieldError("phone", "Số điện thoại đã được sử dụng.");
            String replacedAvatarId = null;
            account.setFullName(fullName);
            account.setPhone(phone);
            if (avatarId != null) {
                replacedAvatarId = account.getAvatarPublicId();
                account.setAvatar(avatarUrl);
                account.setAvatarPublicId(avatarId);
            }
            return new SavedProfile(account, replacedAvatarId);
        });
    }

    public ProfileEmailChange pendingEmail(int userId) {
        ProfileEmailChange pending = JpaConfig.read(em -> em.find(ProfileEmailChange.class, userId));
        return pending != null && pending.getExpiresAt().isAfter(Instant.now()) ? pending : null;
    }

    public String requestEmail(int userId, String email, String hash, Instant expiresAt) {
        return JpaConfig.writeResult(em -> {
            User account = lockAccount(em, userId);
            if (account.getEmail().equalsIgnoreCase(email))
                throw new FieldError("email", "Email mới phải khác email hiện tại.");
            if (emailInUse(em, userId, email))
                throw new FieldError("email", "Email đã được sử dụng.");
            ProfileEmailChange pending = em.find(ProfileEmailChange.class, userId);
            if (pending == null) {
                pending = new ProfileEmailChange(userId);
                pending.replace(email, hash, expiresAt);
                em.persist(pending);
            } else {
                pending.replace(email, hash, expiresAt);
            }
            return account.getFullName();
        });
    }

    public EmailConfirmation confirmEmail(int userId, String code) {
        return JpaConfig.writeResult(em -> {
            User account = lockAccount(em, userId);
            ProfileEmailChange pending = em.find(ProfileEmailChange.class, userId);
            if (pending == null) return new EmailConfirmation(null, AccountVerificationService.OTP_EXPIRED);
            if (!pending.getExpiresAt().isAfter(Instant.now())) {
                em.remove(pending);
                return new EmailConfirmation(null, AccountVerificationService.OTP_EXPIRED);
            }
            String email = pending.getEmail();
            if (!OtpCodes.matches(code, pending.getCodeHash(), ProfileEmailChange.hashPurpose(email), userId)) {
                pending.recordFailure();
                if (pending.getAttempts() >= MAX_ATTEMPTS) {
                    em.remove(pending);
                    return new EmailConfirmation(null, "Bạn đã nhập sai 5 lần. Vui lòng yêu cầu mã OTP mới.");
                }
                return new EmailConfirmation(null, AccountVerificationService.OTP_INVALID);
            }
            if (emailInUse(em, userId, email)) {
                em.remove(pending);
                return new EmailConfirmation(null, "Email đã được sử dụng. Vui lòng nhập địa chỉ khác.");
            }
            account.setEmail(email);
            em.remove(pending);
            em.createQuery("delete from OtpToken t where t.user.id = :id and t.purpose = :purpose")
                    .setParameter("id", userId)
                    .setParameter("purpose", OtpToken.PURPOSE_RESET)
                    .executeUpdate();
            return new EmailConfirmation(account, null);
        });
    }

    private static User lockAccount(EntityManager em, int userId) {
        User account = em.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
        if (account == null) throw new IllegalArgumentException("Tài khoản không tồn tại.");
        return account;
    }

    private static boolean phoneInUse(EntityManager em, int userId, String phone) {
        return em.createQuery("select count(u) from AppUser u where u.phone = :phone and u.id <> :id", Long.class)
                .setParameter("phone", phone).setParameter("id", userId).getSingleResult() > 0;
    }

    private static boolean emailInUse(EntityManager em, int userId, String email) {
        return em.createQuery("select count(u) from AppUser u where u.email = :email and u.id <> :id", Long.class)
                .setParameter("email", email).setParameter("id", userId).getSingleResult() > 0;
    }
}
