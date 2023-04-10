package an.evdokimov.securitystarter.security.authentication.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    @Value("${application.security.jwt.secret}")
    private String jwtSecret;
    @Value("${application.security.jwt.expiration-time}")
    private long expirationTime;

    public String generateToken(String login) {
        log.debug("generate JWT for {}", login);

        return Jwts.builder()
                .setSubject(login)
                .setExpiration(Date.from(LocalDateTime.now().plusSeconds(expirationTime).toInstant(ZoneOffset.UTC)))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public void validateToken(String token) throws JwtException {
        log.debug("validate token {}", token);

        Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
    }

    public String getLoginFromToken(String token) {
        log.debug("getting login from token: {}", token);

        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
}
