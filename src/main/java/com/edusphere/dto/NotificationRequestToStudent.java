package com.edusphere.dto;

public record NotificationRequestToStudent(
        Long teacherId,
        Long studentId,
        String message
) {
}
