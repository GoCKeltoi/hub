package hub.web;

import hub.config.Config;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class LoginServlet  extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServlet.class);

    private static final Instant ONE_HOUR = Instant.now().plusSeconds(3600);
    private final LoginService service;
    private final JwtResolver jwtResolver;

    public LoginServlet(LoginService service) {
        this.service = service;
        this.jwtResolver = new JwtResolver();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        //resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        Customer c = new Customer(login, password);
        service.save(c);
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
    }

   // val secret = cfg("commercial.vi.secret").getBytes
   // val expireIn1h = Instant.now().plusSeconds(3600)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        Customer c = service.find(login, password);
        if(null != c){
            String token = token(c.getId(), ONE_HOUR);
            resp.getWriter().print(token);
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        }

    }

    private String token(String custId, Instant expiration) {
        return Jwts.builder()
                .claim("custid", custId)
                .signWith(SignatureAlgorithm.HS512, Config.mustExist("secret"))
                .setExpiration(Date.from(expiration))
                .compact();
    }
}
