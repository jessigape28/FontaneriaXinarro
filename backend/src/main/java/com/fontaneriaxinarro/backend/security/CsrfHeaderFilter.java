package com.fontaneriaxinarro.backend.security;

import java.io.IOException;
import java.util.Set;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CsrfHeaderFilter extends OncePerRequestFilter {

    private static final String REQUIRED_HEADER = "X-Requested-With";
    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!SAFE_METHODS.contains(request.getMethod()) && request.getHeader(REQUIRED_HEADER) == null) {

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter()
                    .write("{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Missing required header\"}");

            return;
        }

        filterChain.doFilter(request, response);
    }

}
