package lt_web.service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import jakarta.servlet.http.Part;
import lt_web.entity.ProfileEmailChange;
import lt_web.entity.User;
import lt_web.repository.DataAccessException;
import lt_web.repository.impl.ProfileRepository;
import lt_web.repository.impl.ProfileRepository.EmailConfirmation;
import lt_web.repository.impl.ProfileRepository.SavedProfile;
import lt_web.utils.CloudinaryAvatarStorage;
import lt_web.utils.CloudinaryAvatarStorage.Avatar;
import lt_web.utils.MailSender;
import lt_web.utils.OtpCodes;
import lt_web.validation.FormValidation;
import lt_web.validation.FormValidation.FieldError;

public class ProfileService {
    private static final Duration OTP_LIFETIME = Duration.ofMinutes(10);
    private final ProfileRepository repository = new ProfileRepository();
    private final CloudinaryAvatarStorage avatars = new CloudinaryAvatarStorage();

    public User update(int userId, String fullName, String phone, Part image) throws IOException {
        String validName = FormValidation.requiredText("fullname", fullName, 100, "Họ tên");
        String validPhone = FormValidation.phone(phone);
        if (image != null && image.getSize() > 0 && repository.phoneInUse(userId, validPhone))
            throw new FieldError("phone", "Số điện thoại đã được sử dụng.");

        Avatar upload = avatars.upload(image);
        SavedProfile saved = null;
        try {
            saved = repository.update(userId, validName, validPhone, upload == null ? null : upload.url(),
                    upload == null ? null : upload.publicId());
        } catch (DataAccessException e) {
            if (e.isDuplicateKey()) throw new FieldError("phone", "Số điện thoại đã được sử dụng.");
            throw e;
        } finally {
            if (saved == null && upload != null) avatars.discard(upload.publicId());
        }
        avatars.discard(saved.replacedAvatarId());
        return saved.account();
    }

    public ProfileEmailChange pendingEmail(int userId) {
        return repository.pendingEmail(userId);
    }

    public void requestEmailChange(int userId, String candidate) {
        String email = FormValidation.email(candidate);
        String code = OtpCodes.generate();
        String hash = OtpCodes.hash(code, ProfileEmailChange.hashPurpose(email), userId);
        String fullName = repository.requestEmail(userId, email, hash, Instant.now().plus(OTP_LIFETIME));
        try {
            MailSender.sendOtp(email, fullName, code, ProfileEmailChange.PURPOSE);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("Không gửi được OTP. Vui lòng yêu cầu gửi lại mã.", e);
        }
    }

    public User confirmEmailChange(int userId, String code) {
        if (code == null || !code.trim().matches("[0-9]{6}"))
            throw new FieldError("code", "Mã OTP phải gồm 6 chữ số.");
        EmailConfirmation result;
        try {
            result = repository.confirmEmail(userId, code);
        } catch (DataAccessException e) {
            if (e.isDuplicateKey()) throw new IllegalArgumentException("Email đã được sử dụng. Vui lòng nhập địa chỉ khác.");
            throw e;
        }
        if (result.error() != null) {
            if (AccountVerificationService.OTP_INVALID.equals(result.error()))
                throw new FieldError("code", result.error());
            throw new IllegalArgumentException(result.error());
        }
        return result.account();
    }
}
