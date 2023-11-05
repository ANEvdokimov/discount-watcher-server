package an.evdokimov.discount.watcher.server.security.filter;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserDetailsAuthenticationToken extends AbstractAuthenticationToken {
    private final UserDetails principal;

    public UserDetailsAuthenticationToken(UserDetails principal) {
        super(principal.getAuthorities());
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        throw new UnsupportedOperationException();
    }
}
