package lt_web.controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lt_web.entity.User;
import lt_web.service.AccountVerificationService;
import lt_web.service.UserService;
import lt_web.service.impl.AccountVerificationServiceImpl;
import lt_web.service.impl.UserServiceImpl;

/**
 * Quên mật khẩu gồm 2 bước:
 * 1. Nhập mã OTP để xác nhận đúng chủ tài khoản.
 * 2. Chỉ sau khi mã đúng mới hiện form nhập mật khẩu mới.
 */
@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String PENDING = "pendingReset";
    private static final String VERIFIED = "resetVerified";

    private final UserService users = new UserServiceImpl();
    private final AccountVerificationService verification = new AccountVerificationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = pendingUser(session);
        if (user == null) {
            session.removeAttribute(PENDING);
            session.removeAttribute(VERIFIED);
            resp.sendRedirect(req.getContextPath() + "/forgot-password?expired=1");
            return;
        }
        render(req, resp, session, user);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = pendingUser(session);
        if (user == null) {
            session.removeAttribute(PENDING);
            session.removeAttribute(VERIFIED);
            resp.sendRedirect(req.getContextPath() + "/forgot-password?expired=1");
            return;
        }
        try {
            String action = req.getParameter("action");
            if ("resend".equals(action)) {
                verification.sendPasswordResetOtp(user);
                session.removeAttribute(VERIFIED);
                req.setAttribute("message", "Mã OTP mới đã được gửi tới " + mask(user.getEmail()) + ".");
            } else if ("verify-code".equals(action)) {
                if (Boolean.TRUE.equals(session.getAttribute(VERIFIED))) {
                    req.setAttribute("alert", "Mã OTP đã được xác nhận. Vui lòng nhập mật khẩu mới.");
                } else {
                    verification.verifyPasswordResetCode(user.getId(), req.getParameter("code"));
                    session.setAttribute(VERIFIED, Boolean.TRUE);
                    req.setAttribute("message", "Xác nhận mã OTP thành công. Hãy đặt mật khẩu mới.");
                }
            } else if ("set-password".equals(action)) {
                if (!Boolean.TRUE.equals(session.getAttribute(VERIFIED)))
                    throw new IllegalArgumentException("Vui lòng xác nhận mã OTP trước khi đặt mật khẩu mới.");
                verification.resetPassword(user.getId(), req.getParameter("password"), req.getParameter("confirm"));
                session.removeAttribute(PENDING);
                session.removeAttribute(VERIFIED);
                resp.sendRedirect(req.getContextPath() + "/login?reset=1");
                return;
            } else {
                throw new IllegalArgumentException("Yêu cầu không hợp lệ.");
            }
        } catch (IllegalArgumentException e) {
            req.setAttribute("alert", e.getMessage());
            if (e.getMessage().startsWith(AccountVerificationService.OTP_EXPIRED)
                    || e.getMessage().contains("quá số lần")) {
                session.removeAttribute(VERIFIED);
            }
        }
        render(req, resp, session, user);
    }

    private void render(HttpServletRequest req, HttpServletResponse resp, HttpSession session, User user)
            throws ServletException, IOException {
        req.setAttribute("step", Boolean.TRUE.equals(session.getAttribute(VERIFIED)) ? "password" : "code");
        req.setAttribute("username", user.getUsername());
        req.setAttribute("maskedEmail", mask(user.getEmail()));
        req.getRequestDispatcher("/views/reset-password.jsp").forward(req, resp);
    }

    private User pendingUser(HttpSession session) {
        Object userId = session.getAttribute(PENDING);
        return userId instanceof Integer id ? users.getById(id) : null;
    }

    private static String mask(String email) {
        if (email == null || email.isBlank()) return "email đã đăng ký";
        int at = email.indexOf('@');
        if (at <= 1) return "***" + email.substring(Math.max(at, 0));
        return email.charAt(0) + "***" + email.substring(at);
    }
}
