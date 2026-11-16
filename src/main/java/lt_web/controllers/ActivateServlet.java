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

/** Kích hoạt tài khoản bằng mã OTP gửi qua email sau khi đăng ký. */
@WebServlet("/activate")
public class ActivateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String PENDING = "pendingActivation";

    private final UserService users = new UserServiceImpl();
    private final AccountVerificationService verification = new AccountVerificationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String username = req.getParameter("username");
        if (username != null && !username.isBlank()) {
            User user = users.get(username.trim());
            if (user != null && !user.isActive()) session.setAttribute(PENDING, user.getId());
        }
        Integer userId = (Integer) session.getAttribute(PENDING);
        User user = userId == null ? null : users.getById(userId);
        if (user == null || user.isActive()) {
            session.removeAttribute(PENDING);
            req.setAttribute("lost", Boolean.TRUE);
        } else {
            req.setAttribute("username", user.getUsername());
            req.setAttribute("maskedEmail", mask(user.getEmail()));
        }
        req.getRequestDispatcher("/views/activate.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute(PENDING);
        User user = userId == null ? null : users.getById(userId);
        if (user == null) {
            String username = req.getParameter("username");
            user = username == null || username.isBlank() ? null : users.get(username.trim());
            if (user != null) session.setAttribute(PENDING, user.getId());
        }

        try {
            if (user == null) throw new IllegalArgumentException("Không tìm thấy tài khoản cần kích hoạt.");
            if (user.isActive()) throw new IllegalArgumentException("Tài khoản đã được kích hoạt. Bạn có thể đăng nhập.");

            if ("resend".equals(req.getParameter("action"))) {
                verification.sendActivationOtp(user);
                req.setAttribute("message", "Mã OTP mới đã được gửi tới " + mask(user.getEmail()) + ".");
            } else {
                verification.activate(user.getId(), req.getParameter("code"));
                session.removeAttribute(PENDING);
                resp.sendRedirect(req.getContextPath() + "/login?activated=1");
                return;
            }
            req.setAttribute("username", user.getUsername());
            req.setAttribute("maskedEmail", mask(user.getEmail()));
        } catch (IllegalArgumentException e) {
            req.setAttribute("alert", e.getMessage());
            if (user != null) {
                req.setAttribute("username", user.getUsername());
                req.setAttribute("maskedEmail", mask(user.getEmail()));
            }
        }
        req.getRequestDispatcher("/views/activate.jsp").forward(req, resp);
    }

    private static String mask(String email) {
        if (email == null || email.isBlank()) return "email đã đăng ký";
        int at = email.indexOf('@');
        if (at <= 1) return "***" + email.substring(Math.max(at, 0));
        return email.charAt(0) + "***" + email.substring(at);
    }
}
