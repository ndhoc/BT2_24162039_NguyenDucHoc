package lt_web.controllers;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import lt_web.entity.Category;
import lt_web.service.CategoryService;
import lt_web.service.impl.CategoryServiceImpl;
import lt_web.utils.ImageStorage;
import lt_web.validation.FormValidation;
import lt_web.utils.JspView;

@WebServlet({"/admin/category/list", "/admin/category/add", "/admin/category/edit", "/admin/category/delete"})
@MultipartConfig(fileSizeThreshold = 1048576, maxFileSize = 5242880, maxRequestSize = 6291456)
public class CategoryServlet extends HttpServlet {
    private final CategoryService categories = new CategoryServiceImpl();
    private String action(HttpServletRequest req) { return req.getServletPath().substring(req.getServletPath().lastIndexOf('/') + 1); }
    private int id(HttpServletRequest req) {
        return FormValidation.positiveId("id", req.getParameter("id"), "Mã danh mục không hợp lệ.");
    }
    private Category existing(HttpServletRequest req) {
        Category category = categories.get(id(req));
        if (category == null) throw new IllegalArgumentException("Danh mục không tồn tại.");
        return category;
    }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = action(req);
            if (action.equals("list")) {
                req.setAttribute("cateList", categories.search(req.getParameter("q")));
                JspView.render(req, resp, "/views/admin/list-category.jsp");
            } else {
                if (!action.equals("add")) req.setAttribute("category", existing(req));
                req.setAttribute("editing", action.equals("edit"));
                JspView.render(req, resp, action.equals("delete") ? "/views/admin/delete-category.jsp" : "/views/admin/category-form.jsp");
            }
        } catch (IllegalArgumentException e) { resp.sendError(404, e.getMessage()); }
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = action(req);
        if (action.equals("list")) { resp.sendError(405); return; }
        Category category = new Category();
        String newIcon = null;
        try {
            if (action.equals("delete")) {
                category = existing(req);
                categories.delete(category.getId());
                cleanup(category.getIcon());
            } else {
                String oldIcon = null;
                if (action.equals("edit")) { category = existing(req); oldIcon = category.getIcon(); }
                category.setName(req.getParameter("catename"));
                category.setName(FormValidation.requiredText("catename", category.getName(), 255, "Tên danh mục"));
                newIcon = ImageStorage.save(req.getPart("icon"));
                if (newIcon != null) category.setIcon(newIcon);
                if (action.equals("edit")) categories.edit(category); else categories.insert(category);
                if (newIcon != null) cleanup(oldIcon);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/category/list?saved=" + action);
        } catch (IllegalArgumentException e) {
            cleanup(newIcon);
            FormErrors.show(req, e);
            req.setAttribute("category", category);
            req.setAttribute("editing", action.equals("edit"));
            JspView.render(req, resp, action.equals("delete") ? "/views/admin/delete-category.jsp" : "/views/admin/category-form.jsp");
        } catch (RuntimeException | IOException | ServletException e) { cleanup(newIcon); throw e; }
    }
    private void cleanup(String icon) {
        try { ImageStorage.delete(icon); }
        catch (IOException e) { getServletContext().log("Could not remove old category image", e); }
    }
}
