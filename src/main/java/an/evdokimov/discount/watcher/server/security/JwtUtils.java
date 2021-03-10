package an.evdokimov.discount.watcher.server.security;

import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.database.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration-time}")
    private long expirationTime;
    private final UserRepository userRepository;

    public JwtUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

    public User getUserByToken(String token) {
        log.debug("getting user by token: {}", token);

        return userRepository.findByLogin(getLoginFromToken(token)).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
    }
}
