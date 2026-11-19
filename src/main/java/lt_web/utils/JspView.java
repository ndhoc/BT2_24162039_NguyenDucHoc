package lt_web.utils;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class JspView {
    private JspView() { }

    public static void render(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        // A forward commits the response on Tomcat 11 before SiteMesh can decorate it.
        request.getRequestDispatcher(path).include(request, response);
    }
}
