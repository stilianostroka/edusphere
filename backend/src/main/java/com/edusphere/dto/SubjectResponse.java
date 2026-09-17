package com.edusphere.dto;

public record SubjectResponse(
        Long id,
        String subjectName,
        String code,
        Integer totalHours,
        Integer programYear
) {
}
