package com.edusphere.dto;

import java.time.LocalDateTime;

public record ModificationResponse(
        Long id,
        Long requestedByTeacherId,
        String requestedByTeacherName,
        Long topicId,
        Long lessonRecordId,
        Long semesterExamGradeId,
        Integer proposedGrade,
        boolean proposedAbsent,
        String proposedTopicName,
        String proposedDescription,
        Integer proposedExamGrade,
        String explanation,
        String status,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt
) {}