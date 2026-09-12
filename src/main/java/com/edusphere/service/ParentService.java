package com.edusphere.service;

import com.edusphere.entity.Parent;
import com.edusphere.entity.Role;
import com.edusphere.entity.User;
import com.edusphere.repository.ParentRepository;
import com.edusphere.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParentService {

    private final UserRepository userRepository;
    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;
    public ParentService(UserRepository userRepository, ParentRepository parentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.parentRepository = parentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Parent createParent(String email, String rawPassword, String firstName, String lastName){
        if(userRepository.existsByEmail(email))
            throw new IllegalArgumentException("A user with email " + email + " already exists");

        User user = new User(email,passwordEncoder.encode(rawPassword), Role.PARENT);
        userRepository.save(user);

        Parent parent = new Parent(user,firstName,lastName);
        parentRepository.save(parent);

        return parent;
    }
}


