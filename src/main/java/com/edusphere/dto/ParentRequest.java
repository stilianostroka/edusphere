package com.edusphere.dto;

public record ParentRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        String gender,
        String address,
        String phoneNumber
) {}
