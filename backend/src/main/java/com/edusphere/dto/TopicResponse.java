package com.edusphere.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TopicResponse(
        Long id,
        Long teachingAssignmentId,
        String schoolClassName,
        String subjectName,
        LocalDate date,
        String topicName,
        String description,
        LocalDateTime createdAt
) {
}
