package com.edusphere.dto;

public record AdminCreateRequest(
        String email,
        String password
) {}