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

@WebServlet(urlPatterns = { "", "/index" })
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CategoryService categories = new CategoryServiceImpl();
    private final ProductService products = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("cateList", categories.getAll());
        request.setAttribute("productList", products.latest(6));
        request.getRequestDispatcher("/views/index.jsp").forward(request, response);
    }
}
