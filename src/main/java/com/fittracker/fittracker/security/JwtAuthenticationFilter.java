package com.fittracker.fittracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";
    private static final int TOKEN_BEGIN_INDEX = 7;

    private final JwtUtils jwtUtils;

    @Autowired
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (hasText(token) && jwtUtils.isTokenValid(token)) {
            setAuthenticationInSecurityContext(request, token);
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authorizationHeaderContent = request.getHeader(AUTHORIZATION_HEADER_NAME);
        if (hasText(authorizationHeaderContent) && authorizationHeaderContent.startsWith(AUTHORIZATION_HEADER_PREFIX)) {
            return authorizationHeaderContent.substring(TOKEN_BEGIN_INDEX);
        }
        return null;
    }

    private void setAuthenticationInSecurityContext(HttpServletRequest request, String token) {
        UUID uuid = jwtUtils.getUserIdFromToken(token);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                uuid, "", List.of());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
