package com.edusphere.dto;

public record ExamGradeChangeRequest(
        Long teacherId,
        Long semesterExamGradeId,
        Integer proposedExamGrade,
        String explanation
) {}