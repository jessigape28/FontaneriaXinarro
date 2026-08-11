package com.fontaneriaxinarro.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fontaneriaxinarro.backend.model.entity.User;
import com.fontaneriaxinarro.backend.repository.IUserRepository;

@Configuration
public class DataSeederConfig {

    @Bean
    public CommandLineRunner seedUsers(IUserRepository userRepository,
                                        PasswordEncoder passwordEncoder,
                                        @Value("${app.user1.username}") String user1Username,
                                        @Value("${app.user1.password}") String user1Password,
                                        @Value("${app.user1.fullname}") String user1Fullname,
                                        @Value("${app.user2.username}") String user2Username,
                                        @Value("${app.user2.password}") String user2Password,
                                        @Value("${app.user2.fullname}") String user2Fullname
    ) {
        return args -> {
            createUserIfNotExists(userRepository, passwordEncoder, user1Username, user1Password, user1Fullname);
            createUserIfNotExists(userRepository, passwordEncoder, user2Username, user2Password, user2Fullname);
        };
    }

    private void createUserIfNotExists(IUserRepository userRepository, PasswordEncoder passwordEncoder, String username, String rawPassword, String fullName) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setFullName(fullName);
            userRepository.save(user);
        }
    }

}
