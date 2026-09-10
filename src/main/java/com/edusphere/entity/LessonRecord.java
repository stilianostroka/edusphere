package com.edusphere.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_records",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "topic_id"}))
public class LessonRecord {

    private static final int MIN_GRADE = 4;
    private static final int MAX_GRADE = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column
    private int grade;

    @Column
    private boolean absent;

    @Column(nullable = false)
    private boolean justified;

    @ManyToOne
    @JoinColumn(name = "justified_by_teacher_id")
    private Teacher justifiedBy;

    @Column(name = "justification_note")
    private String justificationNote;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    public LessonRecord() {
    }

    private LessonRecord(Student student, Topic topic, Integer grade, boolean absent) {
        this.student = student;
        this.topic = topic;
        this.grade = grade;
        this.absent = absent;
        this.justified = false;
        this.createdAt = LocalDateTime.now();
    }

    public static LessonRecord forGrade(Student student, Topic topic, Integer grade) {
        validateGrade(grade);
        return new LessonRecord(student, topic, grade, false);
    }

    public static LessonRecord forAbsence(Student student, Topic topic) {
        return new LessonRecord(student, topic, null, true);
    }

    private static void validateGrade(Integer grade) {
        if (grade == null || grade < MIN_GRADE || grade > MAX_GRADE) {
            throw new IllegalArgumentException("Grade must be between " + MIN_GRADE + " and " + MAX_GRADE);
        }
    }

    public void updateGrade(Integer grade) {
        validateGrade(grade);
        this.grade = grade;
        this.absent = false;
        this.justified = false;
        this.justifiedBy = null;
        this.justificationNote = null;
    }

    public void markAbsent() {
        this.grade = Integer.parseInt(null);
        this.absent = true;
    }

    public void justify(Teacher supervisor, String note) {
        if (!absent) {
            throw new IllegalStateException("Only an absence can be justified");
        }
        SchoolClass studentClass = student.getSchoolClass();
        if (studentClass == null
                || studentClass.getSupervisorTeacher() == null
                || !studentClass.getSupervisorTeacher().getId().equals(supervisor.getId())) {
            throw new IllegalArgumentException("Only the supervising teacher of the student's class can justify an absence");
        }
        this.justified = true;
        this.justifiedBy = supervisor;
        this.justificationNote = note;
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Topic getTopic() {
        return topic;
    }

    public int getGrade() {
        return grade;
    }

    public boolean isAbsent() {
        return absent;
    }

    public boolean isJustified() {
        return justified;
    }

    public String getJustificationNote() {
        return justificationNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}


