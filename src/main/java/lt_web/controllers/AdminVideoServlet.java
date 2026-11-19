package lt_web.controllers;

import java.io.IOException;
import java.time.Instant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lt_web.entity.User;
import lt_web.entity.Video;
import lt_web.service.UserService;
import lt_web.service.VideoService;
import lt_web.service.impl.UserServiceImpl;
import lt_web.service.impl.VideoServiceImpl;
import lt_web.utils.VideoStorage;
import lt_web.validation.FormValidation;
import lt_web.validation.FormValidation.FieldError;
import lt_web.utils.JspView;

/** CRUD video cho quản trị viên: /admin/video/list|add|edit|delete. */
@WebServlet({"/admin/video/list", "/admin/video/add", "/admin/video/edit", "/admin/video/delete"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = VideoStorage.MAX_SIZE,
        maxRequestSize = VideoStorage.MAX_SIZE + 1024 * 1024)
public class AdminVideoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final VideoService videos = new VideoServiceImpl();
    private final UserService users = new UserServiceImpl();

    private String action(HttpServletRequest req) {
        String path = req.getServletPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private int id(HttpServletRequest req) {
        return FormValidation.positiveId("id", req.getParameter("id"), "Mã video không hợp lệ.");
    }

    private Video existing(HttpServletRequest req) {
        Video video = videos.get(id(req));
        if (video == null) throw new IllegalArgumentException("Video không tồn tại.");
        return video;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String action = action(req);
            if (action.equals("list")) {
                String keyword = req.getParameter("q");
                var list = videos.all();
                if (keyword != null && !keyword.isBlank()) {
                    String needle = keyword.trim().toLowerCase();
                    list = list.stream()
                            .filter(v -> v.getTitle().toLowerCase().contains(needle)
                                    || (v.getUser() != null && v.getUser().getUsername().toLowerCase().contains(needle)))
                            .toList();
                }
                req.setAttribute("videoList", list);
                JspView.render(req, resp, "/views/admin/list-video.jsp");
            } else {
                if (!action.equals("add")) req.setAttribute("video", existing(req));
                req.setAttribute("editing", action.equals("edit"));
                req.setAttribute("userList", users.all());
                JspView.render(req, resp, action.equals("delete") ? "/views/admin/delete-video.jsp"
                        : "/views/admin/video-form.jsp");
            }
        } catch (IllegalArgumentException e) {
            resp.sendError(404, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = action(req);
        if (action.equals("list")) { resp.sendError(405); return; }
        Video video = new Video();
        String newFile = null;
        boolean persisted = false;
        try {
            String oldFile = null;
            if (action.equals("delete")) {
                Video target = existing(req);
                videos.delete(target.getId());
                cleanup(target.getFileName(), "Không thể xóa tệp của video đã xóa");
                resp.sendRedirect(req.getContextPath() + "/admin/video/list?saved=delete");
                return;
            }
            if (action.equals("edit")) {
                video = existing(req);
                oldFile = video.getFileName();
            }
            video.setTitle(req.getParameter("title"));
            video.setDescription(req.getParameter("description"));
            video.setTitle(FormValidation.requiredText("title", video.getTitle(), 255, "Tiêu đề video"));
            video.setDescription(FormValidation.description(video.getDescription()));
            int userId = FormValidation.positiveId("userId", req.getParameter("userId"), "Vui lòng chọn chủ sở hữu hợp lệ.");
            User owner = users.getById(userId);
            if (owner == null) throw new FieldError("userId", "Tài khoản chủ sở hữu không tồn tại.");
            video.setUser(owner);
            if (video.getCreatedAt() == null) video.setCreatedAt(Instant.now());

            Part part = req.getPart("videoFile");
            newFile = VideoStorage.save(part);
            if (newFile != null) video.setFileName(newFile);
            else if (oldFile == null) throw new FieldError("videoFile", "Vui lòng chọn tệp video.");

            if (action.equals("edit")) videos.update(video); else videos.insert(video);
            persisted = true;
            if (newFile != null && oldFile != null)
                cleanup(oldFile, "Không thể xóa tệp video cũ sau khi cập nhật");
            resp.sendRedirect(req.getContextPath() + "/admin/video/list?saved=" + action);
        } catch (IllegalArgumentException e) {
            cleanup(newFile, "Không thể xóa tệp video sau thao tác không hợp lệ");
            if (action.equals("delete")) {
                resp.sendError(404, e.getMessage());
                return;
            }
            FormErrors.show(req, e);
            req.setAttribute("video", video);
            req.setAttribute("editing", action.equals("edit"));
            req.setAttribute("userList", users.all());
            JspView.render(req, resp, "/views/admin/video-form.jsp");
        } catch (IllegalStateException e) {
            cleanup(newFile, "Không thể xóa tệp video vượt dung lượng");
            req.setAttribute("alert", "Video vượt quá dung lượng tối đa 50 MB.");
            req.setAttribute("video", video);
            req.setAttribute("editing", action.equals("edit"));
            req.setAttribute("userList", users.all());
            JspView.render(req, resp, "/views/admin/video-form.jsp");
        } catch (RuntimeException | IOException | ServletException e) {
            if (!persisted) cleanup(newFile, "Không thể xóa tệp video sau khi lưu thất bại");
            throw e;
        }
    }

    private void cleanup(String filename, String message) {
        try {
            VideoStorage.delete(filename);
        } catch (IOException e) {
            getServletContext().log(message, e);
        }
    }
}
