package com.fontaneriaxinarro.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fontaneriaxinarro.backend.exception.ResourceNotFoundException;
import com.fontaneriaxinarro.backend.model.entity.User;
import com.fontaneriaxinarro.backend.repository.IUserRepository;

@Component
public class CurrentUserProvider {

    private final IUserRepository userRepository;

    public CurrentUserProvider(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}
