package lt_web.controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lt_web.entity.Product;
import lt_web.service.CategoryService;
import lt_web.service.ProductService;
import lt_web.service.impl.CategoryServiceImpl;
import lt_web.service.impl.ProductServiceImpl;
import lt_web.utils.JspView;
import lt_web.validation.FormValidation;
import lt_web.validation.FormValidation.FieldError;

@WebServlet({"/products", "/products/detail"})
public class ProductServlet extends HttpServlet {
    private final ProductService products = new ProductServiceImpl();
    private final CategoryService categories = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/products/detail".equals(req.getServletPath())) {
            showDetail(req, resp);
            return;
        }

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
        JspView.render(req, resp, "/views/products.jsp");
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        try {
            id = FormValidation.positiveId("id", req.getParameter("id"), "Mã sản phẩm không hợp lệ.");
        } catch (FieldError e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            return;
        }
        Product product = products.get(id);
        if (product == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Sản phẩm không tồn tại.");
            return;
        }
        req.setAttribute("product", product);
        JspView.render(req, resp, "/views/product-detail.jsp");
    }
}
