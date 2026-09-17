package com.edusphere.dto;

public record ResetPasswordRequest(
        String token,
         String newPassword
) {}