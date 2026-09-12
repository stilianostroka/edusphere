package com.edusphere.service;

import com.edusphere.entity.Role;
import com.edusphere.entity.User;
import com.edusphere.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createAnAdmin(String email, String rawPassword) {
        if (userRepository.existsByRole(Role.ADMIN))
            throw new IllegalArgumentException("An administrator already exists.");

        if (userRepository.existsByEmail(email))
            throw new IllegalStateException("This email already exists.");

        User admin = new User(email, passwordEncoder.encode(rawPassword), Role.ADMIN);
        userRepository.save(admin);
        return admin;
    }
}


