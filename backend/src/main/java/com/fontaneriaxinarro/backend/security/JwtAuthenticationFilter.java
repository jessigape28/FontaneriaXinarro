package com.fontaneriaxinarro.backend.security;

import java.io.IOException;

import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthCookieProvider authCookieProvider;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
            UserDetailsServiceImpl userDetailsService,
            AuthCookieProvider authCookieProvider) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authCookieProvider = authCookieProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Cookie cookie = WebUtils.getCookie(request, authCookieProvider.getCookieName());

        if (cookie != null) {
            String token = cookie.getValue();

            if (jwtUtil.isTokenValid(token)) {

                String username = jwtUtil.extractUsername(token);
                UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
