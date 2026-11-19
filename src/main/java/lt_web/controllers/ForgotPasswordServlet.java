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
import lt_web.validation.FormValidation;
import lt_web.validation.FormValidation.FieldError;
import lt_web.utils.JspView;

/** Bước 1 của quên mật khẩu: nhận email và gửi mã xác nhận OTP. */
@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserService users = new UserServiceImpl();
    private final AccountVerificationService verification = new AccountVerificationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JspView.render(req, resp, "/views/forgot-password.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        try {
            User user = users.getByEmail(FormValidation.email(email));
            if (user == null)
                throw new FieldError("email", "Email không tồn tại trong hệ thống.");
            verification.sendPasswordResetOtp(user);
            HttpSession session = req.getSession();
            session.removeAttribute(ResetPasswordServlet.VERIFIED);
            session.setAttribute(ResetPasswordServlet.PENDING, user.getId());
            resp.sendRedirect(req.getContextPath() + "/reset-password?sent=1");
        } catch (IllegalArgumentException e) {
            FormErrors.show(req, e);
            req.setAttribute("email", email);
            JspView.render(req, resp, "/views/forgot-password.jsp");
        }
    }
}
