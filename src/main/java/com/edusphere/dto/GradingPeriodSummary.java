package com.edusphere.dto;

import java.time.LocalDate;

public record GradingPeriodSummary(
        Integer sequenceNumber,
        LocalDate startDate,
        LocalDate endDate
) {}