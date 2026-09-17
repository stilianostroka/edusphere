package com.edusphere.dto;

import java.time.LocalDate;

public record GradingPeriodSummary(
        Long id,
        Integer sequenceNumber,
        LocalDate startDate,
        LocalDate endDate
) {}
