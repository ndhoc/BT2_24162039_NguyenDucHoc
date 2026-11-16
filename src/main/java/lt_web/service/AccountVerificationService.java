package lt_web.service;

import lt_web.entity.User;

/** Nghiệp vụ OTP: kích hoạt tài khoản bằng mã gửi qua email. */
public interface AccountVerificationService {
    String OTP_EXPIRED = "Mã OTP đã hết hạn hoặc không tồn tại. Vui lòng yêu cầu mã mới.";
    String OTP_INVALID = "Mã OTP không đúng.";

    void sendActivationOtp(User user);
    void activate(int userId, String code);
}
