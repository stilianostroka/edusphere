package com.edusphere.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "semester_exam_grades",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "teaching_assignment_id", "grading_period_id"}))
public class SemesterExamGrade {

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

    @ManyToOne
    @JoinColumn(name = "grading_period_id", nullable = false)
    private GradingPeriod gradingPeriod;

    @Column(name = "exam_grade", nullable = false)
    private Integer examGrade;

    public SemesterExamGrade() {

    }
    public SemesterExamGrade(Student student, TeachingAssignment teachingAssignment, GradingPeriod gradingPeriod, Integer examGrade) {
        validateGrade(examGrade);
        this.student = student;
        this.teachingAssignment = teachingAssignment;
        this.gradingPeriod = gradingPeriod;
        this.examGrade = examGrade;
    }

    private static void validateGrade(Integer grade) {
        if (grade == null || grade < MIN_GRADE || grade > MAX_GRADE) {
            throw new IllegalArgumentException("Grade must be between " + MIN_GRADE + " and " + MAX_GRADE);
        }
    }

    public void setExamGrade(Integer examGrade) {
        validateGrade(examGrade);
        this.examGrade = examGrade;
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

    public GradingPeriod getGradingPeriod() {
        return gradingPeriod;
    }

    public Integer getExamGrade() {
        return examGrade;
    }
}


