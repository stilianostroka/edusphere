package com.edusphere.service;

import com.edusphere.entity.Gender;
import com.edusphere.entity.Parent;
import com.edusphere.entity.Role;
import com.edusphere.entity.Student;
import com.edusphere.entity.User;
import com.edusphere.repository.ParentRepository;
import com.edusphere.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParentService {

    private final UserRepository userRepository;
    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentService studentService;

    public ParentService(UserRepository userRepository, ParentRepository parentRepository, PasswordEncoder passwordEncoder, StudentService studentService) {
        this.userRepository = userRepository;
        this.parentRepository = parentRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentService = studentService;
    }

    @Transactional
    public Parent createParent(String email, String rawPassword, String firstName, String lastName, String gender){
        if(userRepository.existsByEmail(email))
            throw new IllegalArgumentException("A user with email " + email + " already exists");

        User user = new User(email,passwordEncoder.encode(rawPassword), Role.PARENT);
        userRepository.save(user);

        Parent parent = new Parent(user,firstName,lastName);
        parent.setGender(Gender.valueOf(gender.toUpperCase()));
        parentRepository.save(parent);

        return parent;
    }

    public Parent getById(Long id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No parent found with id " + id));
    }

    public List<Parent> getAll() {
        return parentRepository.findAll();
    }

    public List<Parent> getParent(Long student_id){
        Student student = studentService.getStudent(student_id);
        return student.getParents().stream().toList();
    }
}