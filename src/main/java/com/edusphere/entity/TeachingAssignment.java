package com.edusphere.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "teaching_assignments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"class_id", "subject_id"}))
public class TeachingAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    public TeachingAssignment() {
    }

    public TeachingAssignment(SchoolClass schoolClass, Subject subject, Teacher teacher) {
        this.schoolClass = schoolClass;
        this.subject = subject;
        this.teacher = teacher;
    }

    public Long getId() {
        return id;
    }

    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    public Subject getSubject() {
        return subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}