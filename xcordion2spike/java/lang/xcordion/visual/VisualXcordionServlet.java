package xcordion.visual;

import xcordion.lang.java.JDomTestDocument;
import xcordion.api.ResourceReference;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

public class VisualXcordionServlet extends HttpServlet {
    private final VisualXcordionServer server;

    public VisualXcordionServlet(VisualXcordionServer server) {
        this.server = server;
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        httpServletResponse.setContentType("text/html");
        byte[] document = server.getLatestDocument();
        if (document != null) {
            httpServletResponse.getOutputStream().write(document);
        }
    }
}
