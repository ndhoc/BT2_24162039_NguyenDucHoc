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
import lt_web.service.VideoService;
import lt_web.service.impl.VideoServiceImpl;
import lt_web.utils.VideoStorage;
import lt_web.validation.FormValidation;
import lt_web.validation.FormValidation.FieldError;
import lt_web.utils.JspView;

/** Thành viên tự tải video lên bằng Multipart; video thuộc về tài khoản đang đăng nhập. */
@WebServlet("/video/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = VideoStorage.MAX_SIZE,
        maxRequestSize = VideoStorage.MAX_SIZE + 1024 * 1024)
public class VideoUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final VideoService videos = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User account = (User) req.getSession().getAttribute("account");
        if (account == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        JspView.render(req, resp, "/views/video-upload.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User account = (User) req.getSession().getAttribute("account");
        if (account == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String newFile = null;
        boolean persisted = false;
        try {
            Video video = new Video();
            video.setTitle(FormValidation.requiredText("title", req.getParameter("title"), 255, "Tiêu đề video"));
            video.setDescription(FormValidation.description(req.getParameter("description")));
            video.setUser(account);
            video.setCreatedAt(Instant.now());

            Part part = req.getPart("videoFile");
            newFile = VideoStorage.save(part);
            if (newFile == null) throw new FieldError("videoFile", "Vui lòng chọn tệp video.");
            video.setFileName(newFile);

            videos.insert(video);
            persisted = true;
            resp.sendRedirect(req.getContextPath() + "/video?saved=1");
        } catch (IllegalArgumentException e) {
            cleanup(newFile);
            FormErrors.show(req, e);
            req.setAttribute("titleValue", req.getParameter("title"));
            req.setAttribute("descriptionValue", req.getParameter("description"));
            JspView.render(req, resp, "/views/video-upload.jsp");
        } catch (IllegalStateException e) {
            cleanup(newFile);
            req.setAttribute("alert", "Video vượt quá dung lượng tối đa 50 MB.");
            req.setAttribute("titleValue", req.getParameter("title"));
            req.setAttribute("descriptionValue", req.getParameter("description"));
            JspView.render(req, resp, "/views/video-upload.jsp");
        } catch (RuntimeException | IOException | ServletException e) {
            if (!persisted) cleanup(newFile);
            throw e;
        }
    }

    private void cleanup(String filename) {
        try {
            VideoStorage.delete(filename);
        } catch (IOException e) {
            getServletContext().log("Không thể xóa tệp video sau khi upload thất bại", e);
        }
    }
}
