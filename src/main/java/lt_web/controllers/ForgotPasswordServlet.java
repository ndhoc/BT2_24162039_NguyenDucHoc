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

/** Bước 1 của quên mật khẩu: nhận email và gửi mã xác nhận OTP. */
@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String PENDING = "pendingReset";

    private final UserService users = new UserServiceImpl();
    private final AccountVerificationService verification = new AccountVerificationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        try {
            if (email == null || email.isBlank())
                throw new IllegalArgumentException("Vui lòng nhập email đã đăng ký.");
            User user = users.getByEmail(email);
            if (user == null)
                throw new IllegalArgumentException("Email không tồn tại trong hệ thống.");
            verification.sendPasswordResetOtp(user);
            HttpSession session = req.getSession();
            session.setAttribute(PENDING, user.getId());
            resp.sendRedirect(req.getContextPath() + "/reset-password?sent=1");
        } catch (IllegalArgumentException e) {
            req.setAttribute("alert", e.getMessage());
            req.setAttribute("email", email);
            req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
        }
    }
}
