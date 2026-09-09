package com.edusphere.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "classes")
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne
    @JoinColumn(name = "supervisor_teacher_id", nullable = false)
    private Teacher supervisorTeacher;

    @Column(name = "class_name",nullable = false)
    private String className;

    @Column(name = "class_year",nullable = false)
    private String classYear;

    @Column(name = "max_students", nullable = false)
    private Integer maxStudents;

    public SchoolClass() {
    }

    public SchoolClass(AcademicYear academicYear, Teacher supervisorTeacher, String className, String classYear, Integer maxStudents) {
        this.academicYear = academicYear;
        this.className = className;
        this.classYear = classYear;
        this.maxStudents = maxStudents;
    }

    public Long getId() {
        return id;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AcademicYear academicYear) {
        this.academicYear = academicYear;
    }

    public Teacher getSupervisorTeacher() {
        return supervisorTeacher;
    }

    public void setSupervisorTeacher(Teacher supervisorTeacher) {
        this.supervisorTeacher = supervisorTeacher;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassYear() {
        return classYear;
    }

    public void setClassYear(String classYear) {
        this.classYear = classYear;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }
}


