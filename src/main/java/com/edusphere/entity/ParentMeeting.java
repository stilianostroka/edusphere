package com.edusphere.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "parent_meetings")
public class ParentMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @ManyToOne
    @JoinColumn(name = "recorded_by_teacher_id", nullable = false)
    private Teacher recordedBy;

    @Column(name = "meeting_datetime", nullable = false)
    private LocalDateTime meetingDateTime;

    @Column(name = "topics_discussed", nullable = false)
    private String topicsDiscussed;

    protected ParentMeeting() {
    }

    public ParentMeeting(SchoolClass schoolClass, Teacher recordedBy, LocalDateTime meetingDateTime, String topicsDiscussed) {
        if (schoolClass.getSupervisorTeacher() == null || !schoolClass.getSupervisorTeacher().getId().equals(recordedBy.getId())) {
            throw new IllegalArgumentException("Only the class's supervising teacher can record a parent meeting");
        }
        if (topicsDiscussed == null || topicsDiscussed.isBlank()) {
            throw new IllegalArgumentException("Topics discussed must be provided");
        }
        this.schoolClass = schoolClass;
        this.recordedBy = recordedBy;
        this.meetingDateTime = meetingDateTime;
        this.topicsDiscussed = topicsDiscussed;
    }

    public void setMeetingDateTime(LocalDateTime meetingDateTime) {
        this.meetingDateTime = meetingDateTime;
    }

    public void setTopicsDiscussed(String topicsDiscussed) {
        if (topicsDiscussed == null || topicsDiscussed.isBlank()) {
            throw new IllegalArgumentException("Topics discussed must be provided");
        }
        this.topicsDiscussed = topicsDiscussed;
    }

    public Long getId() {
        return id;
    }

    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    public Teacher getRecordedBy() {
        return recordedBy;
    }

    public LocalDateTime getMeetingDateTime() {
        return meetingDateTime;
    }

    public String getTopicsDiscussed() {
        return topicsDiscussed;
    }
}