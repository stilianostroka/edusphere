package com.edusphere.dto;

public record AbsenceChangeRequest(
        Long lessonRecordId,
        String explanation
) {}
