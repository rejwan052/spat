package com.pcalouche.spat.security.filter;

import com.pcalouche.spat.security.util.SecurityUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class JwtAuthenticationRequestMatcher implements RequestMatcher {
    private final OrRequestMatcher ignoredMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher(SecurityUtils.LOGIN_PATH)
    );
    private final OrRequestMatcher filteredMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher(SecurityUtils.AUTHENTICATED_PATH)
    );

    @Override
    public boolean matches(HttpServletRequest request) {
        return !ignoredMatcher.matches(request) && filteredMatcher.matches(request);
    }
}
