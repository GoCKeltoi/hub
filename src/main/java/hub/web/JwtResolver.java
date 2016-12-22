package hub.web;

import hub.config.Config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

import javax.servlet.http.HttpServletRequest;

public class JwtResolver {

    private final byte[] secret = Config.mustExist("secret").getBytes();
    private final JwtParser parser = Jwts.parser().setSigningKey(secret);

    public Claims jwsClaims(HttpServletRequest request) {
        return parseClaims(removeBearer(request.getHeader("Authorization")));
    }

    private String removeBearer(String token) {
        return token.replace("Bearer ", "");
    }

    private Claims parseClaims(String token) {
        return parser.parseClaimsJws(token).getBody();
    }

}
