package com.edusphere.controller;

import com.edusphere.dto.*;
import com.edusphere.entity.ModificationRequest;
import com.edusphere.service.ModificationRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modification-requests")
public class ModificationRequestController {

    private final ModificationRequestService modificationRequestService;

    public ModificationRequestController(ModificationRequestService modificationRequestService) {
        this.modificationRequestService = modificationRequestService;
    }

    @PostMapping("/topic")
    public ResponseEntity<ModificationResponse> requestTopicChange(@RequestBody ModificationTopicRequest request) {
        ModificationRequest modificationRequest = modificationRequestService.requestTopicChange(
                request.teacherId(), request.topicId(), request.proposedTopicName(),
                request.proposedDescription(), request.explanation()
        );
        return ResponseEntity.ok(toResponse(modificationRequest));
    }

    @PostMapping("/grade")
    public ResponseEntity<ModificationResponse> requestGradeChange(@RequestBody GradeChangeRequest request) {
        ModificationRequest modificationRequest = modificationRequestService.requestGradeChange(
                request.teacherId(), request.lessonRecordId(), request.proposedGrade(), request.explanation()
        );
        return ResponseEntity.ok(toResponse(modificationRequest));
    }

    @PostMapping("/exam-grade")
    public ResponseEntity<ModificationResponse> requestExamGradeChange(@RequestBody ExamGradeChangeRequest request) {
        ModificationRequest modificationRequest = modificationRequestService.requestExamGradeChange(
                request.teacherId(), request.semesterExamGradeId(), request.proposedExamGrade(), request.explanation()
        );
        return ResponseEntity.ok(toResponse(modificationRequest));
    }

    @PostMapping("/absence")
    public ResponseEntity<ModificationResponse> requestAbsenceChange(@RequestBody AbsenceChangeRequest request) {
        ModificationRequest modificationRequest = modificationRequestService.requestAbsenceChange(
                request.teacherId(), request.lessonRecordId(), request.explanation()
        );
        return ResponseEntity.ok(toResponse(modificationRequest));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ModificationResponse> approve(@PathVariable Long id, @RequestParam Long adminUserId) {
        return ResponseEntity.ok(toResponse(modificationRequestService.approveRequest(id, adminUserId)));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ModificationResponse> reject(@PathVariable Long id, @RequestParam Long adminUserId) {
        return ResponseEntity.ok(toResponse(modificationRequestService.rejectRequest(id, adminUserId)));
    }

    @GetMapping("/pending")
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