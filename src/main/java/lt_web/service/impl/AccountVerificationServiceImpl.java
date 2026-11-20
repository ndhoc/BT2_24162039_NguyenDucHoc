package lt_web.service.impl;

import java.time.Duration;
import java.time.Instant;

import lt_web.entity.OtpToken;
import lt_web.entity.User;
import lt_web.repository.IOtpRepository;
import lt_web.repository.IUserRepository;
import lt_web.repository.impl.OtpRepository;
import lt_web.repository.impl.UserRepository;
import lt_web.service.AccountVerificationService;
import lt_web.utils.MailSender;
import lt_web.utils.OtpCodes;
import lt_web.utils.PasswordUtils;
import lt_web.validation.FormValidation;
import lt_web.validation.FormValidation.FieldError;

public class AccountVerificationServiceImpl implements AccountVerificationService {
    private static final Duration LIFETIME = Duration.ofMinutes(10);
    private static final int MAX_ATTEMPTS = 5;

    private final IOtpRepository tokens = new OtpRepository();
    private final IUserRepository users = new UserRepository();

    @Override
    public void sendActivationOtp(User user) {
        issue(user, OtpToken.PURPOSE_ACTIVATION);
    }

    @Override
    public void activate(int userId, String code) {
        verify(userId, OtpToken.PURPOSE_ACTIVATION, code);
        User user = users.getById(userId);
        if (user == null) throw new IllegalArgumentException("Tài khoản không tồn tại.");
        user.setActive(Boolean.TRUE);
        users.update(user);
        tokens.delete(userId, OtpToken.PURPOSE_ACTIVATION);
    }

    @Override
    public void sendPasswordResetOtp(User user) {
        issue(user, OtpToken.PURPOSE_RESET);
    }

    @Override
    public long verifyPasswordResetCode(int userId, String code) {
        return verify(userId, OtpToken.PURPOSE_RESET, code).getId();
    }

    @Override
    public void resetPassword(int userId, long tokenId, String password, String confirm) {
        FormValidation.newPassword(password, confirm);
        if (!tokens.completePasswordReset(userId, tokenId, PasswordUtils.hash(password)))
            throw new IllegalArgumentException(OTP_EXPIRED);
    }

    private void issue(User user, String purpose) {
        String code = OtpCodes.generate();
        OtpToken token = new OtpToken(user, purpose, OtpCodes.hash(code, purpose, user.getId()),
                Instant.now().plus(LIFETIME));
        tokens.replace(token);
        MailSender.sendOtp(user.getEmail(), user.getFullName(), code, purpose);
    }

    private OtpToken verify(int userId, String purpose, String code) {
        if (code == null || !code.trim().matches("[0-9]{6}"))
            throw new FieldError("code", "Mã OTP phải gồm 6 chữ số.");
        OtpToken token = tokens.find(userId, purpose);
        if (token == null || token.getExpiresAt().isBefore(Instant.now())) {
            if (token != null) tokens.delete(userId, purpose);
            throw new IllegalArgumentException(OTP_EXPIRED);
        }
        if (token.getAttempts() >= MAX_ATTEMPTS) {
            tokens.delete(userId, purpose);
            throw new IllegalArgumentException("Bạn đã nhập sai quá số lần cho phép. Vui lòng yêu cầu mã mới.");
        }
        if (!OtpCodes.matches(code, token.getCodeHash(), purpose, userId)) {
            tokens.incrementAttempts(token.getId());
            throw new FieldError("code", OTP_INVALID);
        }
        return token;
    }
}
