package com.edusphere.service;

import com.edusphere.entity.Role;
import com.edusphere.entity.User;
import com.edusphere.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createAnAdmin(String email, String rawPassword) {
        if (userRepository.existsByRole(Role.ADMIN))
            throw new IllegalArgumentException("An administrator already exists.");

        if (userRepository.existsByEmail(email))
            throw new IllegalStateException("This email already exists.");

        User admin = new User(email, rawPassword, Role.ADMIN);
        userRepository.save(admin);
        return admin;
    }
}


