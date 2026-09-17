package com.edusphere.dto;

import java.time.LocalDateTime;

public record FinalSubjectGradeResponse(
        Long id,
        Long teachingAssignmentId,
        String schoolClassName,
        String subjectName,
        Long studentId,
        String studentFirstName,
        String studentLastName,
        Integer projectGrade,
        Integer cceg,
        Integer cfe,
        Integer finalGrade,
        String status,
        LocalDateTime submittedAt,
        LocalDateTime approvedAt
) {}