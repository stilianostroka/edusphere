package com.edusphere.dto;

public record TeacherRequest(
        String name,
        String surname,
        String email,
        String password
) {
}
