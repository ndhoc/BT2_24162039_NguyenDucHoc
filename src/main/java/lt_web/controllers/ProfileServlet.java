package lt_web.controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lt_web.entity.User;
import lt_web.service.ProfileService;
import lt_web.service.UserService;
import lt_web.service.impl.UserServiceImpl;
import lt_web.utils.ImageStorage;
import lt_web.utils.JspView;
import lt_web.utils.MailSender;

@WebServlet("/profile")
@MultipartConfig(maxFileSize = ImageStorage.MAX_SIZE, maxRequestSize = ImageStorage.MAX_SIZE + 1024 * 1024)
public class ProfileServlet extends HttpServlet {
    private final UserService users = new UserServiceImpl();
    private final ProfileService profiles = new ProfileService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User account = currentAccount(req, resp);
        if (account != null) render(req, resp, account);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User account = currentAccount(req, resp);
        if (account == null) return;

        String action = req.getParameter("action");
        try {
            if ("save-profile".equals(action)) {
                account = profiles.update(account.getId(), req.getParameter("fullname"), req.getParameter("phone"),
                        req.getPart("avatar"));
                refreshAccount(req.getSession(), account);
                redirect(req, resp, "profile");
            } else if ("request-email".equals(action)) {
                profiles.requestEmailChange(account.getId(), req.getParameter("email"));
                redirect(req, resp, "otp");
            } else if ("confirm-email".equals(action)) {
                account = profiles.confirmEmailChange(account.getId(), req.getParameter("code"));
                refreshAccount(req.getSession(), account);
                redirect(req, resp, "email");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            FormErrors.show(req, e);
            req.setAttribute("profileSubmitted", "save-profile".equals(action));
            render(req, resp, account);
        }
    }

    private User currentAccount(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User signedIn = (User) session.getAttribute("account");
        User current = signedIn == null ? null : users.getById(signedIn.getId());
        if (current == null || !current.isActive()) {
            session.removeAttribute("account");
            session.removeAttribute("user");
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        refreshAccount(session, current);
        return current;
    }

    private void render(HttpServletRequest req, HttpServletResponse resp, User account)
            throws ServletException, IOException {
        req.setAttribute("profileAccount", account);
        req.setAttribute("pendingEmail", profiles.pendingEmail(account.getId()));
        req.setAttribute("otpViaEmail", MailSender.isConfigured());
        JspView.render(req, resp, "/views/profile.jsp");
    }

    private static void refreshAccount(HttpSession session, User account) {
        session.setAttribute("account", account);
        session.setAttribute("user", account);
    }

    private static void redirect(HttpServletRequest req, HttpServletResponse resp, String saved) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/profile?saved=" + saved);
    }
}
