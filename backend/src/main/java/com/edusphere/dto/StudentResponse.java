package com.edusphere.dto;

public record StudentResponse(
        Long id,
        Long schoolClassId,
        String className,
        String firstName,
        String lastName,
        String status,
        String personalId,
        java.time.LocalDate dateOfBirth,
        String gender,
        String address,
        java.time.LocalDate enrollmentDate
) {
}
