package com.edusphere.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "timetable_slots",
        uniqueConstraints = @UniqueConstraint(columnNames = {"teaching_assignment_id", "day_of_week", "start_time"})
)
public class TimetableSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teaching_assignment_id", nullable = false)
    private TeachingAssignment teachingAssignment;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    protected TimetableSlot() {
    }

    public TimetableSlot(TeachingAssignment teachingAssignment, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        validateTimes(startTime, endTime);
        this.teachingAssignment = teachingAssignment;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private static void validateTimes(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null || !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }

    public void reschedule(DayOfWeek newDayOfWeek, LocalTime newStartTime, LocalTime newEndTime) {
        validateTimes(newStartTime, newEndTime);
        this.dayOfWeek = newDayOfWeek;
        this.startTime = newStartTime;
        this.endTime = newEndTime;
    }

    public Long getId() {
        return id;
    }

    public TeachingAssignment getTeachingAssignment() {
        return teachingAssignment;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}

