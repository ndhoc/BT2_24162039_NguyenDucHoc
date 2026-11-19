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
import lt_web.utils.JspView;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserService users = new UserServiceImpl();
    private final AccountVerificationService verification = new AccountVerificationServiceImpl();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JspView.render(req, resp, "/views/register.jsp");
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User user = users.register(req.getParameter("username"), req.getParameter("password"),
                    req.getParameter("confirm"), req.getParameter("email"),
                    req.getParameter("fullname"), req.getParameter("phone"));
            HttpSession session = req.getSession();
            session.setAttribute("pendingActivation", user.getId());
            try {
                verification.sendActivationOtp(user);
                resp.sendRedirect(req.getContextPath() + "/activate?sent=1");
            } catch (IllegalStateException mailError) {
                getServletContext().log("Không gửi được OTP kích hoạt cho " + user.getUsername(), mailError);
                req.setAttribute("alert", "Tài khoản đã được tạo nhưng chưa gửi được email OTP: "
                        + mailError.getMessage() + " Hãy dùng nút gửi lại mã bên dưới.");
                req.setAttribute("username", user.getUsername());
                JspView.render(req, resp, "/views/activate.jsp");
            }
        } catch (IllegalArgumentException e) {
            FormErrors.show(req, e);
            JspView.render(req, resp, "/views/register.jsp");
        }
    }
}
