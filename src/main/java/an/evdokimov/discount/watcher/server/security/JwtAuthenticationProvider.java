package an.evdokimov.discount.watcher.server.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtUtils jwtUtils;

    public JwtAuthenticationProvider(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;

        log.debug("Trying to authenticate token {}", jwtAuthenticationToken.getToken());
        try {
            jwtUtils.validateToken(jwtAuthenticationToken.getToken());
        } catch (ExpiredJwtException e) {
            log.warn(e.getMessage());
            throw new CredentialsExpiredException(e.getMessage(), e);
        } catch (UnsupportedJwtException | SignatureException | MalformedJwtException e) {
            log.warn(e.getMessage());
            throw new BadCredentialsException(e.getMessage(), e);
        }
        log.debug("token {} is valid", jwtAuthenticationToken.getToken());

        UserDetails authenticatedUser;
        try {
            authenticatedUser = jwtUtils.getUserByToken(jwtAuthenticationToken.getToken());
        } catch (UsernameNotFoundException e) {
            log.warn("User with token {} not found", jwtAuthenticationToken.getToken());
            throw e;
        }

        jwtAuthenticationToken.setAuthenticated(true, authenticatedUser);

        return jwtAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
