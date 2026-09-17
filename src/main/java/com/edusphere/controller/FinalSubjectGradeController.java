package com.edusphere.controller;

import com.edusphere.dto.ClassGradeRosterEntry;
import com.edusphere.dto.FinalSubjectGradeResponse;
import com.edusphere.dto.ProjectGradeRequest;
import com.edusphere.dto.SubjectClassRosterResponse;
import com.edusphere.entity.*;
import com.edusphere.security.CurrentUserProvider;
import com.edusphere.service.GradingService;
import com.edusphere.service.SchoolClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/final-subject-grades")
public class FinalSubjectGradeController {

    private final GradingService gradingService;
    private final CurrentUserProvider currentUserProvider;
    private final SchoolClassService schoolClassService;

    public FinalSubjectGradeController(GradingService gradingService, CurrentUserProvider currentUserProvider, SchoolClassService schoolClassService) {
        this.gradingService = gradingService;
        this.currentUserProvider = currentUserProvider;
        this.schoolClassService = schoolClassService;
    }

    @GetMapping("/roster/{teachingAssignmentId}")
    public ResponseEntity<List<ClassGradeRosterEntry>> getClassRoster(@PathVariable Long teachingAssignmentId) {
        return ResponseEntity.ok(gradingService.getClassRoster(teachingAssignmentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinalSubjectGradeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(gradingService.getFinalGradeById(id)));
    }

    @GetMapping
    public ResponseEntity<FinalSubjectGradeResponse> get(
            @RequestParam Long studentId, @RequestParam Long teachingAssignmentId
    ) {
        return ResponseEntity.ok(toResponse(gradingService.getFinalGrade(studentId, teachingAssignmentId)));
    }

    @GetMapping("/cceg")
    public ResponseEntity<Double> getCceg(
            @RequestParam Long studentId, @RequestParam Long teachingAssignmentId
    ) {
        return ResponseEntity.ok(gradingService.calculateCceg(studentId, teachingAssignmentId));
    }

    @GetMapping("/cfe")
    public ResponseEntity<Double> getCfe(
            @RequestParam Long studentId, @RequestParam Long teachingAssignmentId
    ) {
        return ResponseEntity.ok(gradingService.calculateCfe(studentId, teachingAssignmentId));
    }

    @PostMapping("/project-grade")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<FinalSubjectGradeResponse> setProjectGrade(@RequestBody ProjectGradeRequest request) {
        FinalSubjectGrade finalSubjectGrade = gradingService.setProjectGrade(
                request.teachingAssignmentId(), request.studentId(), request.projectGrade()
        );
        return ResponseEntity.ok(toResponse(finalSubjectGrade));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<FinalSubjectGradeResponse> submit(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(gradingService.submitFinalGrade(id)));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinalSubjectGradeResponse> approve(@PathVariable Long id) {
        Long adminUserId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(toResponse(gradingService.approveFinalGrade(id, adminUserId)));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinalSubjectGradeResponse> reject(@PathVariable Long id) {
        Long adminUserId = currentUserProvider.getCurrentUserId();
        return ResponseEntity.ok(toResponse(gradingService.rejectFinalGrade(id, adminUserId)));
    }

    @GetMapping("/submitted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FinalSubjectGradeResponse>> getAllSubmitted() {
        return ResponseEntity.ok(gradingService.getAllSubmitted().stream().map(this::toResponse).toList());
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<List<SubjectClassRosterResponse>> getClassRosterAllSubjects(@PathVariable Long classId) {
        requireAdminOrSupervisor(classId);
        return ResponseEntity.ok(gradingService.getClassRosterAllSubjects(classId));
    }

    private void requireAdminOrSupervisor(Long classId) {
        User currentUser = currentUserProvider.getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) return;
        Teacher teacher = currentUserProvider.getCurrentTeacher();
        SchoolClass schoolClass = schoolClassService.getById(classId);
        if (schoolClass.getSupervisorTeacher() == null
                || !schoolClass.getSupervisorTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("Only the supervising teacher of this class (or an admin) can view this data.");
        }
    }

    private FinalSubjectGradeResponse toResponse(FinalSubjectGrade finalSubjectGrade) {
        return new FinalSubjectGradeResponse(
                finalSubjectGrade.getId(),
                finalSubjectGrade.getTeachingAssignment().getId(),
                finalSubjectGrade.getTeachingAssignment().getSchoolClass().getClassName(),
                finalSubjectGrade.getTeachingAssignment().getSubject().getSubjectName(),
                finalSubjectGrade.getStudent().getId(),
                finalSubjectGrade.getStudent().getFirstName(),
                finalSubjectGrade.getStudent().getLastName(),
                finalSubjectGrade.getProjectGrade(),
                finalSubjectGrade.getCceg(),
                finalSubjectGrade.getCfe(),
                finalSubjectGrade.getFinalGrade(),
                finalSubjectGrade.getStatus().name(),
                finalSubjectGrade.getSubmittedAt(),
                finalSubjectGrade.getApprovedAt()
        );
    }
}
