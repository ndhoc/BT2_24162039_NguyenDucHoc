package lt_web.controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lt_web.service.CategoryService;
import lt_web.service.ProductService;
import lt_web.service.UserService;
import lt_web.service.impl.CategoryServiceImpl;
import lt_web.service.impl.ProductServiceImpl;
import lt_web.service.impl.UserServiceImpl;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private final ProductService products = new ProductServiceImpl();
    private final CategoryService categories = new CategoryServiceImpl();
    private final UserService users = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("productCount", products.count());
        req.setAttribute("categoryCount", categories.count());
        req.setAttribute("userCount", users.count());
        req.setAttribute("totalValue", products.totalValue());
        req.setAttribute("categoryStats", products.countByCategory());
        req.setAttribute("recentProducts", products.latest(5));
        req.getRequestDispatcher("/views/dashboard.jsp").forward(req, resp);
    }
}
