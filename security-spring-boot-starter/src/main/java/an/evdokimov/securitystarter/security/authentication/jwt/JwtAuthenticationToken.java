package an.evdokimov.securitystarter.security.authentication.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private UserDetails authenticatedUser;
    private final String jwtToken;

    public JwtAuthenticationToken(String jwtToken) {
        super(null);
        this.jwtToken = jwtToken;
    }

    public JwtAuthenticationToken(String jwtToken, UserDetails authenticatedUser) {
        super(authenticatedUser.getAuthorities());
        this.authenticatedUser = authenticatedUser;
        this.jwtToken = jwtToken;
    }

    public static JwtAuthenticationToken unauthenticated(String jwtToken) {
        return new JwtAuthenticationToken(jwtToken);
    }

    public static JwtAuthenticationToken authenticated(String jwtToken, UserDetails authenticatedUser) {
        return new JwtAuthenticationToken(jwtToken, authenticatedUser);
    }

    public void setAuthenticated(boolean authenticated, UserDetails authenticatedUser) {
        setAuthenticated(authenticated);
        this.authenticatedUser = authenticatedUser;
    }

    public String getToken() {
        return jwtToken;
    }

    @Override
    public Object getCredentials() {
        return getToken();
    }

    @Override
    public Object getPrincipal() {
        return authenticatedUser;
    }
}
