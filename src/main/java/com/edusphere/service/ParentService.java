package com.edusphere.service;

import com.edusphere.entity.Parent;
import com.edusphere.entity.Role;
import com.edusphere.entity.User;
import com.edusphere.repository.ParentRepository;
import com.edusphere.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParentService {

    private final UserRepository userRepository;
    private final ParentRepository parentRepository;

    public ParentService(UserRepository userRepository, ParentRepository parentRepository) {
        this.userRepository = userRepository;
        this.parentRepository = parentRepository;
    }

    @Transactional
    public Parent createParent(String email, String rawPassowrd, String firstName, String lastName){
        if(userRepository.existsByEmail(email))
            throw new IllegalArgumentException("A user with email " + email + " already exists");

        User user = new User(email,rawPassowrd, Role.PARENT); // TODO: replace with real password hashing once security layer is built
        userRepository.save(user);

        Parent parent = new Parent(user,firstName,lastName);
        parentRepository.save(parent);

        return parent;
    }
}


