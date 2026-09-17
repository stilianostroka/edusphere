package com.edusphere.dto;

public record SchoolClassRequest(
        Long academicYearId,
        String className,
        String classYear,
        Integer maxStudents
) {
}
