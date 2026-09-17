package com.edusphere.dto;

import java.time.LocalDate;

public record TopicRequest(
        Long teachingAssignmentId,
        LocalDate date,
        String topicName,
        String description
) {}

