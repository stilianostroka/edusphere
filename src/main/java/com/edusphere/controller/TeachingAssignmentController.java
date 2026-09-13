package com.edusphere.controller;

import com.edusphere.dto.TeachingAssignmentRequest;
import com.edusphere.dto.TeachingAssignmentResponse;
import com.edusphere.entity.TeachingAssignment;
import com.edusphere.service.TeachingAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teaching-assignments")
public class TeachingAssignmentController {

    private final TeachingAssignmentService teachingAssignmentService;


    public TeachingAssignmentController(TeachingAssignmentService teachingAssignmentService){
        this.teachingAssignmentService = teachingAssignmentService;
    }

    @GetMapping
    public ResponseEntity<List<TeachingAssignmentResponse>> getAll() {
        return ResponseEntity.ok(teachingAssignmentService.getAll().stream().map(this::toResponse).toList());
    }

    @GetMapping("/{teacherId}")
    public ResponseEntity<List<TeachingAssignmentResponse>> getByTeacherId(@PathVariable Long teacherId){
        return ResponseEntity.ok(teachingAssignmentService.getByTeacher(teacherId).stream().map(this::toResponse).toList());
    }

    @PostMapping
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