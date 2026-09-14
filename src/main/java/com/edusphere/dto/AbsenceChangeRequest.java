package com.edusphere.dto;

public record AbsenceChangeRequest(
        Long teacherId,
        Long lessonRecordId,
        String explanation
) {}
