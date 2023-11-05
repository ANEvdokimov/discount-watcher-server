package an.evdokimov.discount.watcher.server.security.filter;

import an.evdokimov.discount.watcher.server.security.user.model.User;
import an.evdokimov.discount.watcher.server.security.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;

import static an.evdokimov.discount.watcher.server.security.exception.AuthenticationExceptionCode.PRINCIPAL_NOT_FOUND;

@Slf4j
public class UserLoginExtractorAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private static final String PRINCIPAL_HEADER_NAME = "Authenticated-User";

    private final UserService userService;


    public UserLoginExtractorAuthenticationProcessingFilter(UserService userService) {
        super("/**");
        this.userService = userService;
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                         FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.debug("attempt authentication");

        String login = request.getHeader(PRINCIPAL_HEADER_NAME);
        if (login == null) {
            log.info(PRINCIPAL_NOT_FOUND.getMessage());
            throw new AuthenticationCredentialsNotFoundException(PRINCIPAL_NOT_FOUND.getMessage());
        }

        User user = userService.getOrCreate(login);

        UserDetailsAuthenticationToken authentication = new UserDetailsAuthenticationToken(user);
        authentication.setAuthenticated(true);
        return authentication;
    }
}
