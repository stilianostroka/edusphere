package com.edusphere.dto;

import java.time.LocalDateTime;

public record SyllabusResponse(
        Long id,
        Long teachingAssignmentId,
        String schoolClassName,
        String subjectName,
        String fileUrl,
        String originalFileName,
        String contentType,
        LocalDateTime uploadedAt
) {}