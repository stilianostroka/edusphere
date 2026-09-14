package com.edusphere.dto;

public record NotificationRequestToClass(
        Long teacherId,
        Long classId,
        String message
) {
}


