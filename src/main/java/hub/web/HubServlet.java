package hub.web;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class HubServlet  extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(HubServlet.class);

    private static final Type TYPE = new TypeToken<Map<String, Object>>(){}.getType();
    private final Gson gson;
    private final HubService service;
    private final JwtResolver jwtResolver;

    public HubServlet(Gson gson, HubService service) {
        this.gson = gson;
        this.service = service;
        this.jwtResolver = new JwtResolver();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        //resp.setStatus(HttpServletResponse.SC_OK);
        Claims claims = jwtResolver.jwsClaims(req);
        if(null != claims){
            String customerId = claims.get("custid", String.class);
            resp.setContentType("application/json");
            try {
                Map<String, Object> values = gson.fromJson(req.getReader(), TYPE);
                Event e = new Event(customerId, values);
                service.save(e);
                LOGGER.info("" + e);
                gson.toJson(e, resp.getWriter());
            } catch (IOException e) {
                e.printStackTrace();
            }
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Event e = gson.fromJson(req.getReader(), Event.class);
            service.save(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        gson.toJson(service.find(id), resp.getWriter());
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        service.delete(id);
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
    }
}
