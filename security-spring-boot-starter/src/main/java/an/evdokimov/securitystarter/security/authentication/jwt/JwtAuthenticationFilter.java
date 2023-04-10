package an.evdokimov.securitystarter.security.authentication.jwt;

import an.evdokimov.securitystarter.security.authentication.AuthenticationErrorMessage;
import an.evdokimov.securitystarter.security.authentication.util.IncludeExcludeRequestMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${application.security.header.authentication}")
    private String authorisationHeaderName;

    public JwtAuthenticationFilter(String baseUrl, List<String> excludeUrls) {
        super("/**");

        RequestMatcher include = new OrRequestMatcher(new AntPathRequestMatcher(baseUrl));

        RequestMatcher exclude = new OrRequestMatcher(excludeUrls.stream().map(AntPathRequestMatcher::new)
                .collect(Collectors.toList()));

        this.setRequiresAuthenticationRequestMatcher(new IncludeExcludeRequestMatcher(include, exclude));
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager jwtAuthenticationManager) {
        super.setAuthenticationManager(jwtAuthenticationManager);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.debug("attempt authentication");

        String authorizationHeader = request.getHeader(authorisationHeaderName);
        if (authorizationHeader == null) {
            authorizationHeader = request.getParameter(authorisationHeaderName);
        }

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.info(AuthenticationErrorMessage.BAD_TOKEN.getMessage());
            throw new AuthenticationCredentialsNotFoundException(AuthenticationErrorMessage.BAD_TOKEN.getMessage());
        } else {
            String token = authorizationHeader.substring("Bearer".length()).trim();
            JwtAuthenticationToken authRequest = new JwtAuthenticationToken(token);
            return getAuthenticationManager().authenticate(authRequest);
        }
    }
}
