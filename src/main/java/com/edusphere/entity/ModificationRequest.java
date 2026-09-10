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

import java.time.LocalDateTime;

@Entity
@Table(name = "modification_requests")
public class ModificationRequest {

    private static final int MIN_GRADE = 4;
    private static final int MAX_GRADE = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requested_by_teacher_id", nullable = false)
    private Teacher requestedBy;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "lesson_record_id")
    private LessonRecord lessonRecord;

    @Column(name = "proposed_grade")
    private Integer proposedGrade;

    @Column(name = "proposed_absent", nullable = false)
    private boolean proposedAbsent;

    @Column(name = "proposed_topic_name")
    private String proposedTopicName;

    @Column(name = "proposed_description")
    private String proposedDescription;

    @Column(nullable = false)
    private String explanation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModificationRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    protected ModificationRequest() {
    }

    private ModificationRequest(
            Teacher requestedBy,
            Topic topic,
            LessonRecord lessonRecord,
            Integer proposedGrade,
            boolean proposedAbsent,
            String proposedTopicName,
            String proposedDescription,
            String explanation
    ) {
        if (explanation == null || explanation.isBlank()) {
            throw new IllegalArgumentException("An explanation is required");
        }
        this.requestedBy = requestedBy;
        this.topic = topic;
        this.lessonRecord = lessonRecord;
        this.proposedGrade = proposedGrade;
        this.proposedAbsent = proposedAbsent;
        this.proposedTopicName = proposedTopicName;
        this.proposedDescription = proposedDescription;
        this.explanation = explanation;
        this.status = ModificationRequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public static ModificationRequest proposeGradeChange(Teacher requestedBy, LessonRecord lessonRecord, Integer proposedGrade, String explanation) {
        if (proposedGrade == null || proposedGrade < MIN_GRADE || proposedGrade > MAX_GRADE) {
            throw new IllegalArgumentException("Grade must be between " + MIN_GRADE + " and " + MAX_GRADE);
        }
        return new ModificationRequest(requestedBy, null, lessonRecord, proposedGrade, false, null, null, explanation);
    }

    public static ModificationRequest proposeAbsenceChange(Teacher requestedBy, LessonRecord lessonRecord, String explanation) {
        return new ModificationRequest(requestedBy, null, lessonRecord, null, true, null, null, explanation);
    }

    public static ModificationRequest proposeTopicChange(Teacher requestedBy, Topic topic, String proposedTopicName, String proposedDescription, String explanation) {
        boolean hasName = proposedTopicName != null && !proposedTopicName.isBlank();
        boolean hasDescription = proposedDescription != null && !proposedDescription.isBlank();
        if (!hasName && !hasDescription) {
            throw new IllegalArgumentException("Must propose a new topic name or description");
        }
        return new ModificationRequest(requestedBy, topic, null, null, false, proposedTopicName, proposedDescription, explanation);
    }

    public void approve() {
        requireStatus(ModificationRequestStatus.PENDING, "Only a PENDING request can be approved");
        this.status = ModificationRequestStatus.APPROVED;
        this.reviewedAt = LocalDateTime.now();
    }

    public void reject() {
        requireStatus(ModificationRequestStatus.PENDING, "Only a PENDING request can be rejected");
        this.status = ModificationRequestStatus.REJECTED;
        this.reviewedAt = LocalDateTime.now();
    }

    private void requireStatus(ModificationRequestStatus required, String message) {
        if (this.status != required) {
            throw new IllegalStateException(message + " (current status: " + this.status + ")");
        }
    }

    public boolean isForTopic() {
        return topic != null;
    }

    public boolean isForLessonRecord() {
        return lessonRecord != null;
    }

    public Long getId() {
        return id;
    }

    public Teacher getRequestedBy() {
        return requestedBy;
    }

    public Topic getTopic() {
        return topic;
    }

    public LessonRecord getLessonRecord() {
        return lessonRecord;
    }

    public Integer getProposedGrade() {
        return proposedGrade;
    }

    public boolean isProposedAbsent() {
        return proposedAbsent;
    }

    public String getProposedTopicName() {
        return proposedTopicName;
    }

    public String getProposedDescription() {
        return proposedDescription;
    }

    public String getExplanation() {
        return explanation;
    }

    public ModificationRequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
}