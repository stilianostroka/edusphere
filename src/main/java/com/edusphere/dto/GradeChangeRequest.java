package com.edusphere.dto;

public record GradeChangeRequest(
        Long lessonRecordId,
        Integer proposedGrade,
        String explanation
) {
}


