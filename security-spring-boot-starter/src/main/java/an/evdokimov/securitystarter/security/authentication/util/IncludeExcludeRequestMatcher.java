package an.evdokimov.securitystarter.security.authentication.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class IncludeExcludeRequestMatcher implements RequestMatcher {
    private final RequestMatcher include;
    private final RequestMatcher exclude;

    public IncludeExcludeRequestMatcher(RequestMatcher include, RequestMatcher exclude) {
        this.include = include;
        this.exclude = exclude;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return include.matches(request) && !exclude.matches(request);
    }
}
