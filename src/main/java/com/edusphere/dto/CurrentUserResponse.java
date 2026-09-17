package com.edusphere.dto;

public record CurrentUserResponse(
        Long userId,
        Long profileId,
        String email,
        String role,
        String fullName
) {}
