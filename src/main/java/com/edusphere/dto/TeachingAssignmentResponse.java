package com.edusphere.dto;

public record TeachingAssignmentResponse(
        Long id,
        Long schoolClassId,
        String schoolClassName,
        Long subjectId,
        String subjectName,
        String subjectCode,
        Long teacherId,
        String teacherName
) {}
