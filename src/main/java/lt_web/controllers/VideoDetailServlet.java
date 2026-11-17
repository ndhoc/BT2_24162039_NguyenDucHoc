package lt_web.controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lt_web.entity.Video;
import lt_web.service.VideoService;
import lt_web.service.impl.VideoServiceImpl;

/** Trang chi tiết và phát video. */
@WebServlet("/video/detail")
public class VideoDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final VideoService videos = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
            if (id <= 0) throw new NumberFormatException();
        } catch (NumberFormatException | NullPointerException e) {
            resp.sendError(404, "Video không tồn tại.");
            return;
        }
        Video video = videos.get(id);
        if (video == null) {
            resp.sendError(404, "Video không tồn tại.");
            return;
        }
        // Trang chủ hiển thị video của mọi tài khoản nên phần xem chi tiết công khai.
        req.setAttribute("video", video);
        req.getRequestDispatcher("/views/video-detail.jsp").forward(req, resp);
    }
}
