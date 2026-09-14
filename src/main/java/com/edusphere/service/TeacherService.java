package com.edusphere.service;

import com.edusphere.entity.Gender;
import com.edusphere.entity.Role;
import com.edusphere.entity.Teacher;
import com.edusphere.entity.User;
import com.edusphere.repository.TeacherRepository;
import com.edusphere.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Teacher createTeacher(String email, String rawPassword, String firstName, String lastName, String gender){
        if(userRepository.existsByEmail(email))
            throw new IllegalArgumentException("A user with email " + email + " already exists");

        User user = new User(email, passwordEncoder.encode(rawPassword), Role.TEACHER);
        userRepository.save(user);

        Teacher teacher = new Teacher(user, firstName, lastName);
        teacher.setGender(Gender.valueOf(gender.toUpperCase()));
        teacherRepository.save(teacher);

        return teacher;
    }

    public Teacher updateTeacher(Long teacherId, String newEmail, String newFirstName, String newLastName) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(()-> new IllegalArgumentException("No teacher found with that ID."));
        User user = teacher.getUser();

        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("A user with email " + newEmail + " already exists");
        }

        user.setEmail(newEmail);
        userRepository.save(user);

        teacher.setFirstName(newFirstName);
        teacher.setLastName(newLastName);
        teacherRepository.save(teacher);

        return teacher;
    }

    public Teacher getById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No teacher exists with this ID."));
    }

    public List<Teacher> getAll(){
        return teacherRepository.findAll();
    }
}


