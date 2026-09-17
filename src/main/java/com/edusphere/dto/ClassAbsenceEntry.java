package com.edusphere.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClassAbsenceEntry(
        Long id,
        Long studentId,
        String studentFirstName,
        String studentLastName,
        String subjectName,
        String topicName,
        LocalDate date,
        boolean justified,
        String justificationNote,
        LocalDateTime createdAt
) {}