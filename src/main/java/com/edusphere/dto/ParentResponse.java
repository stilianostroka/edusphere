package com.edusphere.dto;

public record ParentResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String gender,
        String address,
        String phoneNumber
) {}