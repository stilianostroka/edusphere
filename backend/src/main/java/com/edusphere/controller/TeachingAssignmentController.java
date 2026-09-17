package com.edusphere.controller;

import com.edusphere.dto.TeachingAssignmentRequest;
import com.edusphere.dto.TeachingAssignmentResponse;
import com.edusphere.entity.TeachingAssignment;
import com.edusphere.service.TeachingAssignmentService;
import com.edusphere.security.CurrentUserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/teaching-assignments")
public class TeachingAssignmentController {

    private final TeachingAssignmentService teachingAssignmentService;
    private final CurrentUserProvider currentUserProvider;


    public TeachingAssignmentController(TeachingAssignmentService teachingAssignmentService, CurrentUserProvider currentUserProvider){
        this.teachingAssignmentService = teachingAssignmentService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    public ResponseEntity<List<TeachingAssignmentResponse>> getAll(@RequestParam(required = false) Long academicYearId) {
        List<TeachingAssignment> assignments = teachingAssignmentService.getAll();
        if (academicYearId != null) {
            assignments = assignments.stream()
                    .filter(a -> a.getSchoolClass().getAcademicYear().getId().equals(academicYearId)).toList();
        }
        return ResponseEntity.ok(assignments.stream().map(this::toResponse).toList());
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<TeachingAssignmentResponse>> getByTeacherId(@PathVariable Long teacherId){
        return ResponseEntity.ok(teachingAssignmentService.getByTeacher(teacherId).stream().map(this::toResponse).toList());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<TeachingAssignmentResponse>> getMine(@RequestParam(required = false) Long academicYearId){
        List<TeachingAssignment> assignments = teachingAssignmentService.getByTeacher(currentUserProvider.getCurrentTeacherId());
        if (academicYearId != null) {
            assignments = assignments.stream()
                    .filter(a -> a.getSchoolClass().getAcademicYear().getId().equals(academicYearId)).toList();
        }
        return ResponseEntity.ok(assignments.stream().map(this::toResponse).toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeachingAssignmentResponse> create(@RequestBody TeachingAssignmentRequest request) {
        TeachingAssignment assignment = teachingAssignmentService.createAssignment(request.classId(),
                request.teacherId(), request.subjectId());
        return ResponseEntity.ok(toResponse(assignment));
    }

    private TeachingAssignmentResponse toResponse(TeachingAssignment assignment) {
        return new TeachingAssignmentResponse(
                assignment.getId(),
                assignment.getSchoolClass().getId(),
                assignment.getSchoolClass().getClassName(),
                assignment.getSubject().getId(),
                assignment.getSubject().getSubjectName(),
                assignment.getSubject().getCode(),
                assignment.getTeacher().getId(),
                assignment.getTeacher().getFullName()
        );
    }
}
