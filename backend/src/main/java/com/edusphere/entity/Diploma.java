package com.edusphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "diplomas",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "academic_year_id"}))
public class Diploma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    protected Diploma() {}

    public Diploma(Student student, AcademicYear academicYear, String fileUrl) {
        this.student = student;
        this.academicYear = academicYear;
        this.fileUrl = fileUrl;
        this.generatedAt = LocalDateTime.now();
    }

    public void regenerate(String fileUrl) {
        this.fileUrl = fileUrl;
        this.generatedAt = LocalDateTime.now();
    }
    public Long getId() {
        return id;
    }
    public Student getStudent() {
        return student;
    }
    public AcademicYear getAcademicYear() {
        return academicYear;
    }
    public String getFileUrl() {
        return fileUrl;
    }
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
}
