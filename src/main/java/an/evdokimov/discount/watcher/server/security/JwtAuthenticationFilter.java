package an.evdokimov.discount.watcher.server.security;

import an.evdokimov.discount.watcher.server.api.error.dto.response.ServerErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${header.authentication}")
    private String authorisationHeaderName;
    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(String baseUrl, List<String> excludeUrls, JwtUtils jwtUtils) {
        super("/**");
        this.jwtUtils = jwtUtils;

        RequestMatcher include = new OrRequestMatcher(new AntPathRequestMatcher(baseUrl));

        RequestMatcher exclude = new OrRequestMatcher(excludeUrls.stream().map(AntPathRequestMatcher::new)
                .collect(Collectors.toList()));

        this.setRequiresAuthenticationRequestMatcher(new IncludeExcludeRequestMatcher(include, exclude));

        setAuthenticationFailureHandler((req, res, e) -> {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ServerErrorResponse response = new ServerErrorResponse(e.getMessage());
            MAPPER.writeValue(res.getOutputStream(), response);
        });
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
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
            throws AuthenticationException, IOException, ServletException {
        log.debug("attempt authentication");

        String authorizationHeader = request.getHeader(authorisationHeaderName);
        if (authorizationHeader == null) {
            authorizationHeader = request.getParameter(authorisationHeaderName);
        }

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn(AuthenticationErrorMessage.BAD_TOKEN.getMessage() + ". token: {}", authorizationHeader);//TODO баг: вызывается при недоступности БД
            throw new AuthenticationCredentialsNotFoundException(AuthenticationErrorMessage.BAD_TOKEN.getMessage());
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {
            jwtUtils.validateToken(token);
            log.info("token {} is valid", token);
        } catch (ExpiredJwtException e) {
            log.warn(e.getMessage());
            throw new CredentialsExpiredException(e.getMessage(), e);
        } catch (UnsupportedJwtException | SignatureException | MalformedJwtException e) {
            log.warn(e.getMessage());
            throw new BadCredentialsException(e.getMessage(), e);
        }

        JwtAuthenticationToken authRequest = new JwtAuthenticationToken(jwtUtils.getUserByToken(token), token);

        return getAuthenticationManager().authenticate(authRequest);
    }
}
