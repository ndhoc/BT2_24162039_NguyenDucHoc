package lt_web.controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lt_web.service.CategoryService;
import lt_web.service.ProductService;
import lt_web.service.impl.CategoryServiceImpl;
import lt_web.service.impl.ProductServiceImpl;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private final ProductService products = new ProductServiceImpl();
    private final CategoryService categories = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer categoryId = null;
        String raw = req.getParameter("categoryId");
        if (raw != null && !raw.isBlank()) {
            try {
                categoryId = Integer.valueOf(raw);
                if (categoryId <= 0) categoryId = null;
            } catch (NumberFormatException ignored) {
                categoryId = null;
            }
        }
        String keyword = req.getParameter("q");

        req.setAttribute("cateList", categories.getAll());
        req.setAttribute("productList", products.search(categoryId, keyword));
        req.setAttribute("selectedCategoryId", categoryId);
        req.setAttribute("keyword", keyword == null ? "" : keyword.trim());
        req.getRequestDispatcher("/views/products.jsp").forward(req, resp);
    }
}
