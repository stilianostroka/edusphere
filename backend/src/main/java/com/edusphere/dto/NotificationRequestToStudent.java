package com.edusphere.dto;

public record NotificationRequestToStudent(
        Long studentId,
        String message
) {
}
