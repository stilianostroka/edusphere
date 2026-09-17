package com.edusphere.controller;

import com.edusphere.dto.CurrentUserResponse;
import com.edusphere.entity.Parent;
import com.edusphere.entity.Role;
import com.edusphere.entity.Teacher;
import com.edusphere.entity.User;
import com.edusphere.security.CurrentUserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class ProfileController {
    private final CurrentUserProvider currentUserProvider;

    public ProfileController(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    public ResponseEntity<CurrentUserResponse> me() {
        User user = currentUserProvider.getCurrentUser();
        Long profileId = null;
        String fullName = user.getEmail();
        if (user.getRole() == Role.TEACHER) {
            Teacher teacher = currentUserProvider.getCurrentTeacher();
            profileId = teacher.getId();
            fullName = teacher.getFullName();
        } else if (user.getRole() == Role.PARENT) {
            Parent parent = currentUserProvider.getCurrentParent();
            profileId = parent.getId();
            fullName = parent.getFirstName() + " " + parent.getLastName();
        }
        return ResponseEntity.ok(new CurrentUserResponse(
                user.getId(), profileId, user.getEmail(), user.getRole().name(), fullName
        ));
    }
}
