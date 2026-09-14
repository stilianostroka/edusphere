package com.edusphere.dto;

import java.time.LocalDateTime;

public record SemesterExamGradeResponse(
        Long id,
        Long teachingAssignmentId,
        String schoolClassName,
        String subjectName,
        Long studentId,
        String studentFirstName,
        String studentLastName,
        Long gradingPeriodId,
        Integer gradingPeriodSequenceNumber,
        Integer examGrade,
        LocalDateTime createdAt
) {}