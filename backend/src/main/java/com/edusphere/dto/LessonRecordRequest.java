package com.edusphere.dto;

public record LessonRecordRequest(
        Long topicId,
        Long studentId,
        Integer grade
) {}