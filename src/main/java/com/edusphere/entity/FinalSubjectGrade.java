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

import java.time.LocalDateTime;

@Entity
@Table(name = "final_subject_grades", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "teaching_assignment_id"})
)
public class FinalSubjectGrade {

    private static final int MIN_GRADE = 4;
    private static final int MAX_GRADE = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teaching_assignment_id", nullable = false)
    private TeachingAssignment teachingAssignment;

    @Column(name = "project_grade")
    private Integer projectGrade;

    @Column(name = "cceg")
    private Integer cceg;

    @Column(name = "cfe")
    private Integer cfe;

    @Column(name = "final_grade")
    private Integer finalGrade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinalGradeStatus status;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne
    @JoinColumn(name = "approved_by_user_id")
    private User approvedBy;

    protected FinalSubjectGrade() {
    }

    public FinalSubjectGrade(Student student, TeachingAssignment teachingAssignment) {
        this.student = student;
        this.teachingAssignment = teachingAssignment;
        this.status = FinalGradeStatus.DRAFT;
    }

    public void setProjectGrade(Integer projectGrade) {
        requireStatus(FinalGradeStatus.DRAFT, "Project grade can only be set while in DRAFT");
        if (projectGrade == null || projectGrade < MIN_GRADE || projectGrade > MAX_GRADE) {
            throw new IllegalArgumentException("Grade must be between " + MIN_GRADE + " and " + MAX_GRADE);
        }
        this.projectGrade = projectGrade;
    }

    public void submit(Integer cceg, Integer cfe) {
        requireStatus(FinalGradeStatus.DRAFT, "Only a DRAFT grade can be submitted");
        if (projectGrade == null) {
            throw new IllegalStateException("Project grade must be set before submitting");
        }
        if (cceg == null || cfe == null) {
            throw new IllegalArgumentException("CCEG and CFE must both be provided to submit");
        }
        this.cceg = cceg;
        this.cfe = cfe;
        this.finalGrade = (int) Math.round((cceg + cfe + projectGrade) / 3.0);
        this.status = FinalGradeStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
    }

    public void approve(User admin) {
        requireStatus(FinalGradeStatus.SUBMITTED, "Only a SUBMITTED grade can be approved");
        this.status = FinalGradeStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.approvedBy = admin;
    }

    public void reject() {
        requireStatus(FinalGradeStatus.SUBMITTED, "Only a SUBMITTED grade can be rejected");
        this.status = FinalGradeStatus.DRAFT;
        this.submittedAt = null;
        this.cceg = null;
        this.cfe = null;
        this.finalGrade = null;
    }

    private void requireStatus(FinalGradeStatus required, String message) {
        if (this.status != required) {
            throw new IllegalStateException(message + " (current status: " + this.status + ")");
        }
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public TeachingAssignment getTeachingAssignment() {
        return teachingAssignment;
    }

    public Integer getProjectGrade() {
        return projectGrade;
    }

    public Integer getCceg() {
        return cceg;
    }

    public Integer getCfe() {
        return cfe;
    }

    public Integer getFinalGrade() {
        return finalGrade;
    }

    public FinalGradeStatus getStatus() {
        return status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public User getApprovedBy() {
        return approvedBy;
    }
}