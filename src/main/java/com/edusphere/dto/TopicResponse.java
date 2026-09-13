package com.edusphere.dto;

import java.time.LocalDate;

public record TopicResponse(
        LocalDate date,
        String topicName,
        String description
) {
}
