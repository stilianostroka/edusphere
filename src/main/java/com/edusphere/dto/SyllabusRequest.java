package com.edusphere.dto;

public record SyllabusRequest(
        Long teachingAssignmentId,
        String fileUrl,
        String originalFileName,
        String contentType
) {}

