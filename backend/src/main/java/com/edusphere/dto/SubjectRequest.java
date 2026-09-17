package com.edusphere.dto;

public record SubjectRequest(
        String subjectName,
        String code,
        Integer totalHours,
        Integer programYear
) {}
