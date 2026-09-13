package com.edusphere.dto;

public record StudentResponse(
        Long id,
        Long schoolClassId,
        String className,
        String firstName,
        String lastName,
        String status
) {
}
