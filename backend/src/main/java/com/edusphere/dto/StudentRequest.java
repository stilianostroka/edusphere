package com.edusphere.dto;

import java.time.LocalDate;

public record StudentRequest(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String personalId,
        String gender,
        String address
) {}
