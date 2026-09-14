package com.edusphere.dto;

public record ProjectGradeRequest(
        Long teachingAssignmentId,
        Long studentId,
        Integer projectGrade
) {}