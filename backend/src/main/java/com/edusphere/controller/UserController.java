package com.edusphere.controller;

import com.edusphere.dto.AdminCreateRequest;
import com.edusphere.dto.AdminResponse;
import com.edusphere.entity.User;
import com.edusphere.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<AdminResponse> createAdmin(@RequestBody AdminCreateRequest request) {
        User admin = userService.createAnAdmin(request.email(), request.password());
        return ResponseEntity.ok(new AdminResponse(admin.getId(), admin.getEmail()));
    }
}

