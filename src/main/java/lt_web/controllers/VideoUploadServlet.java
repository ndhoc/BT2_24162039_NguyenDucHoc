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
        req.getRequestDispatcher("/views/video-upload.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User account = (User) req.getSession().getAttribute("account");
        if (account == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String newFile = null;
        boolean persisted = false;
        try {
            Video video = new Video();
            video.setTitle(req.getParameter("title"));
            String description = req.getParameter("description");
            video.setDescription(description == null || description.isBlank() ? null : description);
            video.setUser(account);
            video.setCreatedAt(Instant.now());

            Part part = req.getPart("videoFile");
            newFile = VideoStorage.save(part);
            if (newFile == null) throw new IllegalArgumentException("Vui lòng chọn tệp video.");
            video.setFileName(newFile);

            videos.insert(video);
            persisted = true;
            resp.sendRedirect(req.getContextPath() + "/video?saved=1");
        } catch (IllegalArgumentException e) {
            cleanup(newFile);
            req.setAttribute("alert", e.getMessage());
            req.setAttribute("titleValue", req.getParameter("title"));
            req.setAttribute("descriptionValue", req.getParameter("description"));
            req.getRequestDispatcher("/views/video-upload.jsp").forward(req, resp);
        } catch (IllegalStateException e) {
            cleanup(newFile);
            req.setAttribute("alert", "Video vượt quá dung lượng tối đa 50 MB.");
            req.setAttribute("titleValue", req.getParameter("title"));
            req.setAttribute("descriptionValue", req.getParameter("description"));
            req.getRequestDispatcher("/views/video-upload.jsp").forward(req, resp);
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
