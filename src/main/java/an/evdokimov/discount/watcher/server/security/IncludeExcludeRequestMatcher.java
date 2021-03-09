package an.evdokimov.discount.watcher.server.security;

import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

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
