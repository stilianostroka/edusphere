package com.edusphere.dto;

public record TeacherResponse(
        Long teacherId,
        String teacherName,
        String teacherSurname,
        String teacherEmail
) {
}
