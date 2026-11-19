package lt_web.controllers;

import java.io.IOException;
import java.nio.file.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lt_web.utils.ImageStorage;

@WebServlet({"/image", "/product-image/*"})
public class ImageServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Path directory = ImageStorage.directory();
        String name = req.getParameter("fname");
        if ("/product-image".equals(req.getServletPath())) {
            directory = ImageStorage.productDirectory();
            String path = req.getPathInfo();
            name = path == null ? null : path.substring(1);
        }
        if (!ImageStorage.isStoredName(name)) { resp.sendError(404); return; }
        Path file = directory.resolve(name);
        if (!Files.isRegularFile(file)) { resp.sendError(404); return; }
        resp.setContentType("image/png"); resp.setContentLengthLong(Files.size(file));
        Files.copy(file, resp.getOutputStream());
    }
}
