package com.pcalouche.spat.restservices.security.provider;

import com.pcalouche.spat.restservices.api.entity.User;
import com.pcalouche.spat.restservices.api.user.service.UserService;
import com.pcalouche.spat.restservices.security.authentication.JwtAuthenticationToken;
import com.pcalouche.spat.restservices.security.util.SecurityUtils;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;

    public JwtAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Try to validate the provided authentication as JwtAuthenticationToken
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        if (jwtAuthenticationToken.getCredentials() == null) {
            throw new BadCredentialsException("JSON web token was empty.");
        }
        String token = jwtAuthenticationToken.getCredentials().toString();
        Claims claims = SecurityUtils.getClaimsFromToken(token);
        String subject = claims.getSubject();

        // When a refresh token happens double check the account status and the authorities
        // with what is in the database to ensure the account is still active.
        List<SimpleGrantedAuthority> simpleGrantedAuthorities;
        if ("refreshToken".equals(authentication.getDetails())) {
            User user = userService.getByUsername(subject);
            simpleGrantedAuthorities = user.getAuthorities();
        } else {
            @SuppressWarnings("unchecked")
            List<String> authorities = claims.get(SecurityUtils.CLAIMS_AUTHORITIES_KEY, List.class);
            simpleGrantedAuthorities = authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        // Authentication was good, so return a good authentication without credentials so the request can continue
        return new JwtAuthenticationToken(subject, null, simpleGrantedAuthorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
