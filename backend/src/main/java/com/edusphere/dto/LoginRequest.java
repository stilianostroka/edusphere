package com.edusphere.dto;

public record LoginRequest(
        String email,
        String password
) {
}
