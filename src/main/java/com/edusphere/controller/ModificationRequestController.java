package com.edusphere.controller;

import com.edusphere.dto.*;
import com.edusphere.entity.ModificationRequest;
import com.edusphere.security.CurrentUserProvider;
import com.edusphere.service.ModificationRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/modification-requests")
public class ModificationRequestController {

    private final ModificationRequestService modificationRequestService;
    private final CurrentUserProvider currentUserProvider;

    public ModificationRequestController(ModificationRequestService modificationRequestService, CurrentUserProvider currentUserProvider) {
        this.modificationRequestService = modificationRequestService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping("/topic")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ModificationResponse> requestTopicChange(@RequestBody ModificationTopicRequest request) {
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        ModificationRequest modificationRequest = modificationRequestService.requestTopicChange(
                teacherId, request.topicId(), request.proposedTopicName(),
                request.proposedDescription(), request.explanation()
        );
        return ResponseEntity.ok(toResponse(modificationRequest));
    }

    @PostMapping("/grade")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ModificationResponse> requestGradeChange(@RequestBody GradeChangeRequest request) {
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        ModificationRequest modificationRequest = modificationRequestService.requestGradeChange(
                teacherId, request.lessonRecordId(), request.proposedGrade(), request.explanation()
        );
        return ResponseEntity.ok(toResponse(modificationRequest));
    }

    @PostMapping("/absence")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ModificationResponse> requestAbsenceChange(@RequestBody AbsenceChangeRequest request) {
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        ModificationRequest modificationRequest = modificationRequestService.requestAbsenceChange(
                teacherId, request.lessonRecordId(), request.explanation()
        );
        return ResponseEntity.ok(toResponse(modificationRequest));
    }

    @PostMapping("/exam-grade")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ModificationResponse> requestExamGradeChange(@RequestBody ExamGradeChangeRequest request) {
        Long teacherId = currentUserProvider.getCurrentTeacherId();
        ModificationRequest modificationRequest = modificationRequestService.requestExamGradeChange(
                teacherId, request.semesterExamGradeId(), request.proposedExamGrade(), request.explanation()
        );
        return ResponseEntity.ok(toResponse(modificationRequest));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModificationResponse> approve(@PathVariable Long id) {
        Long adminUserId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(toResponse(modificationRequestService.approveRequest(id, adminUserId)));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModificationResponse> reject(@PathVariable Long id) {
        Long adminUserId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(toResponse(modificationRequestService.rejectRequest(id, adminUserId)));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ModificationResponse>> getPending() {
        return ResponseEntity.ok(modificationRequestService.getPending().stream().map(this::toResponse).toList());
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ModificationResponse>> getByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(modificationRequestService.getByTeacher(teacherId).stream().map(this::toResponse).toList());
    }

    private ModificationResponse toResponse(ModificationRequest modificationRequest) {
        return new ModificationResponse(
                modificationRequest.getId(),
                modificationRequest.getRequestedBy().getId(),
                modificationRequest.getRequestedBy().getFirstName() + " " + modificationRequest.getRequestedBy().getLastName(),
                modificationRequest.isForTopic() ? modificationRequest.getTopic().getId() : null,
                modificationRequest.isForLessonRecord() ? modificationRequest.getLessonRecord().getId() : null,
                modificationRequest.isForExamGrade() ? modificationRequest.getSemesterExamGrade().getId() : null,
                modificationRequest.getProposedGrade(),
                modificationRequest.isProposedAbsent(),
                modificationRequest.getProposedTopicName(),
                modificationRequest.getProposedDescription(),
                modificationRequest.getProposedExamGrade(),
                modificationRequest.getExplanation(),
                modificationRequest.getStatus().name(),
                modificationRequest.getCreatedAt(),
                modificationRequest.getReviewedAt()
        );
    }
}
