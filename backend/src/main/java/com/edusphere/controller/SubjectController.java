package com.edusphere.controller;

import com.edusphere.dto.SubjectRequest;
import com.edusphere.dto.SubjectResponse;
import com.edusphere.entity.Subject;
import com.edusphere.service.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> showAll(){
        List<SubjectResponse> response = subjectService.getAll().stream()
                .map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/program-year/{programYear}")
    public ResponseEntity<List<SubjectResponse>> findByProgramYear(@PathVariable Integer programYear){
        List<SubjectResponse> response = subjectService.getByProgramYear(programYear).stream()
                .map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectResponse> create(@RequestBody SubjectRequest subjectRequest){
        Subject subject = subjectService.createSubject(subjectRequest.subjectName(),
                subjectRequest.totalHours(), subjectRequest.programYear(), subjectRequest.code());

        return ResponseEntity.ok(toResponse(subject));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectResponse> update(@PathVariable Long id, @RequestBody SubjectRequest subjectRequest) {
        Subject subject = subjectService.updateSubject(id, subjectRequest.subjectName(), subjectRequest.code(),
                subjectRequest.totalHours(), subjectRequest.programYear());
        return ResponseEntity.ok(toResponse(subject));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    private SubjectResponse toResponse(Subject subject) {
        return new SubjectResponse(
                subject.getId(), subject.getSubjectName(), subject.getCode(),
                subject.getTotalHours(), subject.getProgramYear()
        );
    }
}
