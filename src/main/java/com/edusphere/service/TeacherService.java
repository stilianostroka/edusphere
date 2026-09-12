package com.edusphere.service;

import com.edusphere.entity.Role;
import com.edusphere.entity.Teacher;
import com.edusphere.entity.User;
import com.edusphere.repository.TeacherRepository;
import com.edusphere.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeacherService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public TeacherService(UserRepository userRepository, TeacherRepository teacherRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Teacher createTeacher(String email, String rawPassword, String firstName, String lastName){
        if(userRepository.existsByEmail(email))
            throw new IllegalArgumentException("A user with email " + email + " already exists");

        User user = new User(email,passwordEncoder.encode(rawPassword), Role.TEACHER);
        userRepository.save(user);

        Teacher teacher = new Teacher(user,firstName,lastName);
        teacherRepository.save(teacher);

        return teacher;
    }
}


