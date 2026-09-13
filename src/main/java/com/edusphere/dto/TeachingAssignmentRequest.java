package com.edusphere.dto;

public record TeachingAssignmentRequest(
        Long classId,
        Long subjectId,
        Long teacherId
) {}
