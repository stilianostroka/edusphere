package com.edusphere.controller;

import com.edusphere.dto.DiplomaRequest;
import com.edusphere.dto.DiplomaResponse;
import com.edusphere.entity.Diploma;
import com.edusphere.service.DiplomaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diplomas")
public class DiplomaController {
    private final DiplomaService diplomaService;

    public DiplomaController(DiplomaService diplomaService) { this.diplomaService = diplomaService; }

    @GetMapping
    public ResponseEntity<List<DiplomaResponse>> getAll() {
        return ResponseEntity.ok(diplomaService.getAll().stream().map(this::toResponse).toList());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<DiplomaResponse>> getForStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(diplomaService.getForStudent(studentId).stream().map(this::toResponse).toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/generate")
    public ResponseEntity<DiplomaResponse> generate(@RequestBody DiplomaRequest request) {
        return ResponseEntity.ok(toResponse(diplomaService.generate(request.studentId(), request.academicYearId())));
    }

    private DiplomaResponse toResponse(Diploma diploma) {
        return new DiplomaResponse(diploma.getId(), diploma.getStudent().getId(),
                diploma.getStudent().getFirstName() + " " + diploma.getStudent().getLastName(),
                diploma.getAcademicYear().getId(), diploma.getAcademicYear().getAcademicYear(),
                diploma.getFileUrl(), diploma.getGeneratedAt());
    }
}
