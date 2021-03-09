package an.evdokimov.discount.watcher.server.security;

import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String jwtToken;

    public JwtAuthenticationToken(User user, String jwtToken) {
        super(user, null, user.getAuthorities());
        this.jwtToken = jwtToken;
    }

    public String getToken() {
        return jwtToken;
    }
}
