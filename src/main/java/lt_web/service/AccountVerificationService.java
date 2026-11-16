package lt_web.service;

import lt_web.entity.User;

/** Nghiệp vụ OTP: kích hoạt tài khoản và đặt lại mật khẩu. */
public interface AccountVerificationService {
    String OTP_EXPIRED = "Mã OTP đã hết hạn hoặc không tồn tại. Vui lòng yêu cầu mã mới.";
    String OTP_INVALID = "Mã OTP không đúng.";

    void sendActivationOtp(User user);
    void activate(int userId, String code);
    void sendPasswordResetOtp(User user);

    /** Chỉ kiểm tra mã, chưa đổi mật khẩu; dùng cho bước 1 của quên mật khẩu. */
    void verifyPasswordResetCode(int userId, String code);

    /** Bước 2: đổi mật khẩu sau khi mã đã được xác thực ở bước 1. */
    void resetPassword(int userId, String password, String confirm);
}
