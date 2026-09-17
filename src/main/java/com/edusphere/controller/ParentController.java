package com.edusphere.controller;

import com.edusphere.dto.ParentRequest;
import com.edusphere.dto.ParentResponse;
import com.edusphere.dto.LinkParentRequest;
import com.edusphere.dto.StudentResponse;
import com.edusphere.entity.Parent;
import com.edusphere.service.ParentService;
import com.edusphere.security.CurrentUserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parents")
public class ParentController {

    private final ParentService parentService;
    private final CurrentUserProvider currentUserProvider;

    public ParentController(ParentService parentService, CurrentUserProvider currentUserProvider) {
        this.parentService = parentService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ParentResponse>> getAll() {
        return ResponseEntity.ok(parentService.getAll().stream().map(this::toResponse).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(parentService.getById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ParentResponse> create(@RequestBody ParentRequest request){
        Parent parent = parentService.createParent(
                request.email(), request.password(), request.firstName(), request.lastName(), request.gender(),
                request.address(), request.phoneNumber()
        );
        return ResponseEntity.ok(toResponse(parent));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ParentResponse> update(@PathVariable Long id, @RequestBody ParentRequest request) {
        return ResponseEntity.ok(toResponse(parentService.updateParent(
                id, request.email(), request.password(), request.firstName(), request.lastName(),
                request.gender(), request.address(), request.phoneNumber()
        )));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/students")
    public ResponseEntity<StudentResponse> linkStudent(@PathVariable Long id, @RequestBody LinkParentRequest request) {
        return ResponseEntity.ok(toStudentResponse(parentService.linkStudent(id, request.studentId())));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{parentId}/students/{studentId}")
    public ResponseEntity<Void> unlinkStudent(@PathVariable Long parentId, @PathVariable Long studentId) {
        parentService.unlinkStudent(parentId, studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentResponse>> getChildren(@PathVariable Long id) {
        return ResponseEntity.ok(parentService.getChildren(id).stream().map(this::toStudentResponse).toList());
    }

    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/me/students")
    public ResponseEntity<List<StudentResponse>> getMyChildren() {
        return ResponseEntity.ok(parentService.getChildren(currentUserProvider.getCurrentParentId())
                .stream().map(this::toStudentResponse).toList());
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

    private StudentResponse toStudentResponse(com.edusphere.entity.Student student) {
        com.edusphere.entity.SchoolClass schoolClass = student.getSchoolClass();
        return new StudentResponse(student.getId(), schoolClass != null ? schoolClass.getId() : null,
                schoolClass != null ? schoolClass.getClassName() : null,
                student.getFirstName(), student.getLastName(), student.getStatus().name(),
                student.getPersonalId(), student.getDateOfBirth(),
                student.getGender() != null ? student.getGender().name() : null,
                student.getAddress(), student.getEnrollmentDate());
    }
}
