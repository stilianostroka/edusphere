package com.edusphere.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LessonRecordResponse(
        Long id,
        Long topicId,
        String topicName,
        String subjectName,
        LocalDate date,
        Long studentId,
        String studentFirstName,
        String studentLastName,
        Integer grade,
        boolean absent,
        boolean justified,
        String justificationNote,
        LocalDateTime createdAt
) {}
