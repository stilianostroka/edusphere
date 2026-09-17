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
    public Parent createParent(String email, String rawPassword, String firstName, String lastName, String gender,
                               String address, String phoneNumber){
        if(userRepository.existsByEmail(email))
            throw new IllegalArgumentException("A user with email " + email + " already exists");

        User user = new User(email,passwordEncoder.encode(rawPassword), Role.PARENT);
        userRepository.save(user);

        Parent parent = new Parent(user,firstName,lastName);
        parent.setGender(Gender.valueOf(gender.toUpperCase()));
        parent.setAddress(address);
        parent.setPhoneNumber(phoneNumber);
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

    @Transactional
    public Student linkStudent(Long parentId, Long studentId) {
        Parent parent = getById(parentId);
        Student student = studentService.getStudent(studentId);
        student.addParent(parent);
        return studentService.save(student);
    }

    @Transactional
    public Student unlinkStudent(Long parentId, Long studentId) {
        Parent parent = getById(parentId);
        Student student = studentService.getStudent(studentId);
        student.removeParent(parent);
        return studentService.save(student);
    }

    public List<Student> getChildren(Long parentId) {
        return studentService.getByParent(getById(parentId));
    }

    @Transactional
    public Parent updateParent(Long id, String email, String password, String firstName, String lastName,
                               String gender, String address, String phoneNumber) {
        Parent parent = getById(id);
        User user = parent.getUser();
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("A user with email " + email + " already exists");
        }
        user.setEmail(email);
        if (password != null && !password.isBlank()) user.setPasswordHash(passwordEncoder.encode(password));
        parent.setFirstName(firstName);
        parent.setLastName(lastName);
        parent.setGender(Gender.valueOf(gender.toUpperCase()));
        parent.setAddress(address);
        parent.setPhoneNumber(phoneNumber);
        userRepository.save(user);
        return parentRepository.save(parent);
    }
}
