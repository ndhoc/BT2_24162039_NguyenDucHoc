package lt_web.controllers;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lt_web.entity.Video;
import lt_web.service.VideoService;
import lt_web.service.impl.VideoServiceImpl;
import lt_web.utils.JspView;

/** Danh sách tất cả video, phân trang 6 video mỗi trang. */
@WebServlet("/video")
public class VideoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final VideoService videos = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = 1;
        String raw = req.getParameter("page");
        if (raw != null && !raw.isBlank()) {
            try { page = Math.max(1, Integer.parseInt(raw.trim())); }
            catch (NumberFormatException ignored) { page = 1; }
        }

        int pageCount = videos.pageCount();
        if (page > pageCount) page = pageCount;

        List<Video> pageItems = videos.page(page);
        req.setAttribute("videoList", pageItems);
        req.setAttribute("page", page);
        req.setAttribute("pageCount", pageCount);
        req.setAttribute("total", videos.count());
        JspView.render(req, resp, "/views/video-list.jsp");
    }
}
