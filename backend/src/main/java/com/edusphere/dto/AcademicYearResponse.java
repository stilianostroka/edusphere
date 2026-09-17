package com.edusphere.dto;

import java.time.LocalDate;

public record AcademicYearResponse(
        Long id,
        String label,
        LocalDate startDate,
        LocalDate endDate,
        boolean active
) {}
