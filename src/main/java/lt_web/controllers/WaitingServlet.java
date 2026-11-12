package lt_web.controllers;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lt_web.entity.User;

@WebServlet("/waiting")
public class WaitingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("account");
        String target = "/login";
        if (user != null) target = (user.getRoleid() == 1 || user.getRoleid() == 2) ? "/dashboard" : "/products";
        resp.sendRedirect(req.getContextPath() + target);
    }
}
