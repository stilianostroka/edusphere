package com.edusphere.dto;

import java.time.LocalDate;

public record ParentMeetingRequest(
        Long classId,
        LocalDate meetingTime,
        String topic
) {
}
