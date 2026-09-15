package com.edusphere.security;

import com.edusphere.entity.Parent;
import com.edusphere.entity.Teacher;
import com.edusphere.entity.User;
import com.edusphere.repository.ParentRepository;
import com.edusphere.repository.TeacherRepository;
import com.edusphere.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;

    public CurrentUserProvider(UserRepository userRepository, TeacherRepository teacherRepository, ParentRepository parentRepository) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.parentRepository = parentRepository;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("No authenticated user found for the current request"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public Teacher getCurrentTeacher() {
        User user = getCurrentUser();
        return teacherRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("The authenticated account is not a teacher"));
    }

    public Long getCurrentTeacherId() {
        return getCurrentTeacher().getId();
    }

    public Parent getCurrentParent() {
        User user = getCurrentUser();
        return parentRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("The authenticated account is not a parent"));
    }

    public Long getCurrentParentId() {
        return getCurrentParent().getId();
    }
}