package com.edusphere.controller;

import com.edusphere.dto.ExamGradeRequest;
import com.edusphere.dto.SemesterExamGradeResponse;
import com.edusphere.entity.SemesterExamGrade;
import com.edusphere.service.GradingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semester-exam-grades")
public class SemesterExamGradeController {

    private final GradingService gradingService;

    public SemesterExamGradeController(GradingService gradingService) {
        this.gradingService = gradingService;
    }

    @PostMapping
    public ResponseEntity<SemesterExamGradeResponse> record(@RequestBody ExamGradeRequest request) {
        SemesterExamGrade semesterExamGrade = gradingService.recordExamGrade(
                request.teachingAssignmentId(), request.studentId(), request.gradingPeriodId(), request.examGrade()
        );
        return ResponseEntity.ok(toResponse(semesterExamGrade));
    }

    @GetMapping("/student/{studentId}/teaching-assignment/{teachingAssignmentId}")
    public ResponseEntity<List<SemesterExamGradeResponse>> getForStudentAndAssignment(
            @PathVariable Long studentId, @PathVariable Long teachingAssignmentId
    ) {
        List<SemesterExamGradeResponse> response = gradingService
                .getExamGrades(studentId, teachingAssignmentId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    private SemesterExamGradeResponse toResponse(SemesterExamGrade semesterExamGrade) {
        return new SemesterExamGradeResponse(
                semesterExamGrade.getId(),
                semesterExamGrade.getTeachingAssignment().getId(),
                semesterExamGrade.getTeachingAssignment().getSchoolClass().getClassName(),
                semesterExamGrade.getTeachingAssignment().getSubject().getSubjectName(),
                semesterExamGrade.getStudent().getId(),
                semesterExamGrade.getStudent().getFirstName(),
                semesterExamGrade.getStudent().getLastName(),
                semesterExamGrade.getGradingPeriod().getId(),
                semesterExamGrade.getGradingPeriod().getSequenceNumber(),
                semesterExamGrade.getExamGrade(),
                semesterExamGrade.getCreatedAt()
        );
    }
}