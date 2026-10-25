package com.fontaneriaxinarro.backend.web.restcontroller;

import com.fontaneriaxinarro.backend.security.AuthCookieProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fontaneriaxinarro.backend.model.dto.LoginRequest;
import com.fontaneriaxinarro.backend.model.dto.UserInfoResponse;
import com.fontaneriaxinarro.backend.security.JwtUtil;
import com.fontaneriaxinarro.backend.security.UserDetailsImpl;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthCookieProvider authCookieProvider;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            AuthCookieProvider authCookieProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.authCookieProvider = authCookieProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<UserInfoResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        authCookieProvider.createCookie(token).toString())
                .body(new UserInfoResponse(userDetails.getUsername(), userDetails.getFullName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE,
                        authCookieProvider.clearCookie().toString())
                .build();
    }

    @GetMapping("/me")
    public UserInfoResponse me(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return new UserInfoResponse(userDetails.getUsername(), userDetails.getFullName());
    }

}
