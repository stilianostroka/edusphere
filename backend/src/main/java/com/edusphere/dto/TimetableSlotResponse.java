package com.edusphere.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record TimetableSlotResponse (
        Long id,
        Long teachingAssignmentId,
        String schoolClassName,
        String subjectName,
        String teacherName,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}
