package com.edusphere.dto;

public record ExamGradeChangeRequest(
        Long semesterExamGradeId,
        Integer proposedExamGrade,
        String explanation
) {}