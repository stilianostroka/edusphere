package com.edusphere.dto;

public record GradeChangeRequest(
        Long teacherId,
        Long lessonRecordId,
        Integer proposedGrade,
        String explanation
) {
}


