package com.fontaneriaxinarro.backend.web.restcontroller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fontaneriaxinarro.backend.model.dto.LoginRequest;
import com.fontaneriaxinarro.backend.model.dto.LoginResponse;
import com.fontaneriaxinarro.backend.security.JwtUtil;
import com.fontaneriaxinarro.backend.security.UserDetailsImpl;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                                    new UsernamePasswordAuthenticationToken(request.username(), request.password())   
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails.getUsername());

        return new LoginResponse(token, userDetails.getUsername(), userDetails.getFullName());
    }

}
