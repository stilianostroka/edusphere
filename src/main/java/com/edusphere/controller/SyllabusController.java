package com.edusphere.controller;

import com.edusphere.dto.SyllabusRequest;
import com.edusphere.dto.SyllabusResponse;
import com.edusphere.entity.Syllabus;
import com.edusphere.service.SyllabusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/syllabi")
public class SyllabusController {

    private final SyllabusService syllabusService;

    public SyllabusController(SyllabusService syllabusService) {
        this.syllabusService = syllabusService;
    }

    @PostMapping
    public ResponseEntity<SyllabusResponse> upload(@RequestBody SyllabusRequest request) {
        Syllabus syllabus = syllabusService.uploadSyllabus(
                request.teachingAssignmentId(), request.fileUrl(), request.originalFileName(), request.contentType()
        );
        return ResponseEntity.ok(toResponse(syllabus));
    }

    @GetMapping("/teaching-assignment/{teachingAssignmentId}")
    public ResponseEntity<SyllabusResponse> getByTeachingAssignment(@PathVariable Long teachingAssignmentId) {
        return ResponseEntity.ok(toResponse(syllabusService.getByTeachingAssignment(teachingAssignmentId)));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<SyllabusResponse>> getByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(syllabusService.getByTeacher(teacherId).stream().map(this::toResponse).toList());
    }

    private SyllabusResponse toResponse(Syllabus syllabus) {
        return new SyllabusResponse(
                syllabus.getId(),
                syllabus.getTeachingAssignment().getId(),
                syllabus.getTeachingAssignment().getSchoolClass().getClassName(),
                syllabus.getTeachingAssignment().getSubject().getSubjectName(),
                syllabus.getFileUrl(),
                syllabus.getOriginalFileName(),
                syllabus.getContentType(),
                syllabus.getUploadedAt()
        );
    }
}

