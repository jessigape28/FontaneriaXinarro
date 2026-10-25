package com.fontaneriaxinarro.backend.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieProvider {

    private final String cookieName;
    private final boolean secure;
    private final String sameSite;
    private final Duration maxAge;

    public AuthCookieProvider(@Value("${app.auth.cookie-name}") String cookieName,
            @Value("${app.auth.cookie-secure}") boolean secure,
            @Value("${app.auth.cookie-same-site}") String sameSite,
            @Value("${jwt.expiration-ms}") long expirationMS) {
        this.cookieName = cookieName;
        this.secure = secure;
        this.sameSite = sameSite;
        this.maxAge = Duration.ofMillis(expirationMS);
    }

    public String getCookieName() {
        return cookieName;
    }

    public ResponseCookie createCookie(String token) {
        return build(token, maxAge);
    }

    public ResponseCookie clearCookie() {
        return build("", Duration.ZERO);
    }

    private ResponseCookie build(String value, Duration age) {
        return ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/api")
                .maxAge(age)
                .build();
    }
}