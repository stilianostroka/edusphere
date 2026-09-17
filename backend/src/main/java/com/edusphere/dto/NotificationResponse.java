package com.edusphere.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long sentByTeacherId,
        String sentByTeacherName,
        Long targetStudentId,
        String targetStudentName,
        Long targetClassId,
        String targetClassName,
        String message,
        LocalDateTime sentAt
) {}