package com.edusphere.dto;

public record ExamGradeRequest(
        Long teachingAssignmentId,
        Long studentId,
        Long gradingPeriodId,
        Integer examGrade
) {}

