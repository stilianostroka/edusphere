package com.edusphere.dto;

public record MarkAbsentRequest(
        Long topicId,
        Long studentId
) {}