package com.edusphere.controller;

import com.edusphere.dto.ParentRequest;
import com.edusphere.dto.ParentResponse;
import com.edusphere.entity.Parent;
import com.edusphere.service.ParentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parents")
public class ParentController {

    private final ParentService parentService;

    public ParentController(ParentService parentService) {
        this.parentService = parentService;
    }

    @GetMapping
    public ResponseEntity<List<ParentResponse>> getAll() {
        return ResponseEntity.ok(parentService.getAll().stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(parentService.getById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ParentResponse> create(@RequestBody ParentRequest request){
        Parent parent = parentService.createParent(
                request.email(), request.password(), request.firstName(), request.lastName(), request.gender()
        );
        return ResponseEntity.ok(toResponse(parent));
    }

    private ParentResponse toResponse(Parent parent) {
        return new ParentResponse(
                parent.getId(),
                parent.getUser().getEmail(),
                parent.getFirstName(),
                parent.getLastName(),
                parent.getGender() != null ? parent.getGender().name() : null,
                parent.getAddress(),
                parent.getPhoneNumber()
        );
    }
}