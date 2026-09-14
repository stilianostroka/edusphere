package com.edusphere.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record TimetableSlotRequest(
        Long adminUserId,
        Long teachingAssignmentId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}