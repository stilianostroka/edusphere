package com.edusphere.dto;

import java.util.List;

public record ClassGradeRosterEntry(
        Long studentId,
        String studentFirstName,
        String studentLastName,
        List<PeriodGradeSummary> periods,
        Integer cceg,
        Integer cfe,
        Integer projectGrade,
        Integer finalGrade,
        String status
) {}