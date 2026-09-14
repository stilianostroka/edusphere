package com.edusphere.dto;

public record LessonRecordResponse(
        Long id,
        Long topicId,
        Long studentId,
        String studentFirstName,
        String studentLastName,
        Integer grade,
        boolean absent,
        boolean justified,
        String justificationNote
) {}