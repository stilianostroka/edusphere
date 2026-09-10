package com.edusphere.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "syllabi")
public class Syllabus {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "teaching_assignment_id", nullable = false, unique = true)
    private TeachingAssignment teachingAssignment;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    protected Syllabus() {
    }

    public Syllabus(TeachingAssignment teachingAssignment, String fileUrl, String originalFileName, String contentType) {
        validateContentType(contentType);
        this.teachingAssignment = teachingAssignment;
        this.fileUrl = fileUrl;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.uploadedAt = LocalDateTime.now();
    }

    private static void validateContentType(String contentType) {
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Syllabus must be a PDF or Word document");
        }
    }

    public void replace(String newFileUrl, String newOriginalFileName, String newContentType) {
        validateContentType(newContentType);
        this.fileUrl = newFileUrl;
        this.originalFileName = newOriginalFileName;
        this.contentType = newContentType;
        this.uploadedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public TeachingAssignment getTeachingAssignment() {
        return teachingAssignment;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}


