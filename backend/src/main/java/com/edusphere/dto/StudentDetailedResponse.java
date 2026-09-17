package com.edusphere.dto;

import java.time.LocalDate;

public record StudentDetailedResponse(
        Long id,
        Long schoolClassId,
        String schoolName,
        String firstName,
        String lastName,
        String gender,
        String status,
        String personalId,
        LocalDate dateOfBirth,
        String address,
        LocalDate enrollmentDate
) {}
