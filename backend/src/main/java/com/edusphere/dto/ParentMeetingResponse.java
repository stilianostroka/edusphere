package com.edusphere.dto;

import java.time.LocalDate;

public record ParentMeetingResponse(
        Long id,
        Long classId,
        Long teacherId,
        LocalDate meetingTime,
        String Topic
) {
}
