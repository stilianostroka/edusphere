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
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sent_by_teacher_id", nullable = false)
    private Teacher sentBy;

    @ManyToOne
    @JoinColumn(name = "target_student_id")
    private Student targetStudent;

    @ManyToOne
    @JoinColumn(name = "target_class_id")
    private SchoolClass targetClass;

    @Column(nullable = false)
    private String message;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    protected Notification() {
    }

    private Notification(Teacher sentBy, Student targetStudent, SchoolClass targetClass, String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message must be provided");
        }
        this.sentBy = sentBy;
        this.targetStudent = targetStudent;
        this.targetClass = targetClass;
        this.message = message;
        this.sentAt = LocalDateTime.now();
    }

    public static Notification toParentsOfStudent(Teacher sentBy, Student targetStudent, String message) {
        SchoolClass studentClass = targetStudent.getSchoolClass();
        if (studentClass == null
                || studentClass.getSupervisorTeacher() == null
                || !studentClass.getSupervisorTeacher().getId().equals(sentBy.getId())) {
            throw new IllegalArgumentException("Only the student's class supervisor can send this notification");
        }
        return new Notification(sentBy, targetStudent, null, message);
    }

    public static Notification toAllParentsOfClass(Teacher sentBy, SchoolClass targetClass, String message) {
        if (targetClass.getSupervisorTeacher() == null
                || !targetClass.getSupervisorTeacher().getId().equals(sentBy.getId())) {
            throw new IllegalArgumentException("Only the class's supervisor can send this notification");
        }
        return new Notification(sentBy, null, targetClass, message);
    }

    public boolean isForStudent() {
        return targetStudent != null;
    }

    public boolean isForClass() {
        return targetClass != null;
    }

    public Long getId() {
        return id;
    }

    public Teacher getSentBy() {
        return sentBy;
    }

    public Student getTargetStudent() {
        return targetStudent;
    }

    public SchoolClass getTargetClass() {
        return targetClass;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }
}