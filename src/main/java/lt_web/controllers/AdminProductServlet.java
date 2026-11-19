package lt_web.controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lt_web.entity.Category;
import lt_web.entity.Product;
import lt_web.service.CategoryService;
import lt_web.service.ProductService;
import lt_web.service.impl.CategoryServiceImpl;
import lt_web.service.impl.ProductServiceImpl;
import lt_web.utils.ImageStorage;
import lt_web.utils.JspView;
import lt_web.validation.FormValidation;
import lt_web.validation.FormValidation.FieldError;

@WebServlet({"/admin/product", "/admin/product/list", "/admin/product/add", "/admin/product/edit", "/admin/product/delete"})
@MultipartConfig(maxFileSize = ImageStorage.MAX_SIZE, maxRequestSize = ImageStorage.MAX_SIZE + 1024 * 1024)
public class AdminProductServlet extends HttpServlet {
    private final ProductService products = new ProductServiceImpl();
    private final CategoryService categories = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = action(req);
        switch (action) {
            case "add" -> showForm(req, resp, false);
            case "edit", "delete" -> {
                Product product;
                try {
                    product = products.get(productId(req));
                } catch (FieldError e) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                    return;
                }
                if (product == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Sản phẩm không tồn tại.");
                    return;
                }
                req.setAttribute("product", product);
                if ("edit".equals(action)) showForm(req, resp, true);
                else JspView.render(req, resp, "/views/admin/delete-product.jsp");
            }
            default -> {
                String keyword = req.getParameter("q");
                req.setAttribute("productList", products.search(null, keyword == null ? null : keyword.trim()));
                JspView.render(req, resp, "/views/admin/list-product.jsp");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (action(req)) {
            case "add" -> save(req, resp, false);
            case "edit" -> save(req, resp, true);
            case "delete" -> {
                try {
                    cleanup(products.delete(productId(req)));
                } catch (IllegalArgumentException e) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                    return;
                }
                redirect(req, resp, "delete");
            }
            default -> resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, boolean editing)
            throws ServletException, IOException {
        Product changes = new Product();
        if (editing) {
            Product current;
            try {
                current = products.get(productId(req));
            } catch (FieldError e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                return;
            }
            if (current == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Sản phẩm không tồn tại.");
                return;
            }
            req.setAttribute("product", current);
            changes.setId(current.getId());
        }

        String uploadedImage = null;
        boolean saved = false;
        try {
            changes.setName(FormValidation.requiredText("name", req.getParameter("name"), 255, "Tên sản phẩm"));
            changes.setPrice(FormValidation.productPrice(req.getParameter("price")));
            changes.setDescription(FormValidation.description(req.getParameter("description")));
            int categoryId = FormValidation.positiveId("categoryId", req.getParameter("categoryId"),
                    "Vui lòng chọn danh mục hợp lệ.");
            Category category = categories.get(categoryId);
            if (category == null) throw new FieldError("categoryId", "Danh mục không tồn tại.");
            changes.setCategory(category);

            uploadedImage = ImageStorage.saveProduct(req.getPart("image"));
            changes.setImage(uploadedImage);
            String replacedImage = null;
            if (editing) replacedImage = products.update(changes);
            else products.insert(changes);
            saved = true;
            cleanup(replacedImage);
            redirect(req, resp, editing ? "edit" : "add");
        } catch (IllegalArgumentException e) {
            FormErrors.show(req, e);
            req.setAttribute("submitted", true);
            showForm(req, resp, editing);
        } finally {
            if (!saved) cleanup(uploadedImage);
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, boolean editing)
            throws ServletException, IOException {
        req.setAttribute("editing", editing);
        req.setAttribute("categoryList", categories.getAll());
        JspView.render(req, resp, "/views/admin/product-form.jsp");
    }

    private static int productId(HttpServletRequest req) {
        return FormValidation.positiveId("id", req.getParameter("id"), "Mã sản phẩm không hợp lệ.");
    }

    private static String action(HttpServletRequest req) {
        String path = req.getServletPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private static void redirect(HttpServletRequest req, HttpServletResponse resp, String saved) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/admin/product/list?saved=" + saved);
    }

    private void cleanup(String image) {
        try {
            ImageStorage.deleteProduct(image);
        } catch (IOException e) {
            getServletContext().log("Could not remove product image " + image, e);
        }
    }
}
