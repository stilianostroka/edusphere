package com.edusphere.dto;

public record SchoolClassResponse(
        Long id,
        String className,
        String classYear,
        Integer maxStudents,
        String academicYearLabel,
        Long supervisorId,
        String supervisorName
) {}
