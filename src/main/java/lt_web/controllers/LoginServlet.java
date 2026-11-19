package lt_web.controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lt_web.entity.User;
import lt_web.service.AuthService;
import lt_web.utils.AuthCookies;
import lt_web.utils.JspView;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getSession().getAttribute("account") != null) {
            response.sendRedirect(request.getContextPath() + "/waiting"); return;
        }
        JspView.render(request, response, "/views/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = authService.authenticate(username, password);
            boolean remember = "on".equals(request.getParameter("remember"));
            if (remember) AuthCookies.remember(request, response, user);
            else AuthCookies.clear(request, response);
            AuthCookies.signIn(request, user, remember ? "Session + Cookie ghi nhớ" : "Session");
            response.sendRedirect(request.getContextPath() + "/waiting");
        } catch (IllegalStateException e) {
            User pending = authService.findPending(username);
            if (pending != null) request.getSession().setAttribute("pendingActivation", pending.getId());
            request.setAttribute("alert", e.getMessage());
            request.setAttribute("pendingUsername", pending == null ? null : pending.getUsername());
            JspView.render(request, response, "/views/login.jsp");
        } catch (IllegalArgumentException e) {
            FormErrors.show(request, e);
            JspView.render(request, response, "/views/login.jsp");
        }
    }
}
