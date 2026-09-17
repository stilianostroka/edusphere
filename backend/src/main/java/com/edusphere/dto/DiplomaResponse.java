package com.edusphere.dto;

import java.time.LocalDateTime;

public record DiplomaResponse(
        Long id,
        Long studentId,
        String studentName,
        Long academicYearId,
        String academicYearLabel,
        String fileUrl,
        LocalDateTime generatedAt
) {}
